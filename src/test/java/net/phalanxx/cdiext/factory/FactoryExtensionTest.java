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


import static org.fest.assertions.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Named;

import net.phalanxx.cdiext.beans.AbstractTestBean;
import net.phalanxx.cdiext.beans.ApplicationScopedBeanProducedByFactory;
import net.phalanxx.cdiext.beans.DependentScopedBean;
import net.phalanxx.cdiext.beans.DependentScopedBeanProducedByFactory;
import net.phalanxx.cdiext.beans.NamedBeanProducedByFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FactoryExtensionTest {

    @Inject private ApplicationScopedBeanProducedByFactory applicationScopedBeanProducedByFactory;
    @Inject private ApplicationScopedBeanProducedByFactory applicationScopedBeanProducedByFactory2;

    @Inject private DependentScopedBeanProducedByFactory dependentScopedBeanProducedByFactory;
    @Inject private DependentScopedBeanProducedByFactory dependentScopedBeanProducedByFactory2;

    @Inject @Named("SpecialFactoryProducedBean") private AbstractTestBean namedBeanProducedByFactory;

    @Inject private DependentScopedBean dependentScopedBean;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addClass(ProducedByFactory.class)
                         .addClass(Factory.class)
                         .addClass(FactoryExtension.class)
                         .addClass(FactoryProducedBean.class)
                         .addPackage(AbstractTestBean.class.getPackage())
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void factoryProducesAnnotatedBean() {
        assertThat(applicationScopedBeanProducedByFactory).isNotNull();
        assertThat(applicationScopedBeanProducedByFactory.getBeanId()).isEqualTo(ApplicationScopedBeanProducedByFactory.BEAN_ID);
        assertThat(applicationScopedBeanProducedByFactory.getProducedByFactory()).isTrue();
    }

    @Test
    public void factoryDoesNotProduceBeanIfNotAnnotated() {
        assertThat(dependentScopedBean).isNotNull();
        assertThat(dependentScopedBean.getBeanId()).isEqualTo(DependentScopedBean.BEAN_ID);
        assertThat(dependentScopedBean.getProducedByFactory()).isFalse();
    }

    @Test
    public void factoryRespectsScopes() {
        assertThat(applicationScopedBeanProducedByFactory).isNotNull();
        assertThat(applicationScopedBeanProducedByFactory2).isNotNull();
        assertThat(applicationScopedBeanProducedByFactory).isEqualTo(applicationScopedBeanProducedByFactory2);

        assertThat(dependentScopedBeanProducedByFactory).isNotNull();
        assertThat(dependentScopedBeanProducedByFactory2).isNotNull();
        assertThat(dependentScopedBeanProducedByFactory).isNotEqualTo(dependentScopedBeanProducedByFactory2);
    }

    @Test
    public void factoryRespectsQualifiers() {
        assertThat(namedBeanProducedByFactory).isNotNull();
        assertThat(namedBeanProducedByFactory instanceof NamedBeanProducedByFactory).isTrue();
        assertThat(namedBeanProducedByFactory.getBeanId()).isEqualTo(NamedBeanProducedByFactory.BEAN_ID);
        assertThat(namedBeanProducedByFactory.getProducedByFactory()).isTrue();
    }

}
