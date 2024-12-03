/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.core.IndeterminateOperationState;
import com.hazelcast.cp.exception.CPSubsystemException;

public class StaleAppendRequestException
extends CPSubsystemException
implements IndeterminateOperationState {
    private static final long serialVersionUID = -736303015926722821L;

    public StaleAppendRequestException(Endpoint leader) {
        super(leader);
    }
}

