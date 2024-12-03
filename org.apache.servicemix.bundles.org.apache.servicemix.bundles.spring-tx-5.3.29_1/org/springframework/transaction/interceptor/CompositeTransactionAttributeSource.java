/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.Assert;

public class CompositeTransactionAttributeSource
implements TransactionAttributeSource,
Serializable {
    private final TransactionAttributeSource[] transactionAttributeSources;

    public CompositeTransactionAttributeSource(TransactionAttributeSource ... transactionAttributeSources) {
        Assert.notNull((Object)transactionAttributeSources, (String)"TransactionAttributeSource array must not be null");
        this.transactionAttributeSources = transactionAttributeSources;
    }

    public final TransactionAttributeSource[] getTransactionAttributeSources() {
        return this.transactionAttributeSources;
    }

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        for (TransactionAttributeSource source : this.transactionAttributeSources) {
            if (!source.isCandidateClass(targetClass)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        for (TransactionAttributeSource source : this.transactionAttributeSources) {
            TransactionAttribute attr = source.getTransactionAttribute(method, targetClass);
            if (attr == null) continue;
            return attr;
        }
        return null;
    }
}

