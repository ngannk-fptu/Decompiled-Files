/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.interceptor;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttribute;

public interface TransactionAttributeSource {
    default public boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    @Nullable
    public TransactionAttribute getTransactionAttribute(Method var1, @Nullable Class<?> var2);
}

