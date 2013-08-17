package net.phalanxx.cdiext.util;

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
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;

/**
 * Bean manager related util methods. They should be used rarely.
 *
 * @author rbachlec
 */
@ApplicationScoped
public class BeanManagerUtil {

    @Inject private BeanManager beanManager;

    /**
     * Get a single CDI managed instance of a specific class. Return only the first result if multiple beans are
     * available.
     *
     * @param <T> type of the contextual instance to return
     * @param type The class for which to return an instance.
     * @param qualifiers List of qualifier annotations
     * @return The managed instance, or null if none could be provided.
     */
    public <T> T getContextualInstance(final Class<T> type, final Annotation... qualifiers) {
        return getContextualInstance(beanManager, type, qualifiers);
    }

    /**
     * Get a single CDI managed instance of a specific class. Return only the first result if multiple beans are
     * available.
     * <p/>
     * <b>NOTE:</b> Using this method should be avoided if container provided injection is available.
     *
     * @param <T> type of the contextual instance to return
     * @param beanManager The bean manager with which to perform the lookup.
     * @param type The class for which to return an instance.
     * @param qualifiers List of qualifier annotations
     * @return The managed instance, or null if none could be provided.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getContextualInstance(final BeanManager beanManager, final Class<T> type,
                                              final Annotation... qualifiers) {
        T result = null;
        Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type, qualifiers));
        if (bean != null) {
            CreationalContext<T> context = beanManager.createCreationalContext(bean);
            result = (T) beanManager.getReference(bean, type, context);
        }
        return result;
    }

    /**
     * Get all CDI managed instances of a specific class. Return results in a
     * {@link List} in no specific order.
     *
     * @param <T> type of the contextual instance to return
     * @param type The class for which to return instances.
     * @param qualifiers List of qualifier annotations
     * @return list of contextual instances matching the parameters
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getContextualInstances(final Class<T> type, final Annotation... qualifiers) {
        List<T> result = new ArrayList<>();
        for (Bean<?> bean : beanManager.getBeans(type, qualifiers)) {
            CreationalContext<T> context = (CreationalContext<T>) beanManager.createCreationalContext(bean);
            result.add((T) beanManager.getReference(bean, type, context));
        }
        return result;
    }

    /**
     * Does injection magic on the given object.
     *
     * @param <T> type of object to do injection for
     * @param object  object to do injection for
     */
    public <T> void doInjectionsForUnmanagedObject(final T object) {
        doInjectionsForUnmanagedObject(beanManager, object);
    }

    /**
     * Does injection magic on the given object.
     *
     * @param <T> type of object to do injection for
     * @param beanManager The bean manager with which to perform the lookup.
     * @param object  object to do injection for
     */
    @SuppressWarnings("unchecked")
    public static <T> void doInjectionsForUnmanagedObject(final BeanManager beanManager, final T object) {
        AnnotatedType<T> type = (AnnotatedType<T>) beanManager.createAnnotatedType(object.getClass());
        InjectionTarget<T> target = beanManager.createInjectionTarget(type);
        CreationalContext<T> creationalContext = beanManager.createCreationalContext(null);
        target.inject(object, creationalContext);
    }

}
