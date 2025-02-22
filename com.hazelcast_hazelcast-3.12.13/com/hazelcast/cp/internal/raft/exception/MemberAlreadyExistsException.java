/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CPSubsystemException;

public class MemberAlreadyExistsException
extends CPSubsystemException {
    private static final long serialVersionUID = -4895279676261366826L;

    public MemberAlreadyExistsException(Endpoint member) {
        super("Member already exists: " + member, (Endpoint)null);
    }
}

