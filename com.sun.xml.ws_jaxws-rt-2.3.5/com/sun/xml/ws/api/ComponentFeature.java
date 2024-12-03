/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api;

import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ServiceSharedFeatureMarker;
import javax.xml.ws.WebServiceFeature;

public class ComponentFeature
extends WebServiceFeature
implements ServiceSharedFeatureMarker {
    private final Component component;
    private final Target target;

    public ComponentFeature(Component component) {
        this(component, Target.CONTAINER);
    }

    public ComponentFeature(Component component, Target target) {
        this.enabled = true;
        this.component = component;
        this.target = target;
    }

    public String getID() {
        return ComponentFeature.class.getName();
    }

    public Component getComponent() {
        return this.component;
    }

    public Target getTarget() {
        return this.target;
    }

    public static enum Target {
        CONTAINER,
        ENDPOINT,
        SERVICE,
        STUB;

    }
}

