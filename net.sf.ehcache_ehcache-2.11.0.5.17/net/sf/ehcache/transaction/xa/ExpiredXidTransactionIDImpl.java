/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa;

import net.sf.ehcache.transaction.xa.XidTransactionID;
import net.sf.ehcache.transaction.xa.XidTransactionIDImpl;

public class ExpiredXidTransactionIDImpl
extends XidTransactionIDImpl {
    public ExpiredXidTransactionIDImpl(XidTransactionID xidTransactionId) {
        super(xidTransactionId.getXid(), xidTransactionId.getCacheName());
    }
}

