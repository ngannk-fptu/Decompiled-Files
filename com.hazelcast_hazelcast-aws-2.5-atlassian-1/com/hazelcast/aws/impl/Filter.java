/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aws.impl;

import java.util.HashMap;
import java.util.Map;

public class Filter {
    private Map<String, String> filters = new HashMap<String, String>();
    private int index = 1;

    public void addFilter(String name, String value) {
        this.filters.put("Filter." + this.index + ".Name", name);
        this.filters.put("Filter." + this.index + ".Value.1", value);
        ++this.index;
    }

    public Map<String, String> getFilters() {
        return this.filters;
    }
}

