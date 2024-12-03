/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.sal.api.transaction.TransactionCallback;

public interface TransactionManager {
    public <T> T doInTransaction(TransactionCallback<T> var1);
}

