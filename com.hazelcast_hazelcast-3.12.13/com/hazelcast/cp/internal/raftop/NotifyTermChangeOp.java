/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.TermChangeAwareService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class NotifyTermChangeOp
extends RaftOp
implements IdentifiedDataSerializable {
    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        ILogger logger = this.getLogger();
        for (TermChangeAwareService service : this.getNodeEngine().getServices(TermChangeAwareService.class)) {
            try {
                service.onNewTermCommit(groupId, commitIndex);
            }
            catch (Exception e) {
                logger.severe("onNewTermCommit() failed for service: " + service.getClass().getSimpleName() + " and CP group: " + groupId, e);
            }
        }
        return null;
    }

    @Override
    protected String getServiceName() {
        return null;
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 30;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }
}

