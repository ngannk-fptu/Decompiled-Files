/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.support;

import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;

public interface CallbackPreferringPlatformTransactionManager
extends PlatformTransactionManager {
    @Nullable
    public <T> T execute(@Nullable TransactionDefinition var1, TransactionCallback<T> var2) throws TransactionException;
}

