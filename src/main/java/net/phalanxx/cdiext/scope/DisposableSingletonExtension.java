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


import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * CDI extension registering the {@link DisposableSingleton} scope.
 *
 * @author rbachlec
 */
public class DisposableSingletonExtension implements Extension {

    /**
     * Adds scope before bean discovery phase.
     *
     * @param bbd {@link BeforeBeanDiscovery} event
     */
    public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
        bbd.addScope(DisposableSingleton.class, false, false);
    }

    /**
     * Adds context after bean discovery phase.
     *
     * @param abd {@link AfterBeanDiscovery} event
     */
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
        abd.addContext(new DisposableSingletonContextImpl());
    }

}
