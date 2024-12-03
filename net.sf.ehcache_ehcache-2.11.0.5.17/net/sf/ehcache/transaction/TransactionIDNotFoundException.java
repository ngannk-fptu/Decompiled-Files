/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.transaction.TransactionException;

public class TransactionIDNotFoundException
extends TransactionException {
    public TransactionIDNotFoundException(String message) {
        super(message);
    }
}

