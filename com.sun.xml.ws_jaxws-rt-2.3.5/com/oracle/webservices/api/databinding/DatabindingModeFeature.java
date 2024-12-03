/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.oracle.webservices.api.databinding;

import com.sun.xml.ws.api.ServiceSharedFeatureMarker;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.WebServiceFeature;

public class DatabindingModeFeature
extends WebServiceFeature
implements ServiceSharedFeatureMarker {
    public static final String ID = "http://jax-ws.java.net/features/databinding";
    public static final String GLASSFISH_JAXB = "glassfish.jaxb";
    private String mode;
    private Map<String, Object> properties;

    public DatabindingModeFeature(String mode) {
        this.mode = mode;
        this.properties = new HashMap<String, Object>();
    }

    public String getMode() {
        return this.mode;
    }

    public String getID() {
        return ID;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public static Builder builder() {
        return new Builder(new DatabindingModeFeature(null));
    }

    public static final class Builder {
        private final DatabindingModeFeature o;

        Builder(DatabindingModeFeature x) {
            this.o = x;
        }

        public DatabindingModeFeature build() {
            return this.o;
        }

        public Builder value(String x) {
            this.o.mode = x;
            return this;
        }
    }
}

