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


import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import net.phalanxx.cdiext.util.AnnotationUtil;

/**
 * A portable extension base class called by CDI on startup to find {@link ProducedByFactory}
 * annotated classes. For each class found the extension generates a special bean responsible for
 * instantiation of the annotated class. Sometimes it's difficult to write a producer method because
 * the type to be created is not known at compile time. So someone can write a generic factory for a
 * specific class.
 *
 * @author rbachlec
 */
public class FactoryExtension implements Extension {

    private final Set<AnnotatedType<Object>> toBeProducedByFactory = new HashSet<>();

    void processAnnotatedType(@Observes final ProcessAnnotatedType<Object> pat, final BeanManager beanManager) {
        final AnnotatedType<Object> annotatedType = pat.getAnnotatedType();
        if (AnnotationUtil.isAnnotationPresent(annotatedType, beanManager, ProducedByFactory.class)) {
            toBeProducedByFactory.add(annotatedType);
            pat.veto();
        }
    }

    void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, final BeanManager beanManager) {
        for (final AnnotatedType<Object> annotatedType : toBeProducedByFactory) {
            InjectionTarget<Object> injectionTarget = beanManager.createInjectionTarget(annotatedType);

            Bean<Object> bean = new FactoryProducedBean<>(annotatedType, injectionTarget, beanManager);
            abd.addBean(bean);
        }

        toBeProducedByFactory.clear();
    }

}
