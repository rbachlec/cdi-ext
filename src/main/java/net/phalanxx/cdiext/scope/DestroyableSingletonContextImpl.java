package net.phalanxx.cdiext.scope;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

/**
 * Context implementation for the {@link DestroyableSingleton} scope.
 *
 * @author rbachlec
 */
public class DestroyableSingletonContextImpl implements Context {

    private ConcurrentHashMap<String, ContextualInstance<?>> beanStore =
            new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> getScope() {
        return DestroyableSingleton.class;
    }

    /** {@inheritDoc} */
    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        Class beanClass;
// TODO producer methods
//        if (bean instanceof ProducerMethod) {
//            beanClass = ((ProducerMethod) bean).getType();
//        } else {
//            beanClass = bean.getBeanClass();
//        }
        beanClass = bean.getBeanClass();

        T instance;
        ContextualInstance<?> contextualInstance = beanStore.get(beanClass.getCanonicalName());
        if (contextualInstance == null) {
            instance = contextual.create(creationalContext);
            contextualInstance = new DestroyableSingletonInstance(instance, creationalContext, contextual);
            beanStore.put(beanClass.getCanonicalName(), contextualInstance);
        } else {
            instance = (T) contextualInstance.getInstance();
        }

        return instance;
    }

    /**
     * This method is not supported.
     */
    @Override
    public <T> T get(Contextual<T> contextual) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Removes a given instance from the {@link DestroyableSingleton} context.
     *
     * @param <T> type of instance to be destroyed
     * @param instance instance to be destroyed
     */
    protected <T> void destroy(T instance) {
        if (!contains(instance)) {
            throw new IllegalArgumentException("The given instance could not be found in " +
                                               "the DestroyableSingletonContext.");
        }

        beanStore.remove(instance.getClass().getCanonicalName());
    }

    /**
     * Checks if the given object is in the {@link DestroyableSingleton} context.
     *
     * @param <T> type of the object to search for
     * @param singleton object to be searched for
     * @return true/false
     */
    protected <T> Boolean contains(T instance) {
        Boolean found = true;
        String className = instance.getClass().getCanonicalName();
        ContextualInstance<?> instanceFound = beanStore.get(className);
        if (instanceFound == null || instanceFound.getInstance() != instance) {
            found = false;
        }

        return found;
    }

    /**
     * A container class for destroyable singleton instances.
     *
     * @param <T> type of instance in container
     */
    private class DestroyableSingletonInstance<T> implements ContextualInstance<T> {
        private T instance;
        private CreationalContext<T> creationalContext;
        private Contextual<T> contextual;

        public DestroyableSingletonInstance(T instance, CreationalContext<T> creationalContext, Contextual<T> contextual) {
            this.instance = instance;
            this.creationalContext = creationalContext;
            this.contextual = contextual;
        }

        @Override
        public T getInstance() {
            return instance;
        }

        @Override
        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }

        @Override
        public Contextual<T> getContextual() {
            return contextual;
        }
    }

}
