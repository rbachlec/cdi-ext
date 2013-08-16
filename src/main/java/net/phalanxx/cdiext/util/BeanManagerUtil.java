package net.phalanxx.cdiext.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;

/**
 * Bean manager related util methods. They should be used rarely.
 *
 * @author rbachlec
 */
@ApplicationScoped
public class BeanManagerUtil {

    private @Inject BeanManager beanManager;

    /**
     * Get a single CDI managed instance of a specific class. Return only the first result if multiple beans are
     * available.
     *
     * @param type The class for which to return an instance.
     * @param qualifiers List of qualifier annotations
     * @return The managed instance, or null if none could be provided.
     */
    public <T> T getContextualInstance(final Class<T> type, Annotation... qualifiers) {
        return getContextualInstance(beanManager, type, qualifiers);
    }

    /**
     * Get a single CDI managed instance of a specific class. Return only the first result if multiple beans are
     * available.
     * <p/>
     * <b>NOTE:</b> Using this method should be avoided if container provided injection is available.
     *
     * @param beanManager The bean manager with which to perform the lookup.
     * @param type The class for which to return an instance.
     * @param qualifiers List of qualifier annotations
     * @return The managed instance, or null if none could be provided.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getContextualInstance(final BeanManager beanManager, final Class<T> type,
                                              Annotation... qualifiers) {
        T result = null;
        Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type, qualifiers));
        if (bean != null) {
            CreationalContext<T> context = beanManager.createCreationalContext(bean);
            result = (T) beanManager.getReference(bean, type, context);
        }
        return result;
    }

    /**
     * Get all CDI managed instances of a specific class. Return results in a
     * {@link List} in no specific order.
     *
     * @param type The class for which to return instances.
     * @param qualifiers List of qualifier annotations
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getContextualInstances(final Class<T> type, Annotation... qualifiers) {
        List<T> result = new ArrayList<>();
        for (Bean<?> bean : beanManager.getBeans(type, qualifiers)) {
            CreationalContext<T> context = (CreationalContext<T>) beanManager.createCreationalContext(bean);
            result.add((T) beanManager.getReference(bean, type, context));
        }
        return result;
    }

    /**
     * Does injection magic on the given object.
     *
     * @param <T> type of object to do injection for
     * @param object  object to do injection for
     */
    public <T> void doInjectionsForUnmanagedObject(T object) {
        doInjectionsForUnmanagedObject(beanManager, object);
    }

    /**
     * Does injection magic on the given object.
     *
     * @param beanManager The bean manager with which to perform the lookup.
     * @param <T> type of object to do injection for
     * @param object  object to do injection for
     */
    @SuppressWarnings("unchecked")
    public static <T> void doInjectionsForUnmanagedObject(final BeanManager beanManager, T object) {
        AnnotatedType<T> type = (AnnotatedType<T>) beanManager.createAnnotatedType(object.getClass());
        InjectionTarget<T> target = beanManager.createInjectionTarget(type);
        CreationalContext<T> creationalContext = beanManager.createCreationalContext(null);
        target.inject(object, creationalContext);
    }

}
