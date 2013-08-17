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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By giving this annotation on a class the {@link FactoryExtension} creates a special bean for
 * instantiating the annotated class. Sometimes it's difficult to write a producer method because
 * the type to be created is not known at compile time. With this extension someone can easily
 * implement a generic factory for a specific class.
 *
 * @author rbachlec
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProducedByFactory {

    /**
     * The class of the factory to be used for creation of annotated types.
     *
     * @return class implementing the {@link Factory} interface
     */
    Class<? extends Factory> factory();

}
