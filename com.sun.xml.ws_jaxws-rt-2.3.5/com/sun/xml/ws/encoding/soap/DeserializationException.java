/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.encoding.soap;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class DeserializationException
extends JAXWSExceptionBase {
    public DeserializationException(String key, Object ... args) {
        super(key, args);
    }

    public DeserializationException(Throwable throwable) {
        super(throwable);
    }

    public DeserializationException(Localizable arg) {
        super("nestedDeserializationError", arg);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.encoding";
    }
}

