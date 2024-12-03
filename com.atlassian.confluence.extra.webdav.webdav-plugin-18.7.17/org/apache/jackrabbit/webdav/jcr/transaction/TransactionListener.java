/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.transaction;

import org.apache.jackrabbit.webdav.transaction.TransactionResource;

public interface TransactionListener {
    public void beforeCommit(TransactionResource var1, String var2);

    public void afterCommit(TransactionResource var1, String var2, boolean var3);
}

