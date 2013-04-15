package net.phalanxx.cdiext.factory;

/**
 * Classes annotated by {@link CreatedByFactory} are created by an implementation of this interface.
 *
 * @author rbachlec
 */
public interface Factory {

    /**
     * Method creating an instance of the given class. Additional parameters for instantiation can
     * eventually be given by another annotation on the class to be created.
     *
     * @param <T> type of object to be created
     * @param clazz class of object to be created
     * @return new instance of given class
     */
    <T> T createInstance(Class<T> clazz);

}
