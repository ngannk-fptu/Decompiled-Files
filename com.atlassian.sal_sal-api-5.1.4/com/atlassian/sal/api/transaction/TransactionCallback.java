/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.transaction;

public interface TransactionCallback<T> {
    public T doInTransaction();
}

