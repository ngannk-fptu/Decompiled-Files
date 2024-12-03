/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.developer.servlet;

import com.sun.xml.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

public class HttpSessionScopeFeature
extends WebServiceFeature {
    public static final String ID = "http://jax-ws.dev.java.net/features/servlet/httpSessionScope";

    @FeatureConstructor
    public HttpSessionScopeFeature() {
        this.enabled = true;
    }

    public String getID() {
        return ID;
    }
}

