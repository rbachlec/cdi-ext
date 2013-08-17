package net.phalanxx.cdiext.util;

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
