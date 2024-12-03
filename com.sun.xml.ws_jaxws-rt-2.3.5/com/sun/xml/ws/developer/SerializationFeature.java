/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

public class SerializationFeature
extends WebServiceFeature {
    public static final String ID = "http://jax-ws.java.net/features/serialization";
    private final String encoding;

    public SerializationFeature() {
        this("");
    }

    @FeatureConstructor(value={"encoding"})
    public SerializationFeature(String encoding) {
        this.encoding = encoding;
    }

    public String getID() {
        return ID;
    }

    public String getEncoding() {
        return this.encoding;
    }
}

