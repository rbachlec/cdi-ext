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


import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import net.phalanxx.cdiext.beans.AbstractTestBean;
import net.phalanxx.cdiext.beans.AlternativeBean;
import net.phalanxx.cdiext.beans.DependentScopedBean;
import net.phalanxx.cdiext.beans.NamedBean;
import net.phalanxx.cdiext.beans.StereotypedBeanWithInjectionPoint;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.literal.NamedLiteral;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BeanManagerUtilTest {

    @Inject private BeanManagerUtil beanManagerUtil;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addClass(BeanManagerUtil.class)
                         .addPackage(AbstractTestBean.class.getPackage())
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void getContextualInstance() {
        DependentScopedBean theBean = beanManagerUtil.getContextualInstance(DependentScopedBean.class);
        assertThat(theBean).isNotNull();
        assertThat(theBean instanceof DependentScopedBean).isTrue();
    }

    @Test
    public void getContextualInstanceFindsNamedBean() {
        AbstractTestBean theBean = beanManagerUtil.getContextualInstance(AbstractTestBean.class, new NamedLiteral("SpecialBean"));
        assertThat(theBean).isNotNull();
        assertThat(theBean.getClass()).isEqualTo(NamedBean.class);
    }

    @Test
    public void getContextualInstancesDoesNotFindAlternativeAnnotatedBean() {
        AlternativeBean bean = beanManagerUtil.getContextualInstance(AlternativeBean.class);
        assertThat(bean).isNull();
    }

    @Test
    public void getContextualInstancesFindsOne() {
        List<DependentScopedBean> beans = beanManagerUtil.getContextualInstances(DependentScopedBean.class);
        assertThat(beans).isNotEmpty();
        assertThat(beans.size()).isEqualTo(1);
    }

    @Test
    public void getContextualInstancesFindsThree() {
        List<AbstractTestBean> beans = beanManagerUtil.getContextualInstances(AbstractTestBean.class);
        assertThat(beans).isNotEmpty();
        assertThat(beans.size()).isEqualTo(8);
    }

    @Test
    public void getContextualInstancesFindsOneNamedBean() {
        List<AbstractTestBean> beans = beanManagerUtil.getContextualInstances(AbstractTestBean.class, new NamedLiteral("SpecialBean"));
        assertThat(beans).isNotEmpty();
        assertThat(beans.size()).isEqualTo(1);
    }

    @Test
    public void doInjectionsForUnmanagedObject() {
        StereotypedBeanWithInjectionPoint beanWithInjectionPoint = new StereotypedBeanWithInjectionPoint();
        assertThat(beanWithInjectionPoint.getDependentScopedBean()).isNull();
        beanManagerUtil.doInjectionsForUnmanagedObject(beanWithInjectionPoint);
        assertThat(beanWithInjectionPoint.getDependentScopedBean()).isNotNull();
    }

}
