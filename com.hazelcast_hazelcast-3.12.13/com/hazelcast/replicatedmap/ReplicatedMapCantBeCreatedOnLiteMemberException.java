/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.Address;

public class ReplicatedMapCantBeCreatedOnLiteMemberException
extends HazelcastException {
    public ReplicatedMapCantBeCreatedOnLiteMemberException(Address address) {
        this("Can't create replicated map instance on " + address);
    }

    public ReplicatedMapCantBeCreatedOnLiteMemberException(String message) {
        super(message);
    }
}

