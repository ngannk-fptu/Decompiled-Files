/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.soap.SOAPHeaderElement
 */
package com.sun.xml.ws.message.saaj;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.message.DOMHeader;
import javax.xml.soap.SOAPHeaderElement;

public final class SAAJHeader
extends DOMHeader<SOAPHeaderElement> {
    public SAAJHeader(SOAPHeaderElement header) {
        super(header);
    }

    @Override
    @NotNull
    public String getRole(@NotNull SOAPVersion soapVersion) {
        String v = this.getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
        if (v == null || v.equals("")) {
            v = soapVersion.implicitRole;
        }
        return v;
    }
}

