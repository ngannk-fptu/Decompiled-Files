/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipServiceEvent;

public interface MembershipAwareService {
    public void memberAdded(MembershipServiceEvent var1);

    public void memberRemoved(MembershipServiceEvent var1);

    public void memberAttributeChanged(MemberAttributeServiceEvent var1);
}

