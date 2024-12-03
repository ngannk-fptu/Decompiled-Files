/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.transaction.TransactionException;

public class DeadLockException
extends TransactionException {
    public DeadLockException(String message) {
        super(message);
    }
}

