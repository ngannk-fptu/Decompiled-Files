/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.AbstractJoiner;
import com.hazelcast.internal.cluster.impl.JoinRequest;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.cluster.impl.SplitBrainMulticastListener;
import com.hazelcast.nio.Address;
import com.hazelcast.util.Clock;
import com.hazelcast.util.RandomPicker;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MulticastJoiner
extends AbstractJoiner {
    private static final long JOIN_RETRY_INTERVAL = 1000L;
    private static final int PUBLISH_INTERVAL_MIN = 50;
    private static final int PUBLISH_INTERVAL_MAX = 200;
    private static final int TRY_COUNT_MAX_LAST_DIGITS = 512;
    private static final int TRY_COUNT_MODULO = 10;
    private final AtomicInteger currentTryCount = new AtomicInteger(0);
    private final AtomicInteger maxTryCount;
    private final BlockingDeque<SplitBrainJoinMessage> splitBrainJoinMessages = new LinkedBlockingDeque<SplitBrainJoinMessage>();

    public MulticastJoiner(Node node) {
        super(node);
        this.maxTryCount = new AtomicInteger(this.calculateTryCount());
        node.multicastService.addMulticastListener(new SplitBrainMulticastListener(node, this.splitBrainJoinMessages));
    }

    @Override
    public void doJoin() {
        long joinStartTime = Clock.currentTimeMillis();
        long maxJoinMillis = this.getMaxJoinMillis();
        Address thisAddress = this.node.getThisAddress();
        while (this.shouldRetry() && Clock.currentTimeMillis() - joinStartTime < maxJoinMillis) {
            this.clusterService.setMasterAddressToJoin(null);
            Address masterAddress = this.getTargetAddress();
            if (masterAddress == null) {
                masterAddress = this.findMasterWithMulticast();
            }
            this.clusterService.setMasterAddressToJoin(masterAddress);
            if (masterAddress == null || thisAddress.equals(masterAddress)) {
                this.clusterJoinManager.setThisMemberAsMaster();
                return;
            }
            this.logger.info("Trying to join to discovered node: " + masterAddress);
            this.joinMaster();
        }
    }

    private void joinMaster() {
        Address master;
        long maxMasterJoinTime = this.getMaxJoinTimeToMasterNode();
        long start = Clock.currentTimeMillis();
        while (this.shouldRetry() && Clock.currentTimeMillis() - start < maxMasterJoinTime && (master = this.clusterService.getMasterAddress()) != null) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Joining to master " + master);
            }
            this.clusterJoinManager.sendJoinRequest(master, true);
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (!this.isBlacklisted(master)) continue;
            this.clusterService.setMasterAddressToJoin(null);
            return;
        }
    }

    @Override
    public void searchForOtherClusters() {
        this.node.multicastService.send(this.node.createSplitBrainJoinMessage());
        try {
            SplitBrainJoinMessage splitBrainMsg;
            while ((splitBrainMsg = this.splitBrainJoinMessages.poll(3L, TimeUnit.SECONDS)) != null) {
                Address targetAddress;
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Received  " + splitBrainMsg);
                }
                if (this.node.clusterService.getMember(targetAddress = splitBrainMsg.getAddress()) != null) {
                    if (!this.logger.isFineEnabled()) continue;
                    this.logger.fine("Ignoring merge join response, since " + targetAddress + " is already a member.");
                    continue;
                }
                SplitBrainJoinMessage request = this.node.createSplitBrainJoinMessage();
                SplitBrainJoinMessage.SplitBrainMergeCheckResult result = this.sendSplitBrainJoinMessageAndCheckResponse(targetAddress, request);
                if (result == SplitBrainJoinMessage.SplitBrainMergeCheckResult.LOCAL_NODE_SHOULD_MERGE) {
                    this.logger.warning(this.node.getThisAddress() + " is merging [multicast] to " + targetAddress);
                    this.startClusterMerge(targetAddress, this.clusterService.getMemberListVersion());
                    return;
                }
                if (result != SplitBrainJoinMessage.SplitBrainMergeCheckResult.REMOTE_NODE_SHOULD_MERGE) continue;
                this.node.multicastService.send(this.node.createSplitBrainJoinMessage());
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.fine(e);
        }
        catch (Exception e) {
            this.logger.warning(e);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.splitBrainJoinMessages.clear();
    }

    @Override
    public String getType() {
        return "multicast";
    }

    public int getSplitBrainMessagesCount() {
        return this.splitBrainJoinMessages.size();
    }

    void onReceivedJoinRequest(JoinRequest joinRequest) {
        if (joinRequest.getUuid().compareTo(this.clusterService.getThisUuid()) < 0) {
            this.maxTryCount.incrementAndGet();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Address findMasterWithMulticast() {
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Searching for master node. Max tries: " + this.maxTryCount.get());
            }
            JoinRequest joinRequest = this.node.createJoinRequest(false);
            while (this.node.isRunning() && this.currentTryCount.incrementAndGet() <= this.maxTryCount.get()) {
                joinRequest.setTryCount(this.currentTryCount.get());
                this.node.multicastService.send(joinRequest);
                Address masterAddress = this.clusterService.getMasterAddress();
                if (masterAddress != null) {
                    Address address = masterAddress;
                    return address;
                }
                Thread.sleep(this.getPublishInterval());
            }
        }
        catch (Exception e) {
            if (this.logger != null) {
                this.logger.warning(e);
            }
        }
        finally {
            this.currentTryCount.set(0);
        }
        return null;
    }

    private int calculateTryCount() {
        int lastDigits;
        NetworkConfig networkConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.config);
        long timeoutMillis = TimeUnit.SECONDS.toMillis(networkConfig.getJoin().getMulticastConfig().getMulticastTimeoutSeconds());
        int avgPublishInterval = 125;
        int tryCount = (int)timeoutMillis / avgPublishInterval;
        String host = this.node.getThisAddress().getHost();
        try {
            lastDigits = Integer.parseInt(host.substring(host.lastIndexOf(46) + 1));
        }
        catch (NumberFormatException e) {
            lastDigits = RandomPicker.getInt(512);
        }
        int portDiff = this.node.getThisAddress().getPort() - networkConfig.getPort();
        return tryCount += (lastDigits + portDiff) % 10;
    }

    private int getPublishInterval() {
        return RandomPicker.getInt(50, 200);
    }
}

