/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.MembershipEvent;
import com.hazelcast.instance.MemberImpl;

public class MembershipServiceEvent
extends MembershipEvent {
    public MembershipServiceEvent(MembershipEvent e) {
        super(e.getCluster(), e.getMember(), e.getEventType(), e.getMembers());
    }

    @Override
    public MemberImpl getMember() {
        return (MemberImpl)super.getMember();
    }
}

