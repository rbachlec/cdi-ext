package net.phalanxx.cdiext.factory;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.inject.Named;

import net.phalanxx.cdiext.factory.test.AbstractTestBean;
import net.phalanxx.cdiext.factory.test.ApplicationScopedBean;
import net.phalanxx.cdiext.factory.test.ApplicationScopedBeanProducedByFactory;
import net.phalanxx.cdiext.factory.test.DependentScopedBean;
import net.phalanxx.cdiext.factory.test.NamedBean;
import net.phalanxx.cdiext.factory.test.StereotypedBeanWithInjectionPoint;
import net.phalanxx.cdiext.factory.test.TestFactory;
import net.phalanxx.cdiext.factory.test.TestStereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GeneratedBeanTest {

    @Inject private BeanManager                       beanManager;

    @Inject private StereotypedBeanWithInjectionPoint stereotyped;

    private Bean<ApplicationScopedBeanProducedByFactory> applicationScopedBeanProducedByFactory;
    private Bean<ApplicationScopedBean>                  applicationScopedBean;
    private Bean<DependentScopedBean>                    dependentScopedBean;
    private Bean<StereotypedBeanWithInjectionPoint>      stereotypedBeanWithInjectionPoint;
    private Bean<NamedBean>                              namedBean;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addPackage(TestFactory.class.getPackage())
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void setUp() {
        applicationScopedBeanProducedByFactory = createGeneratedBean(ApplicationScopedBeanProducedByFactory.class);
        applicationScopedBean                  = createGeneratedBean(ApplicationScopedBean.class);
        dependentScopedBean                    = createGeneratedBean(DependentScopedBean.class);
        stereotypedBeanWithInjectionPoint                        = createGeneratedBean(StereotypedBeanWithInjectionPoint.class);
        namedBean                              = createGeneratedBean(NamedBean.class);
    }


    @Test
    public void getStereotypes() {
        Set<Class<? extends Annotation>> stereotypes = stereotypedBeanWithInjectionPoint.getStereotypes();
        assertThat(stereotypes).isNotEmpty();
        assertThat(stereotypes.size()).isEqualTo(1);
        assertThat(stereotypes).contains(TestStereotype.class);
    }

    @Test
    public void getTypes() {
        Set<Type> types = stereotypedBeanWithInjectionPoint.getTypes();
        assertThat(types).isNotEmpty();
        assertThat(types.size()).isEqualTo(3);
        assertThat(types).contains(StereotypedBeanWithInjectionPoint.class, AbstractTestBean.class, Object.class);
    }

    @Test
    public void getQualifiers() {
        Set<Annotation> qualifiers = namedBean.getQualifiers();
        assertThat(qualifiers).isNotEmpty();
        assertThat(qualifiers.size()).isEqualTo(3);

        List<Class<? extends Annotation>> qualifierClasses = getClasses(qualifiers);
        assertThat(qualifierClasses).contains(Any.class, Default.class, Named.class);
    }

    @Test
    public void getScopeDependent() {
        Class<? extends Annotation> scope = dependentScopedBean.getScope();
        assertThat(scope).isNotNull();
        assertThat(scope).isEqualTo(Dependent.class);
    }

    @Test
    public void getScopeApplicationScoped() {
        Class<? extends Annotation> scope = applicationScopedBean.getScope();
        assertThat(scope).isNotNull();
        assertThat(scope).isEqualTo(ApplicationScoped.class);
    }

    @Test
    public void getName() {
        String name = namedBean.getName();
        assertThat(name).isNotNull();
        assertThat(name).isEqualTo("SpecialBean");
    }

    @Test
    public void getNameNotFound() {
        String name = applicationScopedBean.getName();
        assertThat(name).isNull();
    }

    @Test
    public void getBeanType() {
        Class<?> beanClass = stereotypedBeanWithInjectionPoint.getBeanClass();
        assertThat(beanClass).isNotNull();
        assertThat(beanClass).isEqualTo(StereotypedBeanWithInjectionPoint.class);
    }

    @Test
    public void isAlternative() {
        boolean isAlternative = applicationScopedBean.isAlternative();
        assertThat(isAlternative).isFalse();
    }

    @Test
    public void isNullable() {
        boolean isNullable = applicationScopedBean.isNullable();
        assertThat(isNullable).isFalse();
    }

    @Test
    public void getInjectionPoints() {
        Set<InjectionPoint> injectionPoints = stereotypedBeanWithInjectionPoint.getInjectionPoints();
        assertThat(injectionPoints).isNotEmpty();
        assertThat(injectionPoints.size()).isEqualTo(1);
    }

    @Test
    public void create() {
        CreationalContext<ApplicationScopedBeanProducedByFactory> creationalContext =
                beanManager.createCreationalContext(applicationScopedBeanProducedByFactory);
        ApplicationScopedBeanProducedByFactory theBean = applicationScopedBeanProducedByFactory.create(creationalContext);
        assertThat(theBean).isNotNull();
    }

    @Test(expected=IllegalArgumentException.class)
    public void createThrowsExceptionOnClassWithoutFactory() {
        CreationalContext<StereotypedBeanWithInjectionPoint> creationalContext =
                beanManager.createCreationalContext(stereotypedBeanWithInjectionPoint);
        stereotypedBeanWithInjectionPoint.create(creationalContext);
    }

    @Test
    public void destroy() {
        CreationalContext<StereotypedBeanWithInjectionPoint> creationalContext =
                beanManager.createCreationalContext(stereotypedBeanWithInjectionPoint);
        stereotypedBeanWithInjectionPoint.destroy(stereotyped, creationalContext);

    }

    private List<Class<? extends Annotation>> getClasses(Set<Annotation> qualifiers) {
        List<Class<? extends Annotation>> qualifierClasses = new ArrayList<>();
        for (Annotation qualifier : qualifiers) {
            qualifierClasses.add(qualifier.annotationType());
        }
        return qualifierClasses;
    }

    private <T> Bean<T> createGeneratedBean(Class<T> clazz) {
        AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(clazz);
        InjectionTarget<T> injectionTarget = beanManager.createInjectionTarget(annotatedType);
        return new GeneratedBean<>(annotatedType, injectionTarget, beanManager);
    }

}
