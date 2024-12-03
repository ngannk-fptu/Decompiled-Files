/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.crdt.pncounter;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.PNCounterAddCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.crdt.pncounter.operations.AddOperation;
import com.hazelcast.crdt.pncounter.operations.CRDTTimestampedLong;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.PNCounterPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.Map;

public class PNCounterAddMessageTask
extends AbstractAddressMessageTask<PNCounterAddCodec.RequestParameters> {
    public PNCounterAddMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Address getAddress() {
        return ((PNCounterAddCodec.RequestParameters)this.parameters).targetReplica;
    }

    @Override
    protected Operation prepareOperation() {
        VectorClock vectorClock = new VectorClock();
        if (((PNCounterAddCodec.RequestParameters)this.parameters).replicaTimestamps != null) {
            for (Map.Entry<String, Long> timestamp : ((PNCounterAddCodec.RequestParameters)this.parameters).replicaTimestamps) {
                vectorClock.setReplicaTimestamp(timestamp.getKey(), timestamp.getValue());
            }
        }
        return new AddOperation(((PNCounterAddCodec.RequestParameters)this.parameters).name, ((PNCounterAddCodec.RequestParameters)this.parameters).delta, ((PNCounterAddCodec.RequestParameters)this.parameters).getBeforeUpdate, vectorClock);
    }

    @Override
    protected PNCounterAddCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = PNCounterAddCodec.decodeRequest(clientMessage);
        ((PNCounterAddCodec.RequestParameters)this.parameters).targetReplica = this.clientEngine.memberAddressOf(((PNCounterAddCodec.RequestParameters)this.parameters).targetReplica);
        return (PNCounterAddCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        CRDTTimestampedLong resp = (CRDTTimestampedLong)response;
        PNCounterConfig counterConfig = this.nodeEngine.getConfig().findPNCounterConfig(((PNCounterAddCodec.RequestParameters)this.parameters).name);
        return PNCounterAddCodec.encodeResponse(resp.getValue(), resp.getVectorClock().entrySet(), counterConfig.getReplicaCount());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:PNCounterService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((PNCounterAddCodec.RequestParameters)this.parameters).delta, ((PNCounterAddCodec.RequestParameters)this.parameters).getBeforeUpdate};
    }

    @Override
    public Permission getRequiredPermission() {
        return new PNCounterPermission(((PNCounterAddCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public String getDistributedObjectName() {
        return ((PNCounterAddCodec.RequestParameters)this.parameters).name;
    }
}

