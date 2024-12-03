/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.NamedBean;
import org.springframework.util.Assert;

public class NamedBeanHolder<T>
implements NamedBean {
    private final String beanName;
    private final T beanInstance;

    public NamedBeanHolder(String beanName, T beanInstance) {
        Assert.notNull((Object)beanName, (String)"Bean name must not be null");
        this.beanName = beanName;
        this.beanInstance = beanInstance;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }

    public T getBeanInstance() {
        return this.beanInstance;
    }
}

