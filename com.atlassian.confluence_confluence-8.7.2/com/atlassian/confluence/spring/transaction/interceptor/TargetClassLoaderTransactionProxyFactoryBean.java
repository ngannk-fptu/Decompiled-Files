/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.interceptor.TransactionProxyFactoryBean
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.spring.transaction.interceptor;

import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springframework.util.Assert;

public class TargetClassLoaderTransactionProxyFactoryBean
extends TransactionProxyFactoryBean {
    private Object target;

    public void setTarget(Object target) {
        Assert.notNull((Object)target, (String)"Property 'target' must not be null");
        super.setTarget(target);
        super.setProxyClassLoader(target.getClass().getClassLoader());
        this.target = target;
    }
}

