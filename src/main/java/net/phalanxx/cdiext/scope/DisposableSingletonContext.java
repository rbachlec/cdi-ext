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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * This class can be used to obtain or dispose a singleton instance for a given class.
 *
 * @author rbachlec
 */
public class DisposableSingletonContext {

    @Inject private BeanManager beanManager;

    /**
     * If there has already been created a singleton instance for the given type this instance is
     * returned. Otherwise a new instance is created and put in the {@link DisposableSingleton}
     * context.
     *
     * @param <T> type of singleton to be created
     * @param type class of singleton to be created
     * @return disposable singleton instance
     */
    @SuppressWarnings("unchecked")
    public <T> T getSingleton(final Class<T> type) {
        T result = null;
        Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type));

        if (bean != null) {
            Class<? extends Annotation> scope = bean.getScope();
            if (DisposableSingleton.class.equals(scope)) {
                CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
                result = (T) beanManager.getReference(bean, type, creationalContext);
            }
        }
        return result;
    }

    /**
     * Disposes a given singleton instance. If the given object is not present in the
     * {@link DisposableSingleton} context an exception is thrown.
     *
     * @param <T> type of the given singleton
     * @param singleton disposable singleton instance
     */
    public <T> void disposeSingleton(final T singleton) {
        DisposableSingletonContextImpl context =
                (DisposableSingletonContextImpl) beanManager.getContext(DisposableSingleton.class);
        context.dispose(singleton);
    }

    /**
     * Checks if the given object is in the {@link DisposableSingleton} context.
     *
     * @param <T> type of the object to search for
     * @param singleton object to be searched for
     * @return true/false
     */
    public <T> Boolean contains(final T singleton) {
        DisposableSingletonContextImpl context =
                (DisposableSingletonContextImpl) beanManager.getContext(DisposableSingleton.class);
        return context.contains(singleton);
    }

}
