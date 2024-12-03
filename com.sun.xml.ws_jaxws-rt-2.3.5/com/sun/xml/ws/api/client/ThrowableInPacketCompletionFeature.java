/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api.client;

import javax.xml.ws.WebServiceFeature;

public class ThrowableInPacketCompletionFeature
extends WebServiceFeature {
    public ThrowableInPacketCompletionFeature() {
        this.enabled = true;
    }

    public String getID() {
        return ThrowableInPacketCompletionFeature.class.getName();
    }
}

