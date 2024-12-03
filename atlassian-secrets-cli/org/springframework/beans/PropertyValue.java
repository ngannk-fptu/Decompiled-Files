/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.io.Serializable;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class PropertyValue
extends BeanMetadataAttributeAccessor
implements Serializable {
    private final String name;
    @Nullable
    private final Object value;
    private boolean optional = false;
    private boolean converted = false;
    @Nullable
    private Object convertedValue;
    @Nullable
    volatile Boolean conversionNecessary;
    @Nullable
    volatile transient Object resolvedTokens;

    public PropertyValue(String name, @Nullable Object value) {
        Assert.notNull((Object)name, "Name must not be null");
        this.name = name;
        this.value = value;
    }

    public PropertyValue(PropertyValue original) {
        Assert.notNull((Object)original, "Original must not be null");
        this.name = original.getName();
        this.value = original.getValue();
        this.optional = original.isOptional();
        this.converted = original.converted;
        this.convertedValue = original.convertedValue;
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        this.setSource(original.getSource());
        this.copyAttributesFrom(original);
    }

    public PropertyValue(PropertyValue original, @Nullable Object newValue) {
        Assert.notNull((Object)original, "Original must not be null");
        this.name = original.getName();
        this.value = newValue;
        this.optional = original.isOptional();
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        this.setSource(original);
        this.copyAttributesFrom(original);
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    public PropertyValue getOriginalPropertyValue() {
        PropertyValue original = this;
        Object source = this.getSource();
        while (source instanceof PropertyValue && source != original) {
            original = (PropertyValue)source;
            source = original.getSource();
        }
        return original;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public synchronized boolean isConverted() {
        return this.converted;
    }

    public synchronized void setConvertedValue(@Nullable Object value) {
        this.converted = true;
        this.convertedValue = value;
    }

    @Nullable
    public synchronized Object getConvertedValue() {
        return this.convertedValue;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PropertyValue)) {
            return false;
        }
        PropertyValue otherPv = (PropertyValue)other;
        return this.name.equals(otherPv.name) && ObjectUtils.nullSafeEquals(this.value, otherPv.value) && ObjectUtils.nullSafeEquals(this.getSource(), otherPv.getSource());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
    }

    public String toString() {
        return "bean property '" + this.name + "'";
    }
}

