/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.cluster.MemberAttributeOperationType;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.nio.serialization.SerializableByConvention;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
public class MemberAttributeServiceEvent
extends MemberAttributeEvent {
    public MemberAttributeServiceEvent() {
    }

    public MemberAttributeServiceEvent(Cluster cluster, MemberImpl member, MemberAttributeOperationType operationType, String key, Object value) {
        super(cluster, member, operationType, key, value);
    }
}

