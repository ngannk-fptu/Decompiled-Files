/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.rest;

import java.util.Collection;
import java.util.LinkedHashMap;

public class RestEntity
extends LinkedHashMap<String, Object> {
    protected void putIfNotEmpty(String name, Collection value) {
        if (value != null && !value.isEmpty()) {
            this.put(name, value);
        }
    }

    protected void putIfNotNull(String name, Object value) {
        if (value != null) {
            this.put(name, value);
        }
    }
}

