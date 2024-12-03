/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.annotation;

import java.lang.reflect.AnnotatedElement;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttribute;

public interface TransactionAnnotationParser {
    default public boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    @Nullable
    public TransactionAttribute parseTransactionAnnotation(AnnotatedElement var1);
}

