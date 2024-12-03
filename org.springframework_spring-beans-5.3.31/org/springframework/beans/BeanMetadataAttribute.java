/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class BeanMetadataAttribute
implements BeanMetadataElement {
    private final String name;
    @Nullable
    private final Object value;
    @Nullable
    private Object source;

    public BeanMetadataAttribute(String name, @Nullable Object value) {
        Assert.notNull((Object)name, (String)"Name must not be null");
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanMetadataAttribute)) {
            return false;
        }
        BeanMetadataAttribute otherMa = (BeanMetadataAttribute)other;
        return this.name.equals(otherMa.name) && ObjectUtils.nullSafeEquals((Object)this.value, (Object)otherMa.value) && ObjectUtils.nullSafeEquals((Object)this.source, (Object)otherMa.source);
    }

    public int hashCode() {
        return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode((Object)this.value);
    }

    public String toString() {
        return "metadata attribute '" + this.name + "'";
    }
}

