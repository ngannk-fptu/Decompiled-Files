/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.client.ClientPrincipal;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAuthenticationCustomCodec;
import com.hazelcast.client.impl.protocol.task.AuthenticationBaseMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.Credentials;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AuthenticationCustomCredentialsMessageTask
extends AuthenticationBaseMessageTask<ClientAuthenticationCustomCodec.RequestParameters> {
    public AuthenticationCustomCredentialsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean isOwnerConnection() {
        return ((ClientAuthenticationCustomCodec.RequestParameters)this.parameters).isOwnerConnection;
    }

    @Override
    protected String getClientType() {
        return ((ClientAuthenticationCustomCodec.RequestParameters)this.parameters).clientType;
    }

    @Override
    protected ClientAuthenticationCustomCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        ClientAuthenticationCustomCodec.RequestParameters parameters = ClientAuthenticationCustomCodec.decodeRequest(clientMessage);
        String uuid = parameters.uuid;
        String ownerUuid = parameters.ownerUuid;
        if (uuid != null && uuid.length() > 0) {
            this.principal = new ClientPrincipal(uuid, ownerUuid);
        }
        this.credentials = (Credentials)this.serializationService.toObject(parameters.credentials);
        this.clientSerializationVersion = parameters.serializationVersion;
        if (parameters.clientHazelcastVersionExist) {
            this.clientVersion = parameters.clientHazelcastVersion;
        }
        if (parameters.clientNameExist) {
            this.clientName = parameters.clientName;
        }
        this.labels = parameters.labelsExist ? Collections.unmodifiableSet(new HashSet<String>(parameters.labels)) : Collections.emptySet();
        this.partitionCount = parameters.partitionCountExist ? parameters.partitionCount : null;
        this.clusterId = parameters.clusterIdExist ? parameters.clusterId : null;
        return parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return (ClientMessage)response;
    }

    @Override
    protected ClientMessage encodeAuth(byte status, Address thisAddress, String uuid, String ownerUuid, byte version, List<Member> cleanedUpMembers, int partitionCount, String clusterId) {
        return ClientAuthenticationCustomCodec.encodeResponse(status, thisAddress, uuid, ownerUuid, version, this.getMemberBuildInfo().getVersion(), cleanedUpMembers, partitionCount, clusterId);
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

