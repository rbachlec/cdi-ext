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
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;

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

    @Inject private BeanManager beanManager;
    @Inject private ClassWithAStereotype classWithAStereotype;

    private Bean<ClassWithAStereotype> classWithAStereotypeBean;
    private Bean<ClassWithApplicationScope> classWithApplicationScopeBean;
    private Bean<Opel> classWithAFactory;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addClass(GeneratedBean.class)
                         .addClass(ClassWithAStereotype.class)
                         .addClass(ClassWithApplicationScope.class)
                         .addClass(Opel.class)
                         .addClass(CarFactory.class)
                         .addClass(AnnotationLiteral.class)
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void setUp() {
        classWithAStereotypeBean = createGeneratedBean(ClassWithAStereotype.class);
        classWithApplicationScopeBean = createGeneratedBean(ClassWithApplicationScope.class);
        classWithAFactory = createGeneratedBean(Opel.class);
    }


    @Test
    public void getStereotypes() {
        Set<Class<? extends Annotation>> stereotypes = classWithAStereotypeBean.getStereotypes();
        assertThat(stereotypes).isNotEmpty();
        assertThat(stereotypes.size()).isEqualTo(1);
        assertThat(stereotypes).contains(StereotypeForTest.class);
    }

    @Test
    public void getTypes() {
        Set<Type> types = classWithAStereotypeBean.getTypes();
        assertThat(types).isNotEmpty();
        assertThat(types.size()).isEqualTo(2);
        assertThat(types).contains(ClassWithAStereotype.class, Object.class);
    }

    @Test
    public void getQualifiers() {
        Set<Annotation> qualifiers = classWithAStereotypeBean.getQualifiers();
        assertThat(qualifiers).isNotEmpty();
        assertThat(qualifiers.size()).isEqualTo(3);

        List<Class<? extends Annotation>> qualifierClasses = getClasses(qualifiers);
        assertThat(qualifierClasses).contains(Any.class, Default.class, Named.class);
    }

    @Test
    public void getScopeDependent() {
        Class<? extends Annotation> scope = classWithAStereotypeBean.getScope();
        assertThat(scope).isNotNull();
        assertThat(scope).isEqualTo(Dependent.class);
    }

    @Test
    public void getScopeApplicationScoped() {
        Class<? extends Annotation> scope = classWithApplicationScopeBean.getScope();
        assertThat(scope).isNotNull();
        assertThat(scope).isEqualTo(ApplicationScoped.class);
    }

    @Test
    public void getName() {
        String name = classWithAStereotypeBean.getName();
        assertThat(name).isNotNull();
        assertThat(name).isEqualTo("ClassWithAStereotype");
    }

    @Test
    public void getNameNotFound() {
        String name = classWithApplicationScopeBean.getName();
        assertThat(name).isNull();
    }

    @Test
    public void getBeanType() {
        Class<?> beanClass = classWithAStereotypeBean.getBeanClass();
        assertThat(beanClass).isNotNull();
        assertThat(beanClass).isEqualTo(ClassWithAStereotype.class);
    }

    @Test
    public void isAlternative() {
        boolean isAlternative = classWithAStereotypeBean.isAlternative();
        assertThat(isAlternative).isFalse();
    }

    @Test
    public void isNullable() {
        boolean isNullable = classWithAStereotypeBean.isNullable();
        assertThat(isNullable).isFalse();
    }

    @Test
    public void getInjectionPoints() {
        Set<InjectionPoint> injectionPoints = classWithAStereotypeBean.getInjectionPoints();
        assertThat(injectionPoints).isNotEmpty();
        assertThat(injectionPoints.size()).isEqualTo(1);
    }

    @Test
    public void create() {
        CreationalContext<Opel> creationalContext = beanManager.createCreationalContext(classWithAFactory);
        Opel opel = classWithAFactory.create(creationalContext);
        assertThat(opel).isNotNull();
    }

    @Test(expected=IllegalArgumentException.class)
    public void createThrowsExceptionOnClassWithoutFactory() {
        CreationalContext<ClassWithAStereotype> creationalContext =
                beanManager.createCreationalContext(classWithAStereotypeBean);
        classWithAStereotypeBean.create(creationalContext);
    }

    @Test
    public void destroy() {
        CreationalContext<ClassWithAStereotype> creationalContext = beanManager.createCreationalContext(classWithAStereotypeBean);
        classWithAStereotypeBean.destroy(classWithAStereotype, creationalContext);

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
