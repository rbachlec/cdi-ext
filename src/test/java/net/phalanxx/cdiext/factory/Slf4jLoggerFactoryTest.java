package net.phalanxx.cdiext.factory;

import static org.fest.assertions.Assertions.assertThat;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

@RunWith(Arquillian.class)
public class Slf4jLoggerFactoryTest {

    @Inject private Logger log;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addClass(Slf4jLoggerFactory.class)
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void loggerHasBeenInjected() {
        assertThat(log).isNotNull();
    }

    @Test
    public void loggerHasCorrectName() {
        assertThat(log).isNotNull();
        assertThat(log.getName()).isEqualTo(getClass().getName());
    }

}
