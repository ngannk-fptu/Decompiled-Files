/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.lock.AbstractLockEntry;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.transaction.TransactionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TxLockEntry
extends AbstractLockEntry
implements TransactionConstants {
    private static Logger log = LoggerFactory.getLogger(TxLockEntry.class);
    private final Scope scope;

    public TxLockEntry(boolean isLocal) {
        this.scope = isLocal ? LOCAL : GLOBAL;
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

