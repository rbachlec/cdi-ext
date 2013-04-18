package net.phalanxx.cdiext.factory;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
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
import net.phalanxx.cdiext.util.AnnotationUtil;
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
        if (AnnotationUtil.isAnnotationPresent(annotatedType, beanManager, CreatedByFactory.class)) {
            toBeCreatedByFactory.add(annotatedType);
            pat.veto();
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
                    qualifiers.addAll(AnnotationUtil.getQualifiers(annotatedType, beanManager));
                    return qualifiers;
                }

                @Override
                public Class getScope() {
                    return AnnotationUtil.getScope(annotatedType, beanManager).annotationType();
                }

                @Override
                public String getName() {
                    return null;
                }

                @Override
                public Set getStereotypes() {
                    return AnnotationUtil.getStereotypes(annotatedType, beanManager);
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
                    CreatedByFactory annotation = AnnotationUtil.getAnnotation(annotatedType, beanManager, CreatedByFactory.class);
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

}