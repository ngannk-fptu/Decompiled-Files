/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddMerkleTreeConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;

public class AddMerkleTreeConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddMerkleTreeConfigCodec.RequestParameters> {
    public AddMerkleTreeConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddMerkleTreeConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddMerkleTreeConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddMerkleTreeConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        Preconditions.checkHasText(((DynamicConfigAddMerkleTreeConfigCodec.RequestParameters)this.parameters).mapName, "Merkle tree config must define a map name");
        return new MerkleTreeConfig().setMapName(((DynamicConfigAddMerkleTreeConfigCodec.RequestParameters)this.parameters).mapName).setEnabled(((DynamicConfigAddMerkleTreeConfigCodec.RequestParameters)this.parameters).enabled).setDepth(((DynamicConfigAddMerkleTreeConfigCodec.RequestParameters)this.parameters).depth);
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        MerkleTreeConfig merkleTreeConfig = (MerkleTreeConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getMapMerkleTreeConfigs(), merkleTreeConfig.getMapName(), merkleTreeConfig);
    }

    @Override
    public String getMethodName() {
        return "addMerkleTreeConfig";
    }
}

