/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.conversion.TypeConverter;

public interface TypeConverterCreator {
    public TypeConverter createTypeConverter(String var1) throws Exception;

    public TypeConverter createTypeConverter(Class<?> var1) throws Exception;
}

