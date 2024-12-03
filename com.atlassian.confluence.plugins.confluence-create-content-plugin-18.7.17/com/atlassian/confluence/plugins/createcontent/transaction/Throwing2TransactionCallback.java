/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.transaction;

public interface Throwing2TransactionCallback<T, X1 extends Exception, X2 extends Exception> {
    public T doInTransaction() throws X1, X2;
}

