/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cardinality;

import com.hazelcast.cardinality.impl.operations.EstimateOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CardinalityEstimatorEstimateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CardinalityEstimatorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CardinalityEstimatorEstimateMessageTask
extends AbstractPartitionMessageTask<CardinalityEstimatorEstimateCodec.RequestParameters> {
    public CardinalityEstimatorEstimateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new EstimateOperation(((CardinalityEstimatorEstimateCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected CardinalityEstimatorEstimateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CardinalityEstimatorEstimateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CardinalityEstimatorEstimateCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cardinalityEstimatorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CardinalityEstimatorPermission(((CardinalityEstimatorEstimateCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CardinalityEstimatorEstimateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "estimate";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

