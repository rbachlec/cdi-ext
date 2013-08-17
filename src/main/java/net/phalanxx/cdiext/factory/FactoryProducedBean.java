package net.phalanxx.cdiext.factory;

/*
 * ---LICENSE_BEGIN---
 * cdi-ext - Some extensions for CDI
 * ---
 * Copyright (C) 2013 Roland Bachlechner
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---LICENSE_END---
 */


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

import net.phalanxx.cdiext.util.AnnotationUtil;
import net.phalanxx.cdiext.util.BeanManagerUtil;

/**
 * Bean produced by the FactoryExtension.
 *
 * @author rbachlec
 * @param <T> type to create {@link Bean} instance
 */
public class FactoryProducedBean<T> implements Bean<T> {

    private final AnnotatedType<T> annotatedType;
    private final InjectionTarget<T> injectionTarget;
    private final BeanManager beanManager;

    FactoryProducedBean(final AnnotatedType<T> annotatedType, final InjectionTarget<T> injectionTarget,
                  final BeanManager beanManager) {
        this.annotatedType = annotatedType;
        this.injectionTarget = injectionTarget;
        this.beanManager = beanManager;
    }

    @Override
    public final Set<Type> getTypes() {
        return annotatedType.getTypeClosure();
    }

    @Override
    public final Set<Annotation> getQualifiers() {
        Set<Annotation> qualifiers = new HashSet<>();
        qualifiers.add(new AnnotationLiteral<Any>() {});
        qualifiers.add(new AnnotationLiteral<Default>() {});
        qualifiers.addAll(AnnotationUtil.getQualifiers(annotatedType, beanManager));

        return qualifiers;
    }

    @Override
    public final Class<? extends Annotation> getScope() {
        return AnnotationUtil.getScope(annotatedType, beanManager).annotationType();
    }

    @Override
    public final String getName() {
        String name = null;

        Named namedAnnotation = AnnotationUtil.getAnnotation(annotatedType, beanManager, Named.class);
        if (namedAnnotation != null) {
            name = namedAnnotation.value();
        }

        return name;
    }

    @Override
    public final Set<Class<? extends Annotation>> getStereotypes() {
        Set<Class<? extends Annotation>> stereotypeClasses = new HashSet<>();

        Set<Annotation> stereotypes = AnnotationUtil.getStereotypes(annotatedType, beanManager);
        for (Annotation stereotype : stereotypes) {
            stereotypeClasses.add(stereotype.annotationType());
        }

        return stereotypeClasses;
    }

    @Override
    public final Class<T> getBeanClass() {
        return annotatedType.getJavaClass();
    }

    @Override
    public final boolean isAlternative() {
        return false;
    }

    @Override
    public final boolean isNullable() {
        return false;
    }

    @Override
    public final Set<InjectionPoint> getInjectionPoints() {
        return injectionTarget.getInjectionPoints();
    }

    @Override
    public final T create(final CreationalContext<T> creationalContext) {
        ProducedByFactory annotation = AnnotationUtil.getAnnotation(annotatedType, beanManager, ProducedByFactory.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Class " + annotatedType.getClass() +
                                               " does not define a factory class.");
        }

        Factory factory = BeanManagerUtil.getContextualInstance(beanManager, annotation.factory());

        T instance = factory.createInstance(annotatedType.getJavaClass());
        injectionTarget.inject(instance, creationalContext);
        injectionTarget.postConstruct(instance);

        return instance;
    }

    @Override
    public final void destroy(final T instance, final CreationalContext<T> creationalContext) {
        injectionTarget.preDestroy(instance);
        injectionTarget.dispose(instance);
        creationalContext.release();
    }

}
