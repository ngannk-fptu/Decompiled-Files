/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.CPSubsystemException;

public class CPGroupDestroyedException
extends CPSubsystemException {
    private static final long serialVersionUID = -5363753263443789491L;
    private final CPGroupId groupId;

    public CPGroupDestroyedException() {
        this((CPGroupId)null);
    }

    public CPGroupDestroyedException(CPGroupId groupId) {
        super(String.valueOf(groupId), (Endpoint)null);
        this.groupId = groupId;
    }

    public CPGroupId getGroupId() {
        return this.groupId;
    }
}

