/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.xwork.interceptors;

import com.atlassian.xwork.interceptors.TransactionalInvocation;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class XWorkTransactionInterceptor
implements Interceptor {
    public abstract PlatformTransactionManager getTransactionManager();

    protected abstract boolean shouldIntercept(ActionInvocation var1);

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        if (this.shouldIntercept(invocation)) {
            return new TransactionalInvocation(this.getTransactionManager()).invokeInTransaction(invocation);
        }
        return invocation.invoke();
    }
}

