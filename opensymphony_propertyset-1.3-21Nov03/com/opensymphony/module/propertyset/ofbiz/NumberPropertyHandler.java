/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ofbiz;

import com.opensymphony.module.propertyset.IllegalPropertyException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import com.opensymphony.module.propertyset.ofbiz.PropertyHandler;

public class NumberPropertyHandler
implements PropertyHandler {
    private static final Long ZERO = new Long(0L);

    public Object processGet(int type, Object input) throws PropertyException {
        if (input instanceof Long) {
            Long value = (Long)input;
            switch (type) {
                case 1: {
                    return value.equals(ZERO) ? Boolean.FALSE : Boolean.TRUE;
                }
                case 2: {
                    return new Integer(value.intValue());
                }
                case 3: {
                    return value;
                }
            }
            throw new PropertyImplementationException("Cannot retrieve this type of property");
        }
        throw new PropertyImplementationException("Unexepected type of property");
    }

    public Object processSet(int type, Object input) throws PropertyException {
        if (input == null) {
            return new Long(0L);
        }
        try {
            switch (type) {
                case 1: {
                    return new Long((Boolean)input != false ? 1L : 0L);
                }
                case 2: 
                case 3: {
                    return new Long(((Number)input).longValue());
                }
            }
            throw new PropertyImplementationException("Cannot store this type of property");
        }
        catch (ClassCastException e) {
            throw new IllegalPropertyException("Cannot cast value to appropiate type");
        }
    }
}

