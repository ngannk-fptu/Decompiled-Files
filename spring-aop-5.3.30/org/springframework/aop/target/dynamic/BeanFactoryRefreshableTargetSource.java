/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.util.Assert
 */
package org.springframework.aop.target.dynamic;

import org.springframework.aop.target.dynamic.AbstractRefreshableTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

public class BeanFactoryRefreshableTargetSource
extends AbstractRefreshableTargetSource {
    private final BeanFactory beanFactory;
    private final String beanName;

    public BeanFactoryRefreshableTargetSource(BeanFactory beanFactory, String beanName) {
        Assert.notNull((Object)beanFactory, (String)"BeanFactory is required");
        Assert.notNull((Object)beanName, (String)"Bean name is required");
        this.beanFactory = beanFactory;
        this.beanName = beanName;
    }

    @Override
    protected final Object freshTarget() {
        return this.obtainFreshBean(this.beanFactory, this.beanName);
    }

    protected Object obtainFreshBean(BeanFactory beanFactory, String beanName) {
        return beanFactory.getBean(beanName);
    }
}

