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


/**
 * Classes annotated by {@link ProducedByFactory} are created by an implementation of this interface.
 *
 * @author rbachlec
 */
public interface Factory {

    /**
     * Method creating an instance of the given class. Additional parameters for instantiation can
     * eventually be given by another annotation on the class to be created.
     *
     * @param <T> type of object to be created
     * @param clazz class of object to be created
     * @return new instance of given class
     */
    <T> T createInstance(Class<T> clazz);

}
