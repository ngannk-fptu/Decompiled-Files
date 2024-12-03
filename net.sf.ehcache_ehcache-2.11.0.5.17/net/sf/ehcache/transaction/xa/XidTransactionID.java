/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa;

import javax.transaction.xa.Xid;
import net.sf.ehcache.transaction.TransactionID;

public interface XidTransactionID
extends TransactionID {
    public Xid getXid();

    public String getCacheName();
}

