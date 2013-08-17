package net.phalanxx.cdiext.scope;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context implementation for the {@link DisposableSingleton} scope.
 *
 * @author rbachlec
 */
public class DisposableSingletonContextImpl implements Context {

    private final Logger log = LoggerFactory.getLogger(DisposableSingletonContextImpl.class);

    private final ConcurrentHashMap<Bean<?>, ContextualInstance<?>> beanStore = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> getScope() {
        return DisposableSingleton.class;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        Bean<T> bean = (Bean<T>) contextual;

        T instance;
        ContextualInstance<?> contextualInstance = beanStore.get(bean);
        if (contextualInstance == null) {
            log.debug("Creating instance of bean {}.", bean.toString());
            instance = contextual.create(creationalContext);
            contextualInstance = new DisposableSingletonInstance<>(instance, creationalContext);
            beanStore.put(bean, contextualInstance);
        } else {
            log.debug("Found existing instance of bean {}.", bean.toString());
            instance = (T) contextualInstance.getInstance();
        }

        return instance;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final Contextual<T> contextual) {
        ContextualInstance<?> contextualInstance = beanStore.get(contextual);
        return contextualInstance == null ? null : (T) contextualInstance.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Removes a given instance from the {@link DisposableSingleton} context.
     *
     * @param <T> type of instance to be disposed
     * @param instance instance to be disposed
     */
    protected <T> void dispose(final T instance) {
        if (!contains(instance)) {
            throw new IllegalArgumentException("The given instance could not be found in " +
                                               "the DisposableSingletonContext.");
        }

        for (Map.Entry<Bean<?>, ContextualInstance<?>> entry : beanStore.entrySet()) {
            Bean<?> bean = entry.getKey();
            ContextualInstance<? extends Object> contextualInstance = entry.getValue();

            if (contextualInstance.getInstance() == instance) {
                log.debug("Disposing instance of bean {}.", bean.toString());
                contextualInstance.getCreationalContext().release();
                beanStore.remove(bean);
                break;
            }
        }
    }

    /**
     * Checks if the given object is in the {@link DisposableSingleton} context.
     *
     * @param <T> type of the object to search for
     * @param instance object to be searched for
     * @return true/false
     */
    protected <T> Boolean contains(final T instance) {
        Boolean contains = false;
        for (Map.Entry<Bean<?>, ContextualInstance<?>> entry : beanStore.entrySet()) {
            ContextualInstance<? extends Object> contextualInstance = entry.getValue();
            if (contextualInstance.getInstance() == instance) {
                contains = true;
            }
        }

        return contains;
    }

    /**
     * A container class for disposable singleton instances.
     *
     * @param <T> type of instance in container
     */
    private static class DisposableSingletonInstance<T> implements ContextualInstance<T> {
        private final T instance;
        private final CreationalContext<T> creationalContext;

        public DisposableSingletonInstance(final T instance, final CreationalContext<T> creationalContext) {
            this.instance = instance;
            this.creationalContext = creationalContext;
        }

        @Override
        public T getInstance() {
            return instance;
        }

        @Override
        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }
    }

}
