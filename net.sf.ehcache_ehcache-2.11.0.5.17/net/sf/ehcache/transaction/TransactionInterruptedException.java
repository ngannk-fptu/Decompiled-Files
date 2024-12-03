/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.transaction.TransactionException;

public class TransactionInterruptedException
extends TransactionException {
    public TransactionInterruptedException(String message) {
        super(message);
    }
}

