/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 */
package com.sun.xml.ws.model;

import com.sun.istack.localization.Localizable;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public class RuntimeModelerException
extends JAXWSExceptionBase {
    public RuntimeModelerException(String key, Object ... args) {
        super(key, args);
    }

    public RuntimeModelerException(Throwable throwable) {
        super(throwable);
    }

    public RuntimeModelerException(Localizable arg) {
        super("nestedModelerError", arg);
    }

    @Override
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.ws.resources.modeler";
    }
}

