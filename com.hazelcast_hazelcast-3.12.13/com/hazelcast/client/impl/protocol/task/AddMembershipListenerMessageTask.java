/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAddMembershipListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.cluster.MemberAttributeOperationType;
import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.InitialMembershipListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;

public class AddMembershipListenerMessageTask
extends AbstractCallableMessageTask<ClientAddMembershipListenerCodec.RequestParameters> {
    public AddMembershipListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        String serviceName = "hz:core:clusterService";
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService(serviceName);
        boolean advancedNetworkConfigEnabled = this.isAdvancedNetworkEnabled();
        String registrationId = service.addMembershipListener(new MembershipListenerImpl(this.endpoint, advancedNetworkConfigEnabled));
        this.endpoint.addListenerDestroyAction(serviceName, serviceName, registrationId);
        return registrationId;
    }

    @Override
    protected ClientAddMembershipListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientAddMembershipListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientAddMembershipListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:core:clusterService";
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

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    private class MembershipListenerImpl
    implements InitialMembershipListener {
        private final ClientEndpoint endpoint;
        private final boolean advancedNetworkConfigEnabled;

        public MembershipListenerImpl(ClientEndpoint endpoint, boolean advancedNetworkConfigEnabled) {
            this.endpoint = endpoint;
            this.advancedNetworkConfigEnabled = advancedNetworkConfigEnabled;
        }

        @Override
        public void init(InitialMembershipEvent membershipEvent) {
            ClusterService service = (ClusterService)AddMembershipListenerMessageTask.this.getService("hz:core:clusterService");
            Collection<MemberImpl> members = service.getMemberImpls();
            ArrayList<Member> membersToSend = new ArrayList<Member>();
            for (MemberImpl member : members) {
                membersToSend.add(this.translateMemberAddress(member));
            }
            ClientMessage eventMessage = ClientAddMembershipListenerCodec.encodeMemberListEvent(membersToSend);
            AddMembershipListenerMessageTask.this.sendClientMessage(this.endpoint.getUuid(), eventMessage);
        }

        @Override
        public void memberAdded(MembershipEvent membershipEvent) {
            if (!this.shouldSendEvent()) {
                return;
            }
            MemberImpl member = (MemberImpl)membershipEvent.getMember();
            ClientMessage eventMessage = ClientAddMembershipListenerCodec.encodeMemberEvent(this.translateMemberAddress(member), 1);
            AddMembershipListenerMessageTask.this.sendClientMessage(this.endpoint.getUuid(), eventMessage);
        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {
            if (!this.shouldSendEvent()) {
                return;
            }
            MemberImpl member = (MemberImpl)membershipEvent.getMember();
            ClientMessage eventMessage = ClientAddMembershipListenerCodec.encodeMemberEvent(this.translateMemberAddress(member), 2);
            AddMembershipListenerMessageTask.this.sendClientMessage(this.endpoint.getUuid(), eventMessage);
        }

        @Override
        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
            if (!this.shouldSendEvent()) {
                return;
            }
            MemberImpl member = (MemberImpl)memberAttributeEvent.getMember();
            String uuid = member.getUuid();
            MemberAttributeOperationType op = memberAttributeEvent.getOperationType();
            String key = memberAttributeEvent.getKey();
            String value = memberAttributeEvent.getValue() == null ? null : memberAttributeEvent.getValue().toString();
            ClientMessage eventMessage = ClientAddMembershipListenerCodec.encodeMemberAttributeChangeEvent(uuid, key, op.getId(), value);
            AddMembershipListenerMessageTask.this.sendClientMessage(this.endpoint.getUuid(), eventMessage);
        }

        private boolean shouldSendEvent() {
            if (!this.endpoint.isAlive()) {
                return false;
            }
            ClusterService clusterService = AddMembershipListenerMessageTask.this.clientEngine.getClusterService();
            return !((ClientAddMembershipListenerCodec.RequestParameters)AddMembershipListenerMessageTask.this.parameters).localOnly || clusterService.isMaster();
        }

        private MemberImpl translateMemberAddress(MemberImpl member) {
            if (!this.advancedNetworkConfigEnabled) {
                return member;
            }
            Address clientAddress = member.getAddressMap().get(EndpointQualifier.CLIENT);
            MemberImpl result = new MemberImpl.Builder(clientAddress).version(member.getVersion()).uuid(member.getUuid()).localMember(member.localMember()).liteMember(member.isLiteMember()).memberListJoinVersion(member.getMemberListJoinVersion()).attributes(member.getAttributes()).build();
            return result;
        }
    }
}

