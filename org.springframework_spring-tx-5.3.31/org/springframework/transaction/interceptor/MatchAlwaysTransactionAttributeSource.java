/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class MatchAlwaysTransactionAttributeSource
implements TransactionAttributeSource,
Serializable {
    private TransactionAttribute transactionAttribute = new DefaultTransactionAttribute();

    public void setTransactionAttribute(TransactionAttribute transactionAttribute) {
        if (transactionAttribute instanceof DefaultTransactionAttribute) {
            ((DefaultTransactionAttribute)transactionAttribute).resolveAttributeStrings(null);
        }
        this.transactionAttribute = transactionAttribute;
    }

    @Override
    @Nullable
    public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        return ClassUtils.isUserLevelMethod((Method)method) ? this.transactionAttribute : null;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MatchAlwaysTransactionAttributeSource)) {
            return false;
        }
        MatchAlwaysTransactionAttributeSource otherTas = (MatchAlwaysTransactionAttributeSource)other;
        return ObjectUtils.nullSafeEquals((Object)this.transactionAttribute, (Object)otherTas.transactionAttribute);
    }

    public int hashCode() {
        return MatchAlwaysTransactionAttributeSource.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.transactionAttribute;
    }
}

