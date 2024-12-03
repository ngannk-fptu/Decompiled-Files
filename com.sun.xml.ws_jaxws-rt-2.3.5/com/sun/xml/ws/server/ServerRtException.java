/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.server;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class ServerRtException
extends JAXWSExceptionBase {
    public ServerRtException(String key, Object ... args) {
        super(key, args);
    }

    public ServerRtException(Throwable throwable) {
        super(throwable);
    }

    public ServerRtException(Localizable arg) {
        super("server.rt.err", arg);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.server";
    }
}

