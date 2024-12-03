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
import com.sun.xml.ws.api.ha.StickyFeature;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public class StatefulFeature
extends WebServiceFeature
implements StickyFeature {
    public static final String ID = "http://jax-ws.dev.java.net/features/stateful";

    @FeatureConstructor
    public StatefulFeature() {
        this.enabled = true;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }
}

