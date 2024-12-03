/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import java.util.Map;

public interface ConversionAnnotationProcessor {
    public void process(Map<String, Object> var1, TypeConversion var2, String var3);
}

