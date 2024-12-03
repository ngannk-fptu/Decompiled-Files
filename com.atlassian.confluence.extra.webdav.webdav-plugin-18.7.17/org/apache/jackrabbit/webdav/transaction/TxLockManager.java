/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;

public interface TxLockManager
extends LockManager {
    public void releaseLock(TransactionInfo var1, String var2, TransactionResource var3) throws DavException;

    public ActiveLock getLock(Type var1, Scope var2, TransactionResource var3);
}

