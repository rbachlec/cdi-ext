package net.phalanxx.cdiext.util;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;

import net.phalanxx.cdiext.beans.AbstractTestBean;
import net.phalanxx.cdiext.beans.ApplicationScopedBean;
import net.phalanxx.cdiext.beans.NamedBean;
import net.phalanxx.cdiext.beans.StereotypedBeanWithInjectionPoint;
import net.phalanxx.cdiext.beans.TestStereotypeApplicationScoped;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AnnotationUtilTest {

    @Inject private BeanManager beanManager;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addPackage(AbstractTestBean.class.getPackage())
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void getNamedAnnotation() {
        AnnotatedType<NamedBean> namedBeanType = beanManager.createAnnotatedType(NamedBean.class);
        Named namedAnnotation = AnnotationUtil.getAnnotation(namedBeanType, beanManager, Named.class);
        assertThat(namedAnnotation).isNotNull();
        assertThat(namedAnnotation.value()).isEqualTo("SpecialBean");
    }

    @Test
    public void getAnnotationDoesNotFindMissingAnnotation() {
        AnnotatedType<NamedBean> namedBeanType = beanManager.createAnnotatedType(NamedBean.class);
        ApplicationScoped applicationScopedAnnotation =
                AnnotationUtil.getAnnotation(namedBeanType, beanManager, ApplicationScoped.class);
        assertThat(applicationScopedAnnotation).isNull();
    }

    @Test
    public void getAnnotationReturnsOnlyOneAnnotationIfMultipleArePresent() {
        AnnotatedType<StereotypedBeanWithInjectionPoint> stereotypedBeanType =
                beanManager.createAnnotatedType(StereotypedBeanWithInjectionPoint.class);
        ApplicationScoped applicationScopedAnnotation =
                AnnotationUtil.getAnnotation(stereotypedBeanType, beanManager, ApplicationScoped.class);
        assertThat(applicationScopedAnnotation).isNotNull();
    }

    @Test
    public void isAnnotationPresentTrue() {
        AnnotatedType<NamedBean> namedBeanType = beanManager.createAnnotatedType(NamedBean.class);
        Boolean isPresent = AnnotationUtil.isAnnotationPresent(namedBeanType, beanManager, Named.class);
        assertThat(isPresent).isTrue();
    }

    @Test
    public void isAnnotationPresentFalse() {
        AnnotatedType<NamedBean> namedBeanType = beanManager.createAnnotatedType(NamedBean.class);
        Boolean isPresent = AnnotationUtil.isAnnotationPresent(namedBeanType, beanManager, ApplicationScoped.class);
        assertThat(isPresent).isFalse();
    }

    @Test
    public void getScopeDependent() {
        AnnotatedType<NamedBean> namedBeanType = beanManager.createAnnotatedType(NamedBean.class);
        Annotation scope = AnnotationUtil.getScope(namedBeanType, beanManager);
        assertThat(scope).isNotNull();
        assertThat(scope.annotationType()).isEqualTo(Dependent.class);
    }

    @Test
    public void getScopeApplicationScoped() {
        AnnotatedType<ApplicationScopedBean> appScopedType = beanManager.createAnnotatedType(ApplicationScopedBean.class);
        Annotation scope = AnnotationUtil.getScope(appScopedType, beanManager);
        assertThat(scope).isNotNull();
        assertThat(scope.annotationType()).isEqualTo(ApplicationScoped.class);
    }

    @Test
    public void getStereotypes() {
        AnnotatedType<StereotypedBeanWithInjectionPoint> stereotypeBeanType =
                beanManager.createAnnotatedType(StereotypedBeanWithInjectionPoint.class);
        Set<Annotation> stereotypes = AnnotationUtil.getStereotypes(stereotypeBeanType, beanManager);
        assertThat(stereotypes).isNotEmpty();
        assertThat(stereotypes.size()).isEqualTo(1);
        assertThat(stereotypes.iterator().next().annotationType()).isEqualTo(TestStereotypeApplicationScoped.class);
    }

    @Test
    public void getStereotypesFindsNothing() {
        AnnotatedType<NamedBean> namedBeanType = beanManager.createAnnotatedType(NamedBean.class);
        Set<Annotation> stereotypes = AnnotationUtil.getStereotypes(namedBeanType, beanManager);
        assertThat(stereotypes).isEmpty();
    }

    @Test
    public void getQualifiers() {
        AnnotatedType<NamedBean> namedBeanType = beanManager.createAnnotatedType(NamedBean.class);
        Set<Annotation> qualifiers = AnnotationUtil.getQualifiers(namedBeanType, beanManager);
        assertThat(qualifiers).isNotEmpty();
        assertThat(qualifiers.size()).isEqualTo(1);
        assertThat(qualifiers.iterator().next().annotationType()).isEqualTo(Named.class);
    }

    @Test
    public void getQualifiersFindsNothing() {
        AnnotatedType<StereotypedBeanWithInjectionPoint> stereotypedBeanType =
                beanManager.createAnnotatedType(StereotypedBeanWithInjectionPoint.class);
        Set<Annotation> qualifiers = AnnotationUtil.getQualifiers(stereotypedBeanType, beanManager);
        assertThat(qualifiers).isEmpty();
    }

}
