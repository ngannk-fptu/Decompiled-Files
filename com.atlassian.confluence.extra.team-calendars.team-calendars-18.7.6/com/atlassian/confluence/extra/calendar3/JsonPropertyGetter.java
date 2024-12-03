/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3;

import org.json.JSONObject;

public interface JsonPropertyGetter<V> {
    public V getProperty(JSONObject var1, String var2, boolean var3, boolean var4, Object var5);
}

