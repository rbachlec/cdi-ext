package net.phalanxx.cdiext.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import net.phalanxx.cdiext.util.test.AlternativeBean;
import net.phalanxx.cdiext.util.test.DependentScopedBean;
import net.phalanxx.cdiext.util.test.NamedBean;
import net.phalanxx.cdiext.util.test.TestBean;

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
                         .addPackage(DependentScopedBean.class.getPackage())
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
        TestBean theBean = beanManagerUtil.getContextualInstance(TestBean.class, new NamedLiteral("SpecialBean"));
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
        List<TestBean> beans = beanManagerUtil.getContextualInstances(TestBean.class);
        assertThat(beans).isNotEmpty();
        assertThat(beans.size()).isEqualTo(3);
    }

    @Test
    public void getContextualInstancesFindsOneNamedBean() {
        List<TestBean> beans = beanManagerUtil.getContextualInstances(TestBean.class, new NamedLiteral("SpecialBean"));
        assertThat(beans).isNotEmpty();
        assertThat(beans.size()).isEqualTo(1);
    }

    @Test
    public void doInjectionsForUnmanagedObject() {
        AlternativeBean alternativeBean = new AlternativeBean();
        assertThat(alternativeBean.getDependentScopedBean()).isNull();
        beanManagerUtil.doInjectionsForUnmanagedObject(alternativeBean);
        assertThat(alternativeBean.getDependentScopedBean()).isNotNull();
    }

}
