/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.config.BeanReference;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class RuntimeBeanReference
implements BeanReference {
    private final String beanName;
    @Nullable
    private final Class<?> beanType;
    private final boolean toParent;
    @Nullable
    private Object source;

    public RuntimeBeanReference(String beanName) {
        this(beanName, false);
    }

    public RuntimeBeanReference(String beanName, boolean toParent) {
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
        this.beanType = null;
        this.toParent = toParent;
    }

    public RuntimeBeanReference(Class<?> beanType) {
        this(beanType, false);
    }

    public RuntimeBeanReference(Class<?> beanType, boolean toParent) {
        Assert.notNull(beanType, "'beanType' must not be empty");
        this.beanName = beanType.getName();
        this.beanType = beanType;
        this.toParent = toParent;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }

    @Nullable
    public Class<?> getBeanType() {
        return this.beanType;
    }

    public boolean isToParent() {
        return this.toParent;
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
        if (!(other instanceof RuntimeBeanReference)) {
            return false;
        }
        RuntimeBeanReference that = (RuntimeBeanReference)other;
        return this.beanName.equals(that.beanName) && this.beanType == that.beanType && this.toParent == that.toParent;
    }

    public int hashCode() {
        int result = this.beanName.hashCode();
        result = 29 * result + (this.toParent ? 1 : 0);
        return result;
    }

    public String toString() {
        return '<' + this.getBeanName() + '>';
    }
}

