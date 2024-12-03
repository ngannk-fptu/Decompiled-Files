/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum;

import com.hazelcast.spi.exception.SilentException;
import com.hazelcast.transaction.TransactionException;

public class QuorumException
extends TransactionException
implements SilentException {
    public QuorumException(String message) {
        super(message);
    }
}

