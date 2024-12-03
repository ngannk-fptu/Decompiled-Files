/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.transaction;

public interface ThrowingTransactionCallback<T, X extends Exception> {
    public T doInTransaction() throws X;
}

