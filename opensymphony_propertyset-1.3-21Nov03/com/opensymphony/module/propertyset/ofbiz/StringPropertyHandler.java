/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ofbiz;

import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.ofbiz.PropertyHandler;

public class StringPropertyHandler
implements PropertyHandler {
    public Object processGet(int type, Object input) throws PropertyException {
        if ((type == 5 || type == 6) && input instanceof String) {
            return (String)input;
        }
        throw new InvalidPropertyTypeException();
    }

    public Object processSet(int type, Object input) throws PropertyException {
        if ((type == 5 || type == 6) && input instanceof String) {
            return (String)input;
        }
        throw new InvalidPropertyTypeException();
    }
}

