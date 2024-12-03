/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.soap.SOAPBinding$Style
 *  javax.jws.soap.SOAPBinding$Use
 */
package com.sun.xml.ws.api.model.soap;

import com.sun.xml.ws.api.SOAPVersion;
import javax.jws.soap.SOAPBinding;

public abstract class SOAPBinding {
    protected SOAPBinding.Use use = SOAPBinding.Use.LITERAL;
    protected SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;
    protected SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    protected String soapAction = "";

    public SOAPBinding.Use getUse() {
        return this.use;
    }

    public SOAPBinding.Style getStyle() {
        return this.style;
    }

    public SOAPVersion getSOAPVersion() {
        return this.soapVersion;
    }

    public boolean isDocLit() {
        return this.style == SOAPBinding.Style.DOCUMENT && this.use == SOAPBinding.Use.LITERAL;
    }

    public boolean isRpcLit() {
        return this.style == SOAPBinding.Style.RPC && this.use == SOAPBinding.Use.LITERAL;
    }

    public String getSOAPAction() {
        return this.soapAction;
    }
}

