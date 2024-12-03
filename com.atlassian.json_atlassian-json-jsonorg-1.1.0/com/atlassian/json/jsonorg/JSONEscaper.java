/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.json.jsonorg;

import com.atlassian.annotations.PublicApi;
import com.atlassian.json.jsonorg.JSONObject;

@PublicApi
public class JSONEscaper {
    public static String escape(String jsonStringValue) {
        String quotedValue = JSONObject.quote(jsonStringValue);
        return quotedValue.substring(1, quotedValue.length() - 1);
    }
}

