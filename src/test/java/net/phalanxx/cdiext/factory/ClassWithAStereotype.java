package net.phalanxx.cdiext.factory;

import javax.inject.Inject;
import javax.inject.Named;

@StereotypeForTest
@Named("ClassWithAStereotype")
public class ClassWithAStereotype {

    @Inject ClassWithApplicationScope classWithApplicationScope;

    public ClassWithAStereotype() {
        super();
    }

}
