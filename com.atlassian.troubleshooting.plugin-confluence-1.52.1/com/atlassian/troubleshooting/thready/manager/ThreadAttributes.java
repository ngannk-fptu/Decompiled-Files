/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.troubleshooting.thready.manager;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThreadAttributes {
    private final String baseThreadName;
    private final Map<String, List<String>> attributes = new LinkedHashMap<String, List<String>>();

    public ThreadAttributes(String baseThreadName) {
        this.baseThreadName = baseThreadName;
    }

    public void clearAll() {
        this.attributes.clear();
    }

    public String getThreadName() {
        return this.baseThreadName + this.attributes.entrySet().stream().map(e -> " " + (String)e.getKey() + ": " + String.join((CharSequence)", ", (Iterable)e.getValue())).collect(Collectors.joining(";"));
    }

    public void clear(String key) {
        this.attributes.remove(key);
    }

    public void add(String key, String value) {
        this.attributes.computeIfAbsent(key, k -> new ArrayList()).add(value);
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public void put(String key, String value) {
        this.attributes.put(key, Lists.newArrayList((Object[])new String[]{value}));
    }
}

