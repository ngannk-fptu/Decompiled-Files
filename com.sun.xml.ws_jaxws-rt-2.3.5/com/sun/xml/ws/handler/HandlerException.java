/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.handler;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class HandlerException
extends JAXWSExceptionBase {
    public HandlerException(String key, Object ... args) {
        super(key, args);
    }

    public HandlerException(Throwable throwable) {
        super(throwable);
    }

    public HandlerException(Localizable arg) {
        super("handler.nestedError", arg);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.handler";
    }
}

