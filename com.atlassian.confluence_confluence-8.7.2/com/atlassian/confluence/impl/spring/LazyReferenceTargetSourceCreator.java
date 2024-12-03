/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator
 *  org.springframework.aop.target.LazyInitTargetSource
 */
package com.atlassian.confluence.impl.spring;

import org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator;
import org.springframework.aop.target.LazyInitTargetSource;

public final class LazyReferenceTargetSourceCreator
extends AbstractBeanFactoryBasedTargetSourceCreator {
    protected LazyInitTargetSource createBeanFactoryBasedTargetSource(Class<?> beanClass, String beanName) {
        return new LazyInitTargetSource();
    }
}

