/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class $ToStringBuilder {
    final Map<String, Object> map = new LinkedHashMap<String, Object>();
    final String name;

    public $ToStringBuilder(Class type) {
        this.name = type.getSimpleName();
    }

    public $ToStringBuilder add(String name, Object value) {
        if (this.map.put(name, value) != null) {
            throw new RuntimeException("Duplicate names: " + name);
        }
        return this;
    }

    public String toString() {
        return this.name + this.map.toString().replace('{', '[').replace('}', ']');
    }
}

