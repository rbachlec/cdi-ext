package net.phalanxx.cdiext.util.test;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

@Alternative
public class AlternativeBean {

    @Inject private DependentScopedBean dependentScopedBean;

    public AlternativeBean() {
        super();
    }

    public DependentScopedBean getDependentScopedBean() {
        return dependentScopedBean;
    }

}
