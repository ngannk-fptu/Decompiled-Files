/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.ClassFilter
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.support.AbstractPointcutAdvisor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.interceptor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourcePointcut;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.Assert;

public class TransactionAttributeSourceAdvisor
extends AbstractPointcutAdvisor {
    @Nullable
    private TransactionInterceptor transactionInterceptor;
    private final TransactionAttributeSourcePointcut pointcut = new TransactionAttributeSourcePointcut(){

        @Override
        @Nullable
        protected TransactionAttributeSource getTransactionAttributeSource() {
            return TransactionAttributeSourceAdvisor.this.transactionInterceptor != null ? TransactionAttributeSourceAdvisor.this.transactionInterceptor.getTransactionAttributeSource() : null;
        }
    };

    public TransactionAttributeSourceAdvisor() {
    }

    public TransactionAttributeSourceAdvisor(TransactionInterceptor interceptor) {
        this.setTransactionInterceptor(interceptor);
    }

    public void setTransactionInterceptor(TransactionInterceptor interceptor) {
        this.transactionInterceptor = interceptor;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public Advice getAdvice() {
        Assert.state((this.transactionInterceptor != null ? 1 : 0) != 0, (String)"No TransactionInterceptor set");
        return this.transactionInterceptor;
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }
}

