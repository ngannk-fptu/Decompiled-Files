/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.core.HazelcastException;

public class CPSubsystemException
extends HazelcastException {
    private static final long serialVersionUID = 3165333502175586105L;
    private final Endpoint leader;

    public CPSubsystemException(Endpoint leader) {
        this.leader = leader;
    }

    public CPSubsystemException(String message, Endpoint leader) {
        super(message);
        this.leader = leader;
    }

    public CPSubsystemException(String message, Endpoint leader, Throwable cause) {
        super(message, cause);
        this.leader = leader;
    }

    public Endpoint getLeader() {
        return this.leader;
    }
}

