package net.phalanxx.cdiext.factory;

/**
 *
 * @author rbachlec
 */
public interface AnnotatedTypeFactory<A> {

    Object createInstance(A annotation);

}
