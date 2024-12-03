/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.api.fastinfoset;

import com.sun.xml.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public class FastInfosetFeature
extends WebServiceFeature {
    public static final String ID = "http://java.sun.com/xml/ns/jaxws/fastinfoset";

    public FastInfosetFeature() {
        this.enabled = true;
    }

    @FeatureConstructor(value={"enabled"})
    public FastInfosetFeature(boolean enabled) {
        this.enabled = enabled;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }
}

