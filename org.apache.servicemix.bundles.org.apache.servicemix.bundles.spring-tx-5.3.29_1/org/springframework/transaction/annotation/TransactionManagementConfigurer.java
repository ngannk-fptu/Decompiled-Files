/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.annotation;

import org.springframework.transaction.TransactionManager;

public interface TransactionManagementConfigurer {
    public TransactionManager annotationDrivenTransactionManager();
}

