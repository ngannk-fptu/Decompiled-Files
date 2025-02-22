/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CPSubsystemException;
import com.hazelcast.spi.exception.RetryableException;

public class CannotReplicateException
extends CPSubsystemException
implements RetryableException {
    private static final long serialVersionUID = 4407025930140337716L;

    public CannotReplicateException(Endpoint leader) {
        super("Cannot replicate new operations for now", leader);
    }
}

