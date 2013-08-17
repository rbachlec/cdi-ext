package net.phalanxx.cdiext.beans;

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


import net.phalanxx.cdiext.factory.Factory;

public class TestFactory implements Factory {

    @Override
    public <T> T createInstance(Class<T> clazz) {
        try {
            T car = clazz.newInstance();
            ((AbstractTestBean) car).setProducedByFactory(Boolean.TRUE);

            return car;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Fatal error on producing car.", ex);
        }
    }
}
