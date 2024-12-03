/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.ClassFilter
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourcePointcut;

public class BeanFactoryTransactionAttributeSourceAdvisor
extends AbstractBeanFactoryPointcutAdvisor {
    @Nullable
    private TransactionAttributeSource transactionAttributeSource;
    private final TransactionAttributeSourcePointcut pointcut = new TransactionAttributeSourcePointcut(){

        @Override
        @Nullable
        protected TransactionAttributeSource getTransactionAttributeSource() {
            return BeanFactoryTransactionAttributeSourceAdvisor.this.transactionAttributeSource;
        }
    };

    public void setTransactionAttributeSource(TransactionAttributeSource transactionAttributeSource) {
        this.transactionAttributeSource = transactionAttributeSource;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }
}

