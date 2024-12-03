/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class GenericBeanDefinition
extends AbstractBeanDefinition {
    @Nullable
    private String parentName;

    public GenericBeanDefinition() {
    }

    public GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }

    @Override
    public void setParentName(@Nullable String parentName) {
        this.parentName = parentName;
    }

    @Override
    @Nullable
    public String getParentName() {
        return this.parentName;
    }

    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GenericBeanDefinition)) {
            return false;
        }
        GenericBeanDefinition that = (GenericBeanDefinition)other;
        return ObjectUtils.nullSafeEquals((Object)this.parentName, (Object)that.parentName) && super.equals(other);
    }

    @Override
    public String toString() {
        if (this.parentName != null) {
            return "Generic bean with parent '" + this.parentName + "': " + super.toString();
        }
        return "Generic bean: " + super.toString();
    }
}

