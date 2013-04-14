package net.phalanxx.cdiext.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import net.phalanxx.cdiext.util.BeanManagerUtil;

/**
 * A portable extension base class called by CDI before bean discovery that is responsible to generate factory methods
 * for classes annotated with the annotation class specified by the method {@link #getMarkerAnnotationClassName()}.
 */
public abstract class AnnotatedTypeFactoryGenerationExtension<A extends Annotation, F extends AnnotatedTypeFactory> implements Extension {

    private Set<AnnotatedType> annotatedTypes = new HashSet<>();

    private void annotatedType(@Observes ProcessAnnotatedType pat, BeanManager beanManager) {
        final AnnotatedType annotatedType = pat.getAnnotatedType();
        if (annotatedType.isAnnotationPresent(getMarkerAnnotationClass())) {
            annotatedTypes.add(annotatedType);

            AnnotatedType wrapped = new AnnotatedType() {
                @Override
                public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
                    return annotationType.equals(Alternative.class)
                            ? true : annotatedType.isAnnotationPresent(annotationType);
                }

                @Override
                public Class getJavaClass() {
                    return annotatedType.getJavaClass();
                }

                @Override
                public Set getConstructors() {
                    return annotatedType.getConstructors();
                }

                @Override
                public Set getMethods() {
                    return annotatedType.getMethods();
                }

                @Override
                public Set getFields() {
                    return annotatedType.getFields();
                }

                @Override
                public Type getBaseType() {
                    return annotatedType.getBaseType();
                }

                @Override
                public Set<Type> getTypeClosure() {
                    return annotatedType.getTypeClosure();
                }

                @Override
                public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
                    return annotatedType.getAnnotation(annotationType);
                }

                @Override
                public Set<Annotation> getAnnotations() {
                    return annotatedType.getAnnotations();
                }
            };

            pat.setAnnotatedType(wrapped);
        }
    }

    private void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, final BeanManager beanManager) {
        for (final AnnotatedType annotatedType : annotatedTypes) {
            final InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);

            Bean bean = new Bean() {
                @Override
                public Set getTypes() {
                    return annotatedType.getTypeClosure();
                }

                @Override
                public Set getQualifiers() {
                    Set<Annotation> qualifiers = new HashSet<>();
                    qualifiers.add(new AnnotationLiteral<Default>() {});
                    qualifiers.add(new AnnotationLiteral<Any>() {});

                    // add qualifiers of class
                    Set<Annotation> annotations = annotatedType.getAnnotations();
                    for (Annotation annotation : annotations) {
                        Class<? extends Annotation> annotationType = annotation.annotationType();

                        if (beanManager.isStereotype(annotationType)) {
                            for (Annotation stAnnotation : beanManager.getStereotypeDefinition(annotationType)) {
                                if (beanManager.isQualifier(stAnnotation.annotationType())) {
                                    qualifiers.add(stAnnotation);
                                }
                            }
                        }

                        if (beanManager.isQualifier(annotationType)) {
                            qualifiers.add(annotation);
                        }
                    }

                    return qualifiers;
                }

                @Override
                public Class getScope() {
                    Set<Annotation> annotations = annotatedType.getAnnotations();
                    for (Annotation annotation : annotations) {
                        Class<? extends Annotation> annotationType = annotation.annotationType();

                        if (beanManager.isStereotype(annotationType)) {
                            for (Annotation stAnnotation : beanManager.getStereotypeDefinition(annotationType)) {
                                if (beanManager.isScope(stAnnotation.annotationType())) {
                                    return stAnnotation.annotationType();
                                }
                            }
                        } else {
                            if (beanManager.isScope(annotationType)) {
                                return annotationType;
                            }
                        }
                    }

                    return Dependent.class;
                }

                @Override
                public String getName() {
                    return null;
                }

                @Override
                public Set getStereotypes() {
                    Set<Annotation> stereotypes = new HashSet<>();

                    // add stereotypes of class
                    Set<Annotation> annotations = annotatedType.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (beanManager.isStereotype(annotation.annotationType())) {
                            stereotypes.add(annotation);
                        }
                    }

                    return stereotypes;
                }

                @Override
                public Class getBeanClass() {
                    return annotatedType.getJavaClass();
                }

                @Override
                public boolean isAlternative() {
                    return false;
                }

                @Override
                public boolean isNullable() {
                    return false;
                }

                @Override
                public Set getInjectionPoints() {
                    return injectionTarget.getInjectionPoints();
                }

                @Override
                public Object create(CreationalContext creationalContext) {
                    A annotation = annotatedType.getAnnotation(getMarkerAnnotationClass());
                    F factory = BeanManagerUtil.getContextualInstance(beanManager, getFactoryClass());

                    Object instance = factory.createInstance(annotation);
                    injectionTarget.inject(instance, creationalContext);
                    injectionTarget.postConstruct(instance);
                    return instance;
                }

                @Override
                public void destroy(Object instance, CreationalContext creationalContext) {
                    injectionTarget.preDestroy(instance);
                    injectionTarget.dispose(instance);
                    creationalContext.release();
                }

            };

            abd.addBean(bean);
        }

        annotatedTypes.clear();
    }

    /**
     * Returns the class the factory method should be added to. Note that you should not directly access this class
     * to obtain the class name because javassist can only change untouched classes. So the factory class name should be
     * inserted as string in implementations of this class.
     *
     * @return factory class name
     */
    protected abstract Class<F> getFactoryClass();

    /**
     * Annotation of the classes for which factory methods should be created.
     *
     * @return marker annotation name
     */
    protected abstract Class<A> getMarkerAnnotationClass();

}
