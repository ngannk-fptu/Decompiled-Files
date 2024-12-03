/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api.message;

import javax.xml.ws.WebServiceFeature;

public class SuppressAutomaticWSARequestHeadersFeature
extends WebServiceFeature {
    public SuppressAutomaticWSARequestHeadersFeature() {
        this.enabled = true;
    }

    public String getID() {
        return SuppressAutomaticWSARequestHeadersFeature.class.toString();
    }
}

