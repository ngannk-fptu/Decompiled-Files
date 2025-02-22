/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CPSubsystemException;

public class MemberDoesNotExistException
extends CPSubsystemException {
    private static final long serialVersionUID = -6536728347770526039L;

    public MemberDoesNotExistException(Endpoint member) {
        super("Member does not exist: " + member, (Endpoint)null);
    }
}

