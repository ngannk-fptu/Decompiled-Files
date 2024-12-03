/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api.pipe;

import javax.xml.ws.WebServiceFeature;

public class SyncStartForAsyncFeature
extends WebServiceFeature {
    public SyncStartForAsyncFeature() {
        this.enabled = true;
    }

    public String getID() {
        return SyncStartForAsyncFeature.class.getSimpleName();
    }
}

