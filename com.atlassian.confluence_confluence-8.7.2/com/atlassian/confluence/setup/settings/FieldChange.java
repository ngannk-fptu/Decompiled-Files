/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

public class FieldChange {
    private final String field;
    private final Object oldValue;
    private final Object newValue;

    public FieldChange(String field, Object oldValue, Object newValue) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getField() {
        return this.field;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public Object getNewValue() {
        return this.newValue;
    }
}

