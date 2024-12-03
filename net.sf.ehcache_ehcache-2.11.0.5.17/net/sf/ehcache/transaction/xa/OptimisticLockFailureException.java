/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa;

import net.sf.ehcache.transaction.TransactionException;

public class OptimisticLockFailureException
extends TransactionException {
    public OptimisticLockFailureException() {
        super("");
    }
}

