package net.phalanxx.cdiext.scope;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * This class can be used to obtain or dispose a singleton instance for a given class.
 *
 * @author rbachlec
 */
public class DisposeableSingletonContext {

    private @Inject BeanManager beanManager;

    /**
     * If there has already been created a singleton instance for the given type this instance is
     * returned. Otherwise a new instance is created and put in the {@link DisposeableSingleton}
     * context.
     *
     * @param <T> type of singleton to be created
     * @param type class of singleton to be created
     * @return disposeable singleton instance
     */
    public <T> T getSingleton(Class<T> type) {
        T result = null;
        Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type));

        if (bean != null) {
            Class<? extends Annotation> scope = bean.getScope();
            if (DisposeableSingleton.class.equals(scope)) {
                CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
                result = (T) beanManager.getReference(bean, type, creationalContext);
            }
        }
        return result;
    }

    /**
     * Disposes a given singleton instance. If the given object is not present in the
     * {@link DisposeableSingleton} context an exception is thrown.
     *
     * @param <T> type of the given singleton
     * @param singleton disposeable singleton instance
     */
    public <T> void disposeSingleton(T singleton) {
        DisposeableSingletonContextImpl context =
                (DisposeableSingletonContextImpl) beanManager.getContext(DisposeableSingleton.class);
        context.dispose(singleton);
    }

    /**
     * Checks if the given object is in the {@link DisposeableSingleton} context.
     *
     * @param <T> type of the object to search for
     * @param singleton object to be searched for
     * @return true/false
     */
    public <T> Boolean contains(T singleton) {
        DisposeableSingletonContextImpl context =
                (DisposeableSingletonContextImpl) beanManager.getContext(DisposeableSingleton.class);
        return context.contains(singleton);
    }

}
