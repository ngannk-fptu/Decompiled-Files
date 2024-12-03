/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.lock.DefaultActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.transaction.TransactionConstants;

public class TxActiveLock
extends DefaultActiveLock
implements TransactionConstants {
    public static final long DEFAULT_TIMEOUT = 300000L;
    private final Scope scope;

    public TxActiveLock(LockInfo lockInfo) {
        if (lockInfo != null) {
            if (!TRANSACTION.equals(lockInfo.getType())) {
                throw new IllegalArgumentException("Only 'transaction' type is allowed for a transaction-activelock object.");
            }
            if (!LOCAL.equals(lockInfo.getScope()) && !GLOBAL.equals(lockInfo.getScope())) {
                throw new IllegalArgumentException("Only 'global' or 'local' are valid scopes within a transaction-activelock element.");
            }
            if (!lockInfo.isDeep()) {
                throw new IllegalArgumentException("Only transaction locks can only be deep.");
            }
            this.setOwner(lockInfo.getOwner());
            this.setTimeout(lockInfo.getTimeout());
            this.scope = lockInfo.getScope();
        } else {
            this.setTimeout(300000L);
            this.scope = LOCAL;
        }
    }

    @Override
    public boolean isDeep() {
        return true;
    }

    @Override
    public Type getType() {
        return TRANSACTION;
    }

    @Override
    public Scope getScope() {
        return this.scope;
    }
}

