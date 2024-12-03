/*
 * Decompiled with CFR 0.152.
 */
package com.benryan.conversion;

import java.util.Map;

public interface Converter {
    public static final String CACHE_SERVLET_PATH = "/plugins/servlet/benryanconversion";
    public static final String TYPE_KEY = "type";

    public String execute(Map<String, Object> var1) throws Exception;
}

