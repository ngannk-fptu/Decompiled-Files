/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.developer;

import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public final class BindingTypeFeature
extends WebServiceFeature {
    public static final String ID = "http://jax-ws.dev.java.net/features/binding";
    private final String bindingId;

    public BindingTypeFeature(String bindingId) {
        this.bindingId = bindingId;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public String getBindingId() {
        return this.bindingId;
    }
}

