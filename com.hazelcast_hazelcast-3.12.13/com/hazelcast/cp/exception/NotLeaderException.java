/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.CPSubsystemException;

public class NotLeaderException
extends CPSubsystemException {
    private static final long serialVersionUID = 1817579502149525710L;

    public NotLeaderException(CPGroupId groupId, Endpoint local, Endpoint leader) {
        super(local + " is not LEADER of " + groupId + ". Known leader is: " + (leader != null ? leader : "N/A"), leader);
    }
}

