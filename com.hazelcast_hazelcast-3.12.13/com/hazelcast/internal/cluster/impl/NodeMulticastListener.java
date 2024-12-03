/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.Joiner;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.cluster.impl.JoinRequest;
import com.hazelcast.internal.cluster.impl.MulticastJoiner;
import com.hazelcast.internal.cluster.impl.MulticastListener;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;

public class NodeMulticastListener
implements MulticastListener {
    private final Node node;
    private final ILogger logger;
    private ConfigCheck ourConfig;

    public NodeMulticastListener(Node node) {
        this.node = node;
        this.logger = node.getLogger(NodeMulticastListener.class.getName());
        this.ourConfig = node.createConfigCheck();
    }

    @Override
    public void onMessage(Object msg) {
        if (!this.isValidJoinMessage(msg)) {
            this.logDroppedMessage(msg);
            return;
        }
        JoinMessage joinMessage = (JoinMessage)msg;
        if (this.node.isRunning() && this.node.getClusterService().isJoined()) {
            this.handleActiveAndJoined(joinMessage);
        } else {
            this.handleNotActiveOrNotJoined(joinMessage);
        }
    }

    private void logDroppedMessage(Object msg) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Dropped: " + msg);
        }
    }

    private void handleActiveAndJoined(JoinMessage joinMessage) {
        MemberImpl master;
        if (!(joinMessage instanceof JoinRequest)) {
            this.logDroppedMessage(joinMessage);
            return;
        }
        ClusterServiceImpl clusterService = this.node.getClusterService();
        Address masterAddress = clusterService.getMasterAddress();
        if (clusterService.isMaster()) {
            JoinMessage response = new JoinMessage(4, this.node.getBuildInfo().getBuildNumber(), this.node.getVersion(), this.node.getThisAddress(), this.node.getThisUuid(), this.node.isLiteMember(), this.node.createConfigCheck());
            this.node.multicastService.send(response);
        } else if (joinMessage.getAddress().equals(masterAddress) && (master = this.node.getClusterService().getMember(masterAddress)) != null && !master.getUuid().equals(joinMessage.getUuid())) {
            String message = "New join request has been received from current master. Suspecting " + masterAddress;
            this.logger.warning(message);
            clusterService.suspectMember(master, message, false);
        }
    }

    private void handleNotActiveOrNotJoined(JoinMessage joinMessage) {
        if (this.isJoinRequest(joinMessage)) {
            Joiner joiner = this.node.getJoiner();
            if (joiner instanceof MulticastJoiner) {
                MulticastJoiner multicastJoiner = (MulticastJoiner)joiner;
                multicastJoiner.onReceivedJoinRequest((JoinRequest)joinMessage);
            } else {
                this.logDroppedMessage(joinMessage);
            }
        } else {
            Address address = joinMessage.getAddress();
            if (this.node.getJoiner().isBlacklisted(address)) {
                this.logDroppedMessage(joinMessage);
                return;
            }
            ClusterServiceImpl clusterService = this.node.getClusterService();
            if (!clusterService.isJoined() && clusterService.getMasterAddress() == null) {
                clusterService.setMasterAddressToJoin(joinMessage.getAddress());
            } else {
                this.logDroppedMessage(joinMessage);
            }
        }
    }

    private boolean isJoinRequest(JoinMessage joinMessage) {
        return joinMessage instanceof JoinRequest;
    }

    private boolean isJoinMessage(Object msg) {
        return msg != null && msg instanceof JoinMessage && !(msg instanceof SplitBrainJoinMessage);
    }

    private boolean isValidJoinMessage(Object msg) {
        if (!this.isJoinMessage(msg)) {
            return false;
        }
        JoinMessage joinMessage = (JoinMessage)msg;
        if (this.isMessageToSelf(joinMessage)) {
            return false;
        }
        ConfigCheck theirConfig = joinMessage.getConfigCheck();
        return this.ourConfig.isSameGroup(theirConfig);
    }

    private boolean isMessageToSelf(JoinMessage joinMessage) {
        Address thisAddress = this.node.getThisAddress();
        return thisAddress == null || thisAddress.equals(joinMessage.getAddress());
    }
}

