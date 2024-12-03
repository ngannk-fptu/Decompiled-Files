/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.util;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class UtilException
extends JAXWSExceptionBase {
    public UtilException(String key, Object ... args) {
        super(key, args);
    }

    public UtilException(Throwable throwable) {
        super(throwable);
    }

    public UtilException(Localizable arg) {
        super("nestedUtilError", arg);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.util";
    }
}

