package net.phalanxx.cdiext.scope;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * This class can be used to obtain or destroy a singleton instance for a given class.
 *
 * @author rbachlec
 */
public class DestroyableSingletonContext {

    private @Inject BeanManager beanManager;

    /**
     * If there has already been created a singleton instance for the given type this
     * instance is returned. Otherwise a new instance is created and put in the
     * {@link DestroyableSingleton} context.
     *
     * @param <T> type of singleton to be created
     * @param type class of singleton to be created
     * @return destroyable singleton instance
     */
    public <T> T getSingleton(Class<T> type) {
        T result = null;
        Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type));
        if (bean != null) {
            CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
            if (creationalContext != null) {
                result = (T) beanManager.getReference(bean, type, creationalContext);
            }
        }
        return result;
    }

    /**
     * Destroys a given singleton instance. If the given object is not present in the
     * {@link DestroyableSingleton} context an exception is thrown.
     *
     * @param <T> type of the given singleton
     * @param singleton destroyable singleton instance
     */
    public <T> void destroySingleton(T singleton) {
        DestroyableSingletonContextImpl context =
                (DestroyableSingletonContextImpl) beanManager.getContext(DestroyableSingleton.class);
        context.destroy(singleton);
    }

    /**
     * Checks if the given object is in the {@link DestroyableSingleton} context.
     *
     * @param <T> type of the object to search for
     * @param singleton object to be searched for
     * @return true/false
     */
    public <T> Boolean contains(T singleton) {
        DestroyableSingletonContextImpl context =
                (DestroyableSingletonContextImpl) beanManager.getContext(DestroyableSingleton.class);
        return context.contains(singleton);
    }

}
