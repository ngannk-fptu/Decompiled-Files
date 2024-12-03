/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

public abstract class WebServiceFeature {
    protected boolean enabled = false;

    public abstract String getID();

    protected WebServiceFeature() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

