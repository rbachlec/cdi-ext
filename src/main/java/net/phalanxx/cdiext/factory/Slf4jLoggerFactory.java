package net.phalanxx.cdiext.factory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

/**
 * Factory class for logger instances.
 *
 * @author rbachlec
 */
public class Slf4jLoggerFactory {

    /**
     * Producer method creating slf4j logger instances for injection.
     *
     * @param injectionPoint point of injection
     * @return slf4j logger instance
     */
    @Produces
    public Logger createLogger(final InjectionPoint injectionPoint) {
        String name = injectionPoint.getMember().getDeclaringClass().getName();
        return org.slf4j.LoggerFactory.getLogger(name);
    }
}
