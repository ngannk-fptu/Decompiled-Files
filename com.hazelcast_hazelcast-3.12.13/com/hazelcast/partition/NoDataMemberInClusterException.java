/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition;

import com.hazelcast.core.HazelcastException;

public class NoDataMemberInClusterException
extends HazelcastException {
    public NoDataMemberInClusterException(String message) {
        super(message);
    }
}

