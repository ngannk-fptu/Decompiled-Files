/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource;

import java.util.Map;

public class QueryParams {
    private final Map<String, String> map;

    private QueryParams(Map<String, String> map) {
        this.map = map;
    }

    public static QueryParams of(Map<String, String> map) {
        return new QueryParams(map);
    }

    public String get(String key) {
        return this.map.get(key);
    }

    public String toString() {
        return "QueryParams{map=" + this.map + '}';
    }
}

