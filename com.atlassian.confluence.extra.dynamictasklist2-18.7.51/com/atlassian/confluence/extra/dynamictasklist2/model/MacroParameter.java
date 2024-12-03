/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

public class MacroParameter {
    private final String name;
    private final Object defaultValue;
    private final Type type;
    private Object value;

    public MacroParameter(String name, Type type, boolean defaultValue) {
        this(name, type, (Object)defaultValue);
    }

    public MacroParameter(String name, Type type, Object defaultValue) {
        this(name, type, defaultValue, defaultValue);
    }

    public MacroParameter(String name, Type type, Object defaultValue, Object value) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isDefault() {
        return this.defaultValue.equals(this.value);
    }

    public static class Type {
        public static final Type BOOLEAN = new Type();
        public static final Type STRING = new Type();
        public static final Type SORT = new Type();

        private Type() {
        }
    }
}

