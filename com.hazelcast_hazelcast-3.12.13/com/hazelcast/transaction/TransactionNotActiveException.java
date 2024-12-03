/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.core.HazelcastException;

public class TransactionNotActiveException
extends HazelcastException {
    public TransactionNotActiveException() {
    }

    public TransactionNotActiveException(String message) {
        super(message);
    }
}

