/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.encoding.soap;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class SerializationException
extends JAXWSExceptionBase {
    public SerializationException(String key, Object ... args) {
        super(key, args);
    }

    public SerializationException(Localizable arg) {
        super("nestedSerializationError", arg);
    }

    public SerializationException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.encoding";
    }
}

