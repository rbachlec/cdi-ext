package net.phalanxx.cdiext.factory;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import net.phalanxx.cdiext.util.AnnotationUtil;

/**
 * A portable extension base class called by CDI on startup to find {@link CreatedByFactory}
 * annotated classes. For each class found the extension generates a special bean responsible for
 * instantiation of the annotated class. Sometimes it's difficult to write a producer method because
 * the type to be created is not known at compile time. So someone can write a generic factory for a
 * specific class.
 */
public class FactoryExtension implements Extension {

    private Set<AnnotatedType<Object>> toBeCreatedByFactory = new HashSet<>();

    void processAnnotatedType(@Observes final ProcessAnnotatedType<Object> pat, final BeanManager beanManager) {
        final AnnotatedType<Object> annotatedType = pat.getAnnotatedType();
        if (AnnotationUtil.isAnnotationPresent(annotatedType, beanManager, CreatedByFactory.class)) {
            toBeCreatedByFactory.add(annotatedType);
            pat.veto();
        }
    }

    void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, final BeanManager beanManager) {
        for (final AnnotatedType<Object> annotatedType : toBeCreatedByFactory) {
            InjectionTarget<Object> injectionTarget = beanManager.createInjectionTarget(annotatedType);

            Bean<Object> bean = new GeneratedBean<>(annotatedType, injectionTarget, beanManager);
            abd.addBean(bean);
        }

        toBeCreatedByFactory.clear();
    }

}
