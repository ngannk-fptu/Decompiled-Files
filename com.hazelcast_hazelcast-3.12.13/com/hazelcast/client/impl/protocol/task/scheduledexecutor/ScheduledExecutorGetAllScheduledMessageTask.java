/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetAllScheduledFuturesCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.InvokeOnMembers;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerAccessor;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnMemberOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnPartitionOperationFactory;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.function.Supplier;
import java.security.Permission;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScheduledExecutorGetAllScheduledMessageTask
extends AbstractMessageTask<ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters>
implements BlockingMessageTask {
    private final boolean advancedNetworkEnabled = this.isAdvancedNetworkEnabled();

    public ScheduledExecutorGetAllScheduledMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() throws Throwable {
        LinkedHashMap<Member, List<ScheduledTaskHandler>> scheduledTasks = new LinkedHashMap<Member, List<ScheduledTaskHandler>>();
        this.retrieveAllMemberOwnedScheduled(scheduledTasks);
        this.retrieveAllPartitionOwnedScheduled(scheduledTasks);
        this.sendResponse(scheduledTasks.entrySet());
    }

    @Override
    protected ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ScheduledExecutorGetAllScheduledFuturesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorGetAllScheduledFuturesCodec.encodeResponse((Collection)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "getAllScheduled";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters)this.parameters).schedulerName};
    }

    private void retrieveAllMemberOwnedScheduled(Map<Member, List<ScheduledTaskHandler>> accumulator) {
        try {
            InvokeOnMembers invokeOnMembers = new InvokeOnMembers(this.nodeEngine, this.getServiceName(), new GetAllScheduledOnMemberOperationFactory(((ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters)this.parameters).schedulerName), this.nodeEngine.getClusterService().getMembers());
            this.accumulateTaskHandlersAsUrnValues(accumulator, invokeOnMembers.invoke());
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void retrieveAllPartitionOwnedScheduled(Map<Member, List<ScheduledTaskHandler>> accumulator) {
        try {
            this.accumulateTaskHandlersAsUrnValues(accumulator, this.nodeEngine.getOperationService().invokeOnAllPartitions(this.getServiceName(), new GetAllScheduledOnPartitionOperationFactory(((ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters)this.parameters).schedulerName)));
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private void accumulateTaskHandlersAsUrnValues(Map<Member, List<ScheduledTaskHandler>> accumulator, Map<?, ?> taskHandlersMap) {
        ClusterService clusterService = this.nodeEngine.getClusterService();
        InternalPartitionService partitionService = this.nodeEngine.getPartitionService();
        for (Map.Entry<?, ?> entry : taskHandlersMap.entrySet()) {
            Object key = entry.getKey();
            MemberImpl owner = key instanceof Number ? clusterService.getMember(partitionService.getPartitionOwner((Integer)key)) : (MemberImpl)key;
            owner = this.translateMemberAddress(owner);
            List handlers = (List)entry.getValue();
            this.translateTaskHandlerAddresses(handlers);
            if (accumulator.containsKey(owner)) {
                List<ScheduledTaskHandler> memberUrns = accumulator.get(owner);
                memberUrns.addAll(handlers);
                continue;
            }
            accumulator.put(owner, handlers);
        }
    }

    private MemberImpl translateMemberAddress(MemberImpl member) {
        if (!this.advancedNetworkEnabled) {
            return member;
        }
        Address clientAddress = member.getAddressMap().get(EndpointQualifier.CLIENT);
        MemberImpl result = new MemberImpl.Builder(clientAddress).version(member.getVersion()).uuid(member.getUuid()).localMember(member.localMember()).liteMember(member.isLiteMember()).memberListJoinVersion(member.getMemberListJoinVersion()).attributes(member.getAttributes()).build();
        return result;
    }

    private void translateTaskHandlerAddresses(List<ScheduledTaskHandler> handlers) {
        if (!this.advancedNetworkEnabled) {
            return;
        }
        for (ScheduledTaskHandler handler : handlers) {
            if (handler.getAddress() == null) continue;
            ScheduledTaskHandlerAccessor.setAddress(handler, this.clientEngine.clientAddressOf(handler.getAddress()));
        }
    }

    private class GetAllScheduledOnMemberOperationFactory
    implements Supplier<Operation> {
        private final String schedulerName;

        GetAllScheduledOnMemberOperationFactory(String schedulerName) {
            this.schedulerName = schedulerName;
        }

        @Override
        public Operation get() {
            return new GetAllScheduledOnMemberOperation(this.schedulerName).setCallerUuid(ScheduledExecutorGetAllScheduledMessageTask.this.endpoint.getUuid());
        }
    }
}

