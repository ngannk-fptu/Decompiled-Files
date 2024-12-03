/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterators
 */
package com.atlassian.confluence.util.i18n;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

class SimpleMapResourceBundle
extends ResourceBundle {
    private final Map<String, Object> map;

    SimpleMapResourceBundle(Map<String, Object> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    @Override
    public final Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return this.map.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Iterators.asEnumeration(this.map.keySet().iterator());
    }

    @Override
    protected Set<String> handleKeySet() {
        return this.map.keySet();
    }
}

