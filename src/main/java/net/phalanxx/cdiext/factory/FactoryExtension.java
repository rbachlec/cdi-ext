package net.phalanxx.cdiext.factory;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
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
 * A portable extension base class called by CDI on startup to find {@link CreatedByFactory} annotated
 * classes. For each class found the extension generates a special bean responsible for instantiation of
 * the annotated class. Sometimes it's difficult to write a producer method because the type to be created
 * is not known at compile time. So someone can write a generic factory for a specific class.
 */
public class FactoryExtension implements Extension {

    private Set<AnnotatedType> toBeCreatedByFactory = new HashSet<>();

    private void processAnnotatedType(@Observes ProcessAnnotatedType pat, BeanManager beanManager) {
        final AnnotatedType annotatedType = pat.getAnnotatedType();
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(Annotation annotation) {
                return annotation.annotationType().equals(CreatedByFactory.class);
            }

            @Override
            public boolean isUnique() {
                return true;
            }
        });

        if (annotations.size() > 0) {
            toBeCreatedByFactory.add(annotatedType);
        }
    }

    private void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, final BeanManager beanManager) {
        for (final AnnotatedType annotatedType : toBeCreatedByFactory) {
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

                    Set<Annotation> q = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
                        @Override
                        public boolean matches(Annotation annotation) {
                            return beanManager.isQualifier(annotation.annotationType());
                        }

                        @Override
                        public boolean isUnique() {
                            return false;
                        }
                    });
                    qualifiers.addAll(q);

                    return qualifiers;
                }

                @Override
                public Class getScope() {
                    Set<Annotation> scopes = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
                        @Override
                        public boolean matches(Annotation annotation) {
                            return beanManager.isScope(annotation.annotationType());
                        }

                        @Override
                        public boolean isUnique() {
                            return true;
                        }
                    });

                    if (scopes.size() > 0) {
                        return ((Annotation) scopes.toArray()[0]).annotationType();
                    } else {
                        return Dependent.class;
                    }
                }

                @Override
                public String getName() {
                    return null;
                }

                @Override
                public Set getStereotypes() {
                    Set<Annotation> stereotypes = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
                        @Override
                        public boolean matches(Annotation annotation) {
                            return beanManager.isStereotype(annotation.annotationType());
                        }

                        @Override
                        public boolean isUnique() {
                            return false;
                        }
                    });

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
                    Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
                        @Override
                        public boolean matches(Annotation annotation) {
                            return annotation.annotationType().equals(CreatedByFactory.class);
                        }

                        @Override
                        public boolean isUnique() {
                            return true;
                        }
                    });

                    CreatedByFactory annotation = (CreatedByFactory) annotations.toArray()[0];
                    Factory factory = BeanManagerUtil.getContextualInstance(beanManager, annotation.factory());

                    Object instance = factory.createInstance(annotatedType.getJavaClass());
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

        toBeCreatedByFactory.clear();
    }

    /**
     * Searches the given annotated type for matching annotations according to the given filter implementation.
     *
     * @param annotatedType annotated type to search for matching annotations
     * @param beanManager bean manager
     * @param filter filter implementation to search for annotations
     * @return list of matching annotations
     */
    private Set<Annotation> findAnnotations(AnnotatedType annotatedType, BeanManager beanManager,
                                            AnnotationFilter filter) {
        Set<Annotation> matchingAnnotations = new HashSet<>();

        Set<Annotation> annotations = annotatedType.getAnnotations();
        for (Annotation annotation : annotations) {
            matchingAnnotations.addAll(findMatchingAnnotations(annotation, beanManager, filter));

            if (filter.isUnique() && matchingAnnotations.size() > 0) {
                break;
            }
        }

        return matchingAnnotations;
    }

    /**
     * Recursively searches for annotations matching the filter. Recursion is necessary because of
     * stereotypes.
     *
     * @param annotation annotation to check for stereotype definition
     * @param beanManager bean manager
     * @param filter filter implementation to search for annotations
     * @return list of matching annotations
     */
    private Set<Annotation> findMatchingAnnotations(Annotation annotation, BeanManager beanManager,
                                                    AnnotationFilter filter) {
        Set<Annotation> matchingAnnotations = new HashSet<>();

        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (beanManager.isStereotype(annotationType)) {
            for (Annotation stereotype : beanManager.getStereotypeDefinition(annotationType)) {
                matchingAnnotations.addAll(findMatchingAnnotations(stereotype, beanManager, filter));

                if (filter.isUnique() && matchingAnnotations.size() > 0) {
                    break;
                }
            }
        } else if (filter.matches(annotation)) {
            matchingAnnotations.add(annotation);
        }

        return matchingAnnotations;
    }

    /**
     * Interface for filter used for searching annotations.
     */
    private interface AnnotationFilter {
        boolean matches(Annotation annotation);
        boolean isUnique();
    }

}