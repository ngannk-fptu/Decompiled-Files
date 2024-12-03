/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.runtime.config;

import com.sun.xml.ws.api.FeatureConstructor;
import java.util.List;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public class TubelineFeature
extends WebServiceFeature {
    public static final String ID = "com.sun.xml.ws.runtime.config.TubelineFeature";

    @FeatureConstructor(value={"enabled"})
    public TubelineFeature(boolean enabled) {
        this.enabled = enabled;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    List<String> getTubeFactories() {
        return null;
    }
}

