/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

final class WSServletException
extends JAXWSExceptionBase {
    public WSServletException(String key, Object ... args) {
        super(key, args);
    }

    public WSServletException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.wsservlet";
    }
}

