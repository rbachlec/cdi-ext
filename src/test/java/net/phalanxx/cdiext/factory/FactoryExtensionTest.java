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

@RunWith(Arquillian.class)
public class FactoryExtensionTest {

    @Inject private Opel opel;
    @Inject private Volkswagen volkswagen;
    @Inject private Mercedes mercedes;

    @Inject private Opel anotherOpel;
    @Inject private Volkswagen anotherVolkswagen;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                         .addClass(CreatedByFactory.class)
                         .addClass(Factory.class)
                         .addClass(FactoryExtension.class)
                         .addClass(GeneratedBean.class)
                         .addPackage(FactoryExtensionTest.class.getPackage())
                         .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void factoryDeliversOpel() {
        assertThat(opel).isNotNull();
        assertThat(opel.getBrand()).isEqualTo(Opel.BRAND);
        assertThat(opel.getFactoryName()).isEqualTo(CarFactory.FACTORY_NAME);
    }

    @Test
    public void factoryDeliversVolkswagen() {
        assertThat(volkswagen).isNotNull();
        assertThat(volkswagen.getBrand()).isEqualTo(Volkswagen.BRAND);
        assertThat(volkswagen.getFactoryName()).isEqualTo(CarFactory.FACTORY_NAME);
    }

    @Test
    public void factoryDoesNotDeliverMercedes() {
        assertThat(mercedes).isNotNull();
        assertThat(mercedes.getBrand()).isEqualTo(Mercedes.BRAND);
        assertThat(mercedes.getFactoryName()).isNull();
    }

    @Test
    public void factoryRespectsApplicationScoped() {
        assertThat(opel).isNotNull();
        assertThat(anotherOpel).isNotNull();
        assertThat(opel).isEqualTo(anotherOpel);
    }

    @Test
    public void factoryRespectsDependentScoped() {
        assertThat(volkswagen).isNotNull();
        assertThat(anotherVolkswagen).isNotNull();
        assertThat(volkswagen).isNotEqualTo(anotherVolkswagen);
    }

}
