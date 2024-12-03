/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher;

import java.util.Map;

abstract class StringObjectEntry
implements Map.Entry<String, Object> {
    private String key;
    private Object value;

    StringObjectEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        Map.Entry entry = (Map.Entry)obj;
        return this.keyEquals(entry) && this.valueEquals(entry);
    }

    private boolean keyEquals(Map.Entry<?, ?> entry) {
        return this.key == null ? entry.getKey() == null : this.key.equals(entry.getKey());
    }

    private boolean valueEquals(Map.Entry<?, ?> entry) {
        return this.value == null ? entry.getValue() == null : this.value.equals(entry.getValue());
    }

    @Override
    public int hashCode() {
        return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
    }
}

