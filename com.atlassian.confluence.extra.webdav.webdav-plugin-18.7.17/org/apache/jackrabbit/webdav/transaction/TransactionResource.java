/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;
import org.apache.jackrabbit.webdav.transaction.TxLockManager;

public interface TransactionResource
extends DavResource {
    public static final String METHODS = "";

    public void init(TxLockManager var1, String var2);

    public String getTransactionId();

    public void unlock(String var1, TransactionInfo var2) throws DavException;
}

