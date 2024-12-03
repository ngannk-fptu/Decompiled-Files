/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.client;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class ClientTransportException
extends JAXWSExceptionBase {
    public ClientTransportException(Localizable msg) {
        super(msg);
    }

    public ClientTransportException(Localizable msg, Throwable cause) {
        super(msg, cause);
    }

    public ClientTransportException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.client";
    }
}

