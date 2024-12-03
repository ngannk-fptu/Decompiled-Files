/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model;

import net.sf.ehcache.config.generator.model.NodeAttribute;

public class SimpleNodeAttribute
implements NodeAttribute {
    private final String name;
    private String value;
    private String defaultValue;
    private boolean optional = true;

    public SimpleNodeAttribute(String name) {
        this(name, (String)null);
    }

    public SimpleNodeAttribute(String name, Enum value) {
        this(name, value.name().toLowerCase());
    }

    public SimpleNodeAttribute(String name, int value) {
        this(name, String.valueOf(value));
    }

    public SimpleNodeAttribute(String name, long value) {
        this(name, String.valueOf(value));
    }

    public SimpleNodeAttribute(String name, boolean value) {
        this(name, String.valueOf(value));
    }

    public SimpleNodeAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeAttribute)) {
            return false;
        }
        NodeAttribute other = (NodeAttribute)obj;
        return !(this.name == null ? other.getName() != null : !this.name.equals(other.getName()));
    }

    public String toString() {
        return "SimpleAttribute [name=" + this.name + "]";
    }

    @Override
    public SimpleNodeAttribute optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    @Override
    public SimpleNodeAttribute defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public SimpleNodeAttribute defaultValue(boolean defaultValue) {
        return this.defaultValue(String.valueOf(defaultValue));
    }

    public SimpleNodeAttribute defaultValue(int defaultValue) {
        return this.defaultValue(String.valueOf(defaultValue));
    }

    public SimpleNodeAttribute defaultValue(Enum defaultValue) {
        return this.defaultValue(defaultValue.name().toLowerCase());
    }

    public SimpleNodeAttribute defaultValue(long defaultValue) {
        return this.defaultValue(String.valueOf(defaultValue));
    }
}

