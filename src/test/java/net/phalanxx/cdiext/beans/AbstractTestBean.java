package net.phalanxx.cdiext.beans;


public abstract class AbstractTestBean {

    private Boolean producedByFactory = Boolean.FALSE;

    public AbstractTestBean() {
        super();
    }

    public Boolean getProducedByFactory() {
        return producedByFactory;
    }

    public void setProducedByFactory(Boolean producedByFactory) {
        this.producedByFactory = producedByFactory;
    }

    public abstract String getBeanId();

}
