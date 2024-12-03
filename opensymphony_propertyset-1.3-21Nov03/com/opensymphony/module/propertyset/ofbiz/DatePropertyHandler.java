/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ofbiz;

import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.ofbiz.PropertyHandler;
import java.sql.Timestamp;
import java.util.Date;

public class DatePropertyHandler
implements PropertyHandler {
    public Object processGet(int type, Object input) throws PropertyException {
        if (type == 7) {
            return input;
        }
        throw new InvalidPropertyTypeException();
    }

    public Object processSet(int type, Object input) throws PropertyException {
        if (type == 7) {
            if (input instanceof Date) {
                return new Timestamp(((Date)input).getTime());
            }
            if (input instanceof java.sql.Date) {
                return new Timestamp(((java.sql.Date)input).getTime());
            }
            if (input instanceof Timestamp) {
                return input;
            }
        }
        throw new InvalidPropertyTypeException();
    }
}

