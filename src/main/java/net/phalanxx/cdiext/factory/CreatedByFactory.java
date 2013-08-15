package net.phalanxx.cdiext.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By giving this annotation on a class the {@link FactoryExtension} creates a special bean for
 * instantiating the annotated class. Sometimes it's difficult to write a producer method because
 * the type to be created is not known at compile time. With this extension someone can easily
 * implement a generic factory for a specific class.
 *
 * @author Roland Bachlechner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CreatedByFactory {

    /**
     * The class of the factory to be used for creation of annotated types.
     *
     * @return class implementing the {@link Factory} interface
     */
    Class<? extends Factory> factory();

}
