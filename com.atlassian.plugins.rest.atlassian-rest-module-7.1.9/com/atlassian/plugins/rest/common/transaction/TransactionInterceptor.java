/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.plugins.rest.common.transaction;

import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.lang.reflect.InvocationTargetException;

public class TransactionInterceptor
implements ResourceInterceptor {
    private final TransactionTemplate transactionTemplate;

    public TransactionInterceptor(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        try {
            this.transactionTemplate.execute(() -> {
                try {
                    invocation.invoke();
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new TransactionException(e);
                }
                return null;
            });
        }
        catch (TransactionException ex) {
            Throwable t = ex.getCause();
            if (t instanceof IllegalAccessException) {
                throw (IllegalAccessException)t;
            }
            if (t instanceof InvocationTargetException) {
                throw (InvocationTargetException)t;
            }
            throw new RuntimeException("This should not be possible");
        }
    }

    private static final class TransactionException
    extends RuntimeException {
        private TransactionException(Throwable throwable) {
            super(throwable);
        }
    }
}

