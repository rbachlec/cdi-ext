package net.phalanxx.cdiext.util;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author rbachlec
 */
public class AnnotationUtil {

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(AnnotatedType<?> annotatedType, BeanManager beanManager,
                                                         final Class<T> annotationType) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(Annotation annotation) {
                return annotation.annotationType().equals(annotationType);
            }
        }, true);

        return (T) (annotations.isEmpty() ? null : annotations.toArray()[0]);
    }

    public static boolean isAnnotationPresent(AnnotatedType<?> annotatedType, BeanManager beanManager,
                                              Class<? extends Annotation> annotationType) {
        return getAnnotation(annotatedType, beanManager, annotationType) != null;
    }

    public static Annotation getScope(AnnotatedType<?> annotatedType, final BeanManager beanManager) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(Annotation annotation) {
                return beanManager.isScope(annotation.annotationType());
            }
        }, true);

        return (Annotation) (annotations.isEmpty() ? new AnnotationLiteral<Dependent>() {} : annotations.toArray()[0]);
    }

    public static Set<Annotation> getStereotypes(AnnotatedType<?> annotatedType, final BeanManager beanManager) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(Annotation annotation) {
                return beanManager.isStereotype(annotation.annotationType());
            }
        }, false);

        return annotations;
    }

    public static Set<Annotation> getQualifiers(AnnotatedType<?> annotatedType, final BeanManager beanManager) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(Annotation annotation) {
                return beanManager.isQualifier(annotation.annotationType());
            }
        }, false);

        return annotations;
    }

    /**
     * Searches the given annotated type for matching annotations according to the given filter implementation.
     *
     * @param annotatedType annotated type to search for matching annotations
     * @param beanManager bean manager
     * @param filter filter implementation to search for annotations
     * @param unique there can only be one matching annotation
     * @return list of matching annotations
     */
    private static Set<Annotation> findAnnotations(AnnotatedType<?> annotatedType, BeanManager beanManager,
                                                   AnnotationFilter filter, Boolean unique) {
        Set<Annotation> matchingAnnotations = new HashSet<>();

        Set<Annotation> annotations = annotatedType.getAnnotations();
        for (Annotation annotation : annotations) {
            matchingAnnotations.addAll(findMatchingAnnotations(annotation, beanManager, filter, unique));

            if (unique && matchingAnnotations.size() > 0) {
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
    private static Set<Annotation> findMatchingAnnotations(Annotation annotation, BeanManager beanManager,
                                                           AnnotationFilter filter, Boolean unique) {
        Set<Annotation> matchingAnnotations = new HashSet<>();

        Class<? extends Annotation> annotationType = annotation.annotationType();

        if (filter.matches(annotation)) {
            matchingAnnotations.add(annotation);
        }

        if (!unique || matchingAnnotations.isEmpty()) {
            if (beanManager.isStereotype(annotationType)) {
                for (Annotation stereotype : beanManager.getStereotypeDefinition(annotationType)) {
                    matchingAnnotations.addAll(findMatchingAnnotations(stereotype, beanManager, filter, unique));

                    if (unique && !matchingAnnotations.isEmpty()) {
                        break;
                    }
                }
            }
        }



        return matchingAnnotations;
    }

    /**
     * Interface for filter used for searching annotations.
     */
    private interface AnnotationFilter {
        boolean matches(Annotation annotation);
    }

}
