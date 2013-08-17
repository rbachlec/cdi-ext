package net.phalanxx.cdiext.scope;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import net.phalanxx.cdiext.beans.AlternativeBean;
import net.phalanxx.cdiext.beans.AnotherDisposableSingletonBean;
import net.phalanxx.cdiext.beans.ApplicationScopedBean;
import net.phalanxx.cdiext.beans.DisposableSingletonBean;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class DisposableSingletonContextTest {

    @Inject BeanManager beanManager;
    @Inject DisposableSingletonContext context;

    @Inject DisposableSingletonBean singletonBean;
    @Inject DisposableSingletonBean singletonBean2;
    @Inject AnotherDisposableSingletonBean anotherSingletonBean;
    @Inject ApplicationScopedBean applicationScopedBean;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addPackage(DisposableSingletonContext.class.getPackage())
                         .addPackage(DisposableSingletonBean.class.getPackage())
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void beanSuccessfullyInjected() {
        assertThat(singletonBean).isNotNull();
    }

    @Test
    public void beanIsSingleton() {
        assertThat(singletonBean).isNotNull();
        assertThat(singletonBean).isNotNull();
        assertThat(singletonBean).isEqualTo(singletonBean2);
    }

    @Test
    public void contextContainsAlreadyCreatedInstance() {
        assertThat(singletonBean).isNotNull();
        assertThat(context.contains(singletonBean)).isTrue();
    }

    @Test
    public void contextReturnsExistingBean() {
        assertThat(singletonBean).isNotNull();
        DisposableSingletonBean theBean = context.getSingleton(DisposableSingletonBean.class);
        assertThat(singletonBean).isEqualTo(theBean);
    }

    @Test
    public void disposingSingletonWorks1() {
        assertThat(singletonBean).isNotNull();
        assertThat(context.contains(singletonBean)).isTrue();
        assertThat(context.contains(anotherSingletonBean)).isTrue();

        context.disposeSingleton(singletonBean);
        assertThat(context.contains(singletonBean)).isFalse();
        assertThat(context.contains(anotherSingletonBean)).isTrue();
    }

    @Test
    public void disposingSingletonWorks2() {
        assertThat(singletonBean).isNotNull();
        assertThat(context.contains(singletonBean)).isTrue();
        assertThat(context.contains(anotherSingletonBean)).isTrue();

        context.disposeSingleton(anotherSingletonBean);
        assertThat(context.contains(singletonBean)).isTrue();
        assertThat(context.contains(anotherSingletonBean)).isFalse();
    }

    @Test
    public void contextCreatesDisposedBeanOnDemand() {
        assertThat(singletonBean).isNotNull();

        context.disposeSingleton(singletonBean);
        assertThat(context.contains(singletonBean)).isFalse();

        DisposableSingletonBean theBean = context.getSingleton(DisposableSingletonBean.class);
        assertThat(singletonBean).isNotEqualTo(theBean);
    }

    @Test(expected=IllegalArgumentException.class)
    public void disposingAnUnmanagedSingletonBeanThrowsException() {
        context.disposeSingleton(new DisposableSingletonBean());
    }

    @Test(expected=IllegalArgumentException.class)
    public void disposingAnApplicationScopedBeanThrowsException() {
        context.disposeSingleton(applicationScopedBean);
    }

    @Test
    public void contextDoesNotCreateApplicationScopedBean() {
        ApplicationScopedBean theBean = context.getSingleton(ApplicationScopedBean.class);
        assertThat(theBean).isNull();
    }

    @Test
    public void contextDoesNotCreateAlternativeBean() {
        AlternativeBean theBean = context.getSingleton(AlternativeBean.class);
        assertThat(theBean).isNull();
    }

    @Test
    public void contextReturnsExistingInstanceSuccessfully() {
        Object beanInstance = getContextualInstance();
        assertThat(beanInstance).isNotNull();
        assertThat(beanInstance instanceof DisposableSingletonBean).isTrue();
    }

    @Test
    public void contextReturnsNullOnMissingInstance() {
        context.disposeSingleton(singletonBean);
        Object beanInstance = getContextualInstance();
        assertThat(beanInstance).isNull();
    }

    private Object getContextualInstance() {
        Context theContext = beanManager.getContext(DisposableSingleton.class);

        Set<Bean<?>> disposableSingletonBean = beanManager.getBeans(DisposableSingletonBean.class);
        assertThat(disposableSingletonBean).isNotNull();
        assertThat(disposableSingletonBean.size()).isEqualTo(1);
        Bean<?> theBean = disposableSingletonBean.iterator().next();

        Object beanInstance = theContext.get(theBean);
        return beanInstance;
    }


}
