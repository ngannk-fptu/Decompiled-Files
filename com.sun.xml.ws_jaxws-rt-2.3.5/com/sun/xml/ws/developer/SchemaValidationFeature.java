/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.developer.ValidationErrorHandler;
import com.sun.xml.ws.server.DraconianValidationErrorHandler;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public class SchemaValidationFeature
extends WebServiceFeature {
    public static final String ID = "http://jax-ws.dev.java.net/features/schema-validation";
    private final Class<? extends ValidationErrorHandler> clazz;
    private final boolean inbound;
    private final boolean outbound;

    public SchemaValidationFeature() {
        this(true, true, DraconianValidationErrorHandler.class);
    }

    public SchemaValidationFeature(Class<? extends ValidationErrorHandler> clazz) {
        this(true, true, clazz);
    }

    public SchemaValidationFeature(boolean inbound, boolean outbound) {
        this(inbound, outbound, DraconianValidationErrorHandler.class);
    }

    @FeatureConstructor(value={"inbound", "outbound", "handler"})
    public SchemaValidationFeature(boolean inbound, boolean outbound, Class<? extends ValidationErrorHandler> clazz) {
        this.enabled = true;
        this.inbound = inbound;
        this.outbound = outbound;
        this.clazz = clazz;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public Class<? extends ValidationErrorHandler> getErrorHandler() {
        return this.clazz;
    }

    public boolean isInbound() {
        return this.inbound;
    }

    public boolean isOutbound() {
        return this.outbound;
    }
}

