/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cardinality;

import com.hazelcast.cardinality.impl.operations.AggregateOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CardinalityEstimatorAddCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CardinalityEstimatorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CardinalityEstimatorAddMessageTask
extends AbstractPartitionMessageTask<CardinalityEstimatorAddCodec.RequestParameters> {
    public CardinalityEstimatorAddMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AggregateOperation(((CardinalityEstimatorAddCodec.RequestParameters)this.parameters).name, ((CardinalityEstimatorAddCodec.RequestParameters)this.parameters).hash);
    }

    @Override
    protected CardinalityEstimatorAddCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CardinalityEstimatorAddCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CardinalityEstimatorAddCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cardinalityEstimatorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CardinalityEstimatorPermission(((CardinalityEstimatorAddCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CardinalityEstimatorAddCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "add";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CardinalityEstimatorAddCodec.RequestParameters)this.parameters).name, ((CardinalityEstimatorAddCodec.RequestParameters)this.parameters).hash};
    }
}

