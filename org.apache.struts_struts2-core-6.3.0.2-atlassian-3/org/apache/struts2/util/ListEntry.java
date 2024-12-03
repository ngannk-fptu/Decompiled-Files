/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

public class ListEntry {
    private final Object key;
    private final Object value;
    private final boolean isSelected;

    public ListEntry(Object key, Object value, boolean isSelected) {
        this.key = key;
        this.value = value;
        this.isSelected = isSelected;
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public Object getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }
}

