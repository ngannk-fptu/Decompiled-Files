/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api;

import com.sun.xml.ws.api.ComponentFeature;
import com.sun.xml.ws.api.ServiceSharedFeatureMarker;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ComponentsFeature
extends WebServiceFeature
implements ServiceSharedFeatureMarker {
    private final List<ComponentFeature> componentFeatures;

    public ComponentsFeature(List<ComponentFeature> componentFeatures) {
        this.enabled = true;
        this.componentFeatures = componentFeatures;
    }

    public String getID() {
        return ComponentsFeature.class.getName();
    }

    public List<ComponentFeature> getComponentFeatures() {
        return this.componentFeatures;
    }
}

