/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.crdt.pncounter;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.PNCounterGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.crdt.pncounter.operations.CRDTTimestampedLong;
import com.hazelcast.crdt.pncounter.operations.GetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.PNCounterPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.Map;

public class PNCounterGetMessageTask
extends AbstractAddressMessageTask<PNCounterGetCodec.RequestParameters> {
    public PNCounterGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Address getAddress() {
        return ((PNCounterGetCodec.RequestParameters)this.parameters).targetReplica;
    }

    @Override
    protected Operation prepareOperation() {
        VectorClock vectorClock = new VectorClock();
        if (((PNCounterGetCodec.RequestParameters)this.parameters).replicaTimestamps != null) {
            for (Map.Entry<String, Long> timestamp : ((PNCounterGetCodec.RequestParameters)this.parameters).replicaTimestamps) {
                vectorClock.setReplicaTimestamp(timestamp.getKey(), timestamp.getValue());
            }
        }
        return new GetOperation(((PNCounterGetCodec.RequestParameters)this.parameters).name, vectorClock);
    }

    @Override
    protected PNCounterGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = PNCounterGetCodec.decodeRequest(clientMessage);
        ((PNCounterGetCodec.RequestParameters)this.parameters).targetReplica = this.clientEngine.memberAddressOf(((PNCounterGetCodec.RequestParameters)this.parameters).targetReplica);
        return (PNCounterGetCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        CRDTTimestampedLong resp = (CRDTTimestampedLong)response;
        PNCounterConfig counterConfig = this.nodeEngine.getConfig().findPNCounterConfig(((PNCounterGetCodec.RequestParameters)this.parameters).name);
        return PNCounterGetCodec.encodeResponse(resp.getValue(), resp.getVectorClock().entrySet(), counterConfig.getReplicaCount());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:PNCounterService";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new PNCounterPermission(((PNCounterGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public String getDistributedObjectName() {
        return ((PNCounterGetCodec.RequestParameters)this.parameters).name;
    }
}

