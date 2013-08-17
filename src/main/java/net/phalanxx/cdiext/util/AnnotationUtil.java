package net.phalanxx.cdiext.util;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Util with methods to query for annotations and stuff.
 *
 * @author rbachlec
 */
public final class AnnotationUtil {

    private AnnotationUtil() {
        // utility class
    }

    /**
     * Returns a given annotation on an annotated type instance. If the annotation is found
     * more than once (e.g. because it's also contained in a stereotype) the returned annoation is
     * not deterministic. So make sure to prevent such a situation.
     *
     * @param <T> type of the annotated type
     * @param annotatedType annotated type instance
     * @param beanManager the bean manager
     * @param annotationType class of the annotation to query for
     * @return annotation or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(final AnnotatedType<?> annotatedType,
                                                         final BeanManager beanManager,
                                                         final Class<T> annotationType) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(final Annotation annotation) {
                return annotation.annotationType().equals(annotationType);
            }
        }, true);

        return (T) (annotations.isEmpty() ? null : annotations.toArray()[0]);
    }

    /**
     * Searches for the given annotation and returns if found.
     *
     * @param annotatedType annotated type instance
     * @param beanManager the bean manager
     * @param annotationType class of the annotation to query for
     * @return true/false
     */
    public static boolean isAnnotationPresent(final AnnotatedType<?> annotatedType,
                                              final BeanManager beanManager,
                                              final Class<? extends Annotation> annotationType) {
        return getAnnotation(annotatedType, beanManager, annotationType) != null;
    }

    /**
     * Returns the scope of the given annotated type instance.
     *
     * @param annotatedType annotated type instance
     * @param beanManager the bean manager
     * @return the scope of the given annotated type
     */
    public static Annotation getScope(final AnnotatedType<?> annotatedType, final BeanManager beanManager) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(final Annotation annotation) {
                return beanManager.isScope(annotation.annotationType());
            }
        }, true);

        return (Annotation) (annotations.isEmpty() ? new AnnotationLiteral<Dependent>() {} : annotations.toArray()[0]);
    }

    /**
     * Returns the stereotypes of the given annotated type instance.
     *
     * @param annotatedType annotated type instance
     * @param beanManager the bean manager
     * @return the stereotypes of the given annotated type
     */
    public static Set<Annotation> getStereotypes(final AnnotatedType<?> annotatedType, final BeanManager beanManager) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(final Annotation annotation) {
                return beanManager.isStereotype(annotation.annotationType());
            }
        }, false);

        return annotations;
    }

    /**
     * Returns the qualifiers of the given annotated type instance.
     *
     * @param annotatedType annotated type instance
     * @param beanManager the bean manager
     * @return the qualifiers of the given annotated type
     */
    public static Set<Annotation> getQualifiers(final AnnotatedType<?> annotatedType, final BeanManager beanManager) {
        Set<Annotation> annotations = findAnnotations(annotatedType, beanManager, new AnnotationFilter() {
            @Override
            public boolean matches(final Annotation annotation) {
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
     * @param unique there should only be returned one annotation
     * @return list of matching annotations
     */
    private static Set<Annotation> findAnnotations(final AnnotatedType<?> annotatedType,
                                                   final BeanManager beanManager,
                                                   final AnnotationFilter filter,
                                                   final Boolean unique) {
        Set<Annotation> matchingAnnotations = new HashSet<>();

        Set<Annotation> annotations = annotatedType.getAnnotations();
        for (Annotation annotation : annotations) {
            matchingAnnotations.addAll(findMatchingAnnotations(annotation, beanManager, filter, unique));

            if (unique && !matchingAnnotations.isEmpty()) {
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
    private static Set<Annotation> findMatchingAnnotations(final Annotation annotation,
                                                           final BeanManager beanManager,
                                                           final AnnotationFilter filter,
                                                           final Boolean unique) {
        Set<Annotation> matchingAnnotations = new HashSet<>();

        Class<? extends Annotation> annotationType = annotation.annotationType();

        if (filter.matches(annotation)) {
            matchingAnnotations.add(annotation);
        }

        if ((!unique || matchingAnnotations.isEmpty()) && beanManager.isStereotype(annotationType)) {
            for (Annotation stereotype : beanManager.getStereotypeDefinition(annotationType)) {
                matchingAnnotations.addAll(findMatchingAnnotations(stereotype, beanManager, filter, unique));

                if (unique && !matchingAnnotations.isEmpty()) {
                    break;
                }
            }
        }

        return matchingAnnotations;
    }

    /**
     * Interface for filter used for searching annotations.
     */
    private interface AnnotationFilter {

        /**
         * True if the given annotation matches the filter.
         *
         * @param annotation annotation to be queried
         * @return true/false
         */
        boolean matches(Annotation annotation);
    }

}
