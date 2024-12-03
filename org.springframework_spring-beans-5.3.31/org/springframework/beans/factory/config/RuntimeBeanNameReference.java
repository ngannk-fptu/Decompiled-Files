/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.config.BeanReference;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class RuntimeBeanNameReference
implements BeanReference {
    private final String beanName;
    @Nullable
    private Object source;

    public RuntimeBeanNameReference(String beanName) {
        Assert.hasText((String)beanName, (String)"'beanName' must not be empty");
        this.beanName = beanName;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
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
        if (!(other instanceof RuntimeBeanNameReference)) {
            return false;
        }
        RuntimeBeanNameReference that = (RuntimeBeanNameReference)other;
        return this.beanName.equals(that.beanName);
    }

    public int hashCode() {
        return this.beanName.hashCode();
    }

    public String toString() {
        return '<' + this.getBeanName() + '>';
    }
}

