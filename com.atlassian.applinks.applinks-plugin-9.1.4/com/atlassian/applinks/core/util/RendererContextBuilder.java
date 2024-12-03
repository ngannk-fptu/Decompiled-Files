/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.applinks.core.util;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

public class RendererContextBuilder {
    private final Map<String, Object> context = new HashMap<String, Object>();

    public RendererContextBuilder() {
    }

    public RendererContextBuilder(Map<String, Object> context) {
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public RendererContextBuilder put(String name, Object value) {
        if (value != null) {
            this.context.put(name, value);
        }
        return this;
    }

    public Map<String, Object> build() {
        return ImmutableMap.copyOf(this.context);
    }
}

