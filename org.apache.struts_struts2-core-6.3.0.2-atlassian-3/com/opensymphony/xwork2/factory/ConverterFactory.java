/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.conversion.TypeConverter;
import java.util.Map;

public interface ConverterFactory {
    public TypeConverter buildConverter(Class<? extends TypeConverter> var1, Map<String, Object> var2) throws Exception;
}

