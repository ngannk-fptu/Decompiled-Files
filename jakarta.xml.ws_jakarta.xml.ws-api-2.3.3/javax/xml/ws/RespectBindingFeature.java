/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import javax.xml.ws.WebServiceFeature;

public final class RespectBindingFeature
extends WebServiceFeature {
    public static final String ID = "javax.xml.ws.RespectBindingFeature";

    public RespectBindingFeature() {
        this.enabled = true;
    }

    public RespectBindingFeature(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getID() {
        return ID;
    }
}

