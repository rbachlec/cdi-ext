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


import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

/**
 * Factory class for logger instances.
 *
 * @author rbachlec
 */
public class Slf4jLoggerFactory {

    /**
     * Producer method creating slf4j logger instances for injection.
     *
     * @param injectionPoint point of injection
     * @return slf4j logger instance
     */
    @Produces
    public Logger createLogger(final InjectionPoint injectionPoint) {
        String name = injectionPoint.getMember().getDeclaringClass().getName();
        return org.slf4j.LoggerFactory.getLogger(name);
    }
}
