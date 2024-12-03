/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;

public interface NodeState
extends JsonSerializable {
    public ClusterState getClusterState();

    public com.hazelcast.instance.NodeState getNodeState();

    public Version getClusterVersion();

    public MemberVersion getMemberVersion();
}

