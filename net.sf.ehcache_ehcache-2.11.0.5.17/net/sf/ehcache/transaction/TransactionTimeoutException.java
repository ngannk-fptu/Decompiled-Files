/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.transaction.TransactionException;

public class TransactionTimeoutException
extends TransactionException {
    public TransactionTimeoutException(String message) {
        super(message);
    }
}

