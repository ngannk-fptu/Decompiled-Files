/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.exception;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CPSubsystemException;

public class LeaderDemotedException
extends CPSubsystemException {
    private static final long serialVersionUID = 4284556927980596355L;

    public LeaderDemotedException(Endpoint local, Endpoint leader) {
        super(local + " is not LEADER anymore. Known leader is: " + (leader != null ? leader : "N/A"), leader);
    }
}

