/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinMode;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

@ParametersAreNonnullByDefault
public interface ClusterJoinRequest {
    public HazelcastInstance getHazelcast();

    public ClusterJoinMode getJoinMode();

    public String getLocalAddress();

    public int getLocalPort();

    public String getRemoteAddress();

    public int getRemotePort();

    public ObjectDataInput in();

    public ObjectDataOutput out();
}

