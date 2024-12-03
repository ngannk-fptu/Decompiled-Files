/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.oracle.webservices.api;

import com.oracle.webservices.api.EnvelopeStyle;
import javax.xml.ws.WebServiceFeature;

public class EnvelopeStyleFeature
extends WebServiceFeature {
    private EnvelopeStyle.Style[] styles;

    public EnvelopeStyleFeature(EnvelopeStyle.Style ... s) {
        this.styles = s;
    }

    public EnvelopeStyle.Style[] getStyles() {
        return this.styles;
    }

    public String getID() {
        return EnvelopeStyleFeature.class.getName();
    }
}

