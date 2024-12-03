/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.keymanager;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class SortedProperties
extends Properties {
    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        TreeSet<Map.Entry<Object, Object>> result = new TreeSet<Map.Entry<Object, Object>>(Comparator.comparing(e -> e.getKey().toString()));
        result.addAll(super.entrySet());
        return result;
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
    }
}

