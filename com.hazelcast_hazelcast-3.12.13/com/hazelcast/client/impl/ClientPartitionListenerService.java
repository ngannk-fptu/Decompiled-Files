/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAddPartitionListenerCodec;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionTableView;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.scheduler.CoalescingDelayedTrigger;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPartitionListenerService {
    private static final long UPDATE_DELAY_MS = 100L;
    private static final long UPDATE_MAX_DELAY_MS = 500L;
    private final Map<ClientEndpoint, Long> partitionListeningEndpoints = new ConcurrentHashMap<ClientEndpoint, Long>();
    private final NodeEngineImpl nodeEngine;
    private final boolean advancedNetworkConfigEnabled;
    private final CoalescingDelayedTrigger delayedPartitionUpdateTrigger;

    ClientPartitionListenerService(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.advancedNetworkConfigEnabled = nodeEngine.getConfig().getAdvancedNetworkConfig().isEnabled();
        this.delayedPartitionUpdateTrigger = new CoalescingDelayedTrigger(nodeEngine.getExecutionService(), 100L, 500L, new PushPartitionTableUpdate());
    }

    public void onPartitionStateChange() {
        this.delayedPartitionUpdateTrigger.executeWithDelay();
    }

    private void pushPartitionStateChange() {
        PartitionTableView partitionTableView = this.nodeEngine.getPartitionService().createPartitionTableView();
        Collection<Map.Entry<Address, List<Integer>>> partitions = this.getPartitions(partitionTableView);
        int partitionStateVersion = partitionTableView.getVersion();
        for (Map.Entry<ClientEndpoint, Long> entry : this.partitionListeningEndpoints.entrySet()) {
            ClientMessage clientMessage = this.getPartitionsMessage(partitions, partitionStateVersion);
            Long correlationId = entry.getValue();
            clientMessage.setCorrelationId(correlationId);
            ClientEndpoint clientEndpoint = entry.getKey();
            Connection connection = clientEndpoint.getConnection();
            connection.write(clientMessage);
        }
    }

    private ClientMessage getPartitionsMessage(Collection<Map.Entry<Address, List<Integer>>> partitions, int partitionStateVersion) {
        ClientMessage clientMessage = ClientAddPartitionListenerCodec.encodePartitionsEvent(partitions, partitionStateVersion);
        clientMessage.addFlag((short)192);
        clientMessage.setVersion((short)1);
        return clientMessage;
    }

    public void registerPartitionListener(ClientEndpoint clientEndpoint, long correlationId) {
        this.partitionListeningEndpoints.put(clientEndpoint, correlationId);
        PartitionTableView partitionTableView = this.nodeEngine.getPartitionService().createPartitionTableView();
        Collection<Map.Entry<Address, List<Integer>>> partitions = this.getPartitions(partitionTableView);
        int partitionStateVersion = partitionTableView.getVersion();
        ClientMessage clientMessage = this.getPartitionsMessage(partitions, partitionStateVersion);
        clientMessage.setCorrelationId(correlationId);
        clientEndpoint.getConnection().write(clientMessage);
    }

    public void deregisterPartitionListener(ClientEndpoint clientEndpoint) {
        this.partitionListeningEndpoints.remove(clientEndpoint);
    }

    public Collection<Map.Entry<Address, List<Integer>>> getPartitions(PartitionTableView partitionTableView) {
        HashMap<Address, LinkedList<Integer>> partitionsMap = new HashMap<Address, LinkedList<Integer>>();
        int partitionCount = partitionTableView.getLength();
        for (int partitionId = 0; partitionId < partitionCount; ++partitionId) {
            PartitionReplica owner = partitionTableView.getReplica(partitionId, 0);
            if (owner == null) {
                partitionsMap.clear();
                return partitionsMap.entrySet();
            }
            Address clientOwnerAddress = this.clientAddressOf(owner.address());
            if (clientOwnerAddress == null) {
                partitionsMap.clear();
                return partitionsMap.entrySet();
            }
            LinkedList<Integer> indexes = (LinkedList<Integer>)partitionsMap.get(clientOwnerAddress);
            if (indexes == null) {
                indexes = new LinkedList<Integer>();
                partitionsMap.put(clientOwnerAddress, indexes);
            }
            indexes.add(partitionId);
        }
        return partitionsMap.entrySet();
    }

    private Address clientAddressOf(Address memberAddress) {
        if (!this.advancedNetworkConfigEnabled) {
            return memberAddress;
        }
        MemberImpl member = this.nodeEngine.getClusterService().getMember(memberAddress);
        if (member != null) {
            return member.getAddressMap().get(EndpointQualifier.CLIENT);
        }
        return null;
    }

    public Map<ClientEndpoint, Long> getPartitionListeningEndpoints() {
        return this.partitionListeningEndpoints;
    }

    private class PushPartitionTableUpdate
    implements Runnable {
        private PushPartitionTableUpdate() {
        }

        @Override
        public void run() {
            ClientPartitionListenerService.this.pushPartitionStateChange();
        }
    }
}

