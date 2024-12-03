/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.protocol.xml;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class XMLMessageException
extends JAXWSExceptionBase {
    public XMLMessageException(String key, Object ... args) {
        super(key, args);
    }

    public XMLMessageException(Throwable throwable) {
        super(throwable);
    }

    public XMLMessageException(Localizable arg) {
        super("server.rt.err", arg);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.xmlmessage";
    }
}

