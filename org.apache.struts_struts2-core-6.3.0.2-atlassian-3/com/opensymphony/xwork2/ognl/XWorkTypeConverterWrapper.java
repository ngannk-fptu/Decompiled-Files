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

public class XWorkTypeConverterWrapper
implements com.opensymphony.xwork2.conversion.TypeConverter {
    private final TypeConverter typeConverter;

    public XWorkTypeConverterWrapper(TypeConverter conv) {
        this.typeConverter = conv;
    }

    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        return this.typeConverter.convertValue(context, target, member, propertyName, value, toType);
    }
}

