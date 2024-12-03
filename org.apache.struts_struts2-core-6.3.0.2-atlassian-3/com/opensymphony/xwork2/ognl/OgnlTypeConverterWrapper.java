/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.TypeConverter
 */
package com.opensymphony.xwork2.ognl;

import java.lang.reflect.Member;
import java.util.Map;
import ognl.TypeConverter;

public class OgnlTypeConverterWrapper
implements TypeConverter {
    private final com.opensymphony.xwork2.conversion.TypeConverter typeConverter;

    public OgnlTypeConverterWrapper(com.opensymphony.xwork2.conversion.TypeConverter converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Wrapped type converter cannot be null");
        }
        this.typeConverter = converter;
    }

    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        return this.typeConverter.convertValue(context, target, member, propertyName, value, toType);
    }

    public com.opensymphony.xwork2.conversion.TypeConverter getTarget() {
        return this.typeConverter;
    }
}

