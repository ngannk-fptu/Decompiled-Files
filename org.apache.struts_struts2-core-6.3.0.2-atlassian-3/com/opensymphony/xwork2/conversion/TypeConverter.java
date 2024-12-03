/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion;

import java.lang.reflect.Member;
import java.util.Map;

public interface TypeConverter {
    public static final Object NO_CONVERSION_POSSIBLE = "ognl.NoConversionPossible";
    @Deprecated
    public static final String TYPE_CONVERTER_CONTEXT_KEY = "_typeConverter";

    public Object convertValue(Map<String, Object> var1, Object var2, Member var3, String var4, Object var5, Class var6);
}

