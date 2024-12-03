/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.streaming;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class XMLReaderException
extends JAXWSExceptionBase {
    public XMLReaderException(String key, Object ... args) {
        super(key, args);
    }

    public XMLReaderException(Throwable throwable) {
        super(throwable);
    }

    public XMLReaderException(Localizable arg) {
        super("xmlreader.nestedError", arg);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.streaming";
    }
}

