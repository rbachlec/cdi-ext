package net.phalanxx.cdiext.scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Scope;

/**
 * Scope containing singletons that can be destroyed manually by using an injected
 * instance of {@link DestroyableSingletonContext}.
 *
 * @author rbachlec
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface DestroyableSingleton {}
