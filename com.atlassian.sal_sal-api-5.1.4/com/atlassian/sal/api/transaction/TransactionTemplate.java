/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.transaction;

import com.atlassian.sal.api.transaction.TransactionCallback;

public interface TransactionTemplate {
    public <T> T execute(TransactionCallback<T> var1);
}

