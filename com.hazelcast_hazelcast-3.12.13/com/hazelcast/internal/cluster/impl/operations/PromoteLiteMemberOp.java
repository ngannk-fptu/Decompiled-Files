/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.MembershipManager;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.Address;

public class PromoteLiteMemberOp
extends AbstractClusterOperation {
    private transient MembersView response;

    @Override
    public void run() throws Exception {
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        Address callerAddress = this.getCallerAddress();
        String callerUuid = this.getCallerUuid();
        MembershipManager membershipManager = service.getMembershipManager();
        this.response = membershipManager.promoteToDataMember(callerAddress, callerUuid);
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public int getId() {
        return 42;
    }
}

