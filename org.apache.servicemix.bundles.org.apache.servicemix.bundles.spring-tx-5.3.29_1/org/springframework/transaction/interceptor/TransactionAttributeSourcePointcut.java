/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.ClassFilter
 *  org.springframework.aop.support.StaticMethodMatcherPointcut
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionalProxy;
import org.springframework.util.ObjectUtils;

abstract class TransactionAttributeSourcePointcut
extends StaticMethodMatcherPointcut
implements Serializable {
    protected TransactionAttributeSourcePointcut() {
        this.setClassFilter(new TransactionAttributeSourceClassFilter());
    }

    public boolean matches(Method method, Class<?> targetClass) {
        TransactionAttributeSource tas = this.getTransactionAttributeSource();
        return tas == null || tas.getTransactionAttribute(method, targetClass) != null;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TransactionAttributeSourcePointcut)) {
            return false;
        }
        TransactionAttributeSourcePointcut otherPc = (TransactionAttributeSourcePointcut)other;
        return ObjectUtils.nullSafeEquals((Object)this.getTransactionAttributeSource(), (Object)otherPc.getTransactionAttributeSource());
    }

    public int hashCode() {
        return TransactionAttributeSourcePointcut.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.getTransactionAttributeSource();
    }

    @Nullable
    protected abstract TransactionAttributeSource getTransactionAttributeSource();

    private class TransactionAttributeSourceClassFilter
    implements ClassFilter {
        private TransactionAttributeSourceClassFilter() {
        }

        public boolean matches(Class<?> clazz) {
            if (TransactionalProxy.class.isAssignableFrom(clazz) || TransactionManager.class.isAssignableFrom(clazz) || PersistenceExceptionTranslator.class.isAssignableFrom(clazz)) {
                return false;
            }
            TransactionAttributeSource tas = TransactionAttributeSourcePointcut.this.getTransactionAttributeSource();
            return tas == null || tas.isCandidateClass(clazz);
        }
    }
}

