package net.phalanxx.cdiext.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By giving this annotation on a class the {@link FactoryExtension} creates a special bean
 * for instantiating the annotated class. Sometimes it's difficult to write a producer method
 * because the type to be created is not known at compile time. So someone can write a
 * generic factory for a specific class.
 *
 * @author rbachlec
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CreatedByFactory {
    Class<? extends Factory> factory();
}
