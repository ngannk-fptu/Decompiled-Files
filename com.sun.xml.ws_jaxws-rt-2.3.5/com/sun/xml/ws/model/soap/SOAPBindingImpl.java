/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.soap.SOAPBinding$Style
 */
package com.sun.xml.ws.model.soap;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.model.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding;

public class SOAPBindingImpl
extends SOAPBinding {
    public SOAPBindingImpl() {
    }

    public SOAPBindingImpl(SOAPBinding sb) {
        this.use = sb.getUse();
        this.style = sb.getStyle();
        this.soapVersion = sb.getSOAPVersion();
        this.soapAction = sb.getSOAPAction();
    }

    public void setStyle(SOAPBinding.Style style) {
        this.style = style;
    }

    public void setSOAPVersion(SOAPVersion version) {
        this.soapVersion = version;
    }

    public void setSOAPAction(String soapAction) {
        this.soapAction = soapAction;
    }
}

