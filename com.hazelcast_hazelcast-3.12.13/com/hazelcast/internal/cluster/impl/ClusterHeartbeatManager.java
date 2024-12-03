/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.IcmpFailureDetectorConfig;
import com.hazelcast.core.Member;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.fd.ClusterFailureDetector;
import com.hazelcast.internal.cluster.fd.ClusterFailureDetectorType;
import com.hazelcast.internal.cluster.fd.DeadlineClusterFailureDetector;
import com.hazelcast.internal.cluster.fd.PhiAccrualClusterFailureDetector;
import com.hazelcast.internal.cluster.fd.PingFailureDetector;
import com.hazelcast.internal.cluster.impl.ClusterClockImpl;
import com.hazelcast.internal.cluster.impl.ClusterJoinManager;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.MembershipManager;
import com.hazelcast.internal.cluster.impl.operations.ExplicitSuspicionOp;
import com.hazelcast.internal.cluster.impl.operations.HeartbeatComplaintOp;
import com.hazelcast.internal.cluster.impl.operations.HeartbeatOp;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.quorum.impl.QuorumServiceImpl;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ICMPHelper;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;

public class ClusterHeartbeatManager {
    private static final long CLOCK_JUMP_THRESHOLD = TimeUnit.MINUTES.toMillis(2L);
    private static final int HEART_BEAT_INTERVAL_FACTOR = 10;
    private static final int MAX_PING_RETRY_COUNT = 5;
    private static final long MIN_ICMP_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(1L);
    private final ILogger logger;
    private final Lock clusterServiceLock;
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final ClusterServiceImpl clusterService;
    private final ClusterClockImpl clusterClock;
    private final ClusterFailureDetector heartbeatFailureDetector;
    private final PingFailureDetector<Member> icmpFailureDetector;
    private final long maxNoHeartbeatMillis;
    private final long heartbeatIntervalMillis;
    private final long legacyIcmpCheckThresholdMillis;
    private final boolean icmpEnabled;
    private final boolean icmpParallelMode;
    private final int icmpTtl;
    private final int icmpTimeoutMillis;
    private final int icmpIntervalMillis;
    private final int icmpMaxAttempts;
    @Probe(name="lastHeartbeat")
    private volatile long lastHeartbeat;
    private volatile long lastClusterTimeDiff;

    ClusterHeartbeatManager(Node node, ClusterServiceImpl clusterService, Lock lock) {
        this.node = node;
        this.clusterService = clusterService;
        this.nodeEngine = node.getNodeEngine();
        this.clusterClock = clusterService.getClusterClock();
        this.logger = node.getLogger(this.getClass());
        this.clusterServiceLock = lock;
        HazelcastProperties hazelcastProperties = node.getProperties();
        this.maxNoHeartbeatMillis = hazelcastProperties.getMillis(GroupProperty.MAX_NO_HEARTBEAT_SECONDS);
        this.heartbeatIntervalMillis = ClusterHeartbeatManager.getHeartbeatInterval(hazelcastProperties);
        this.legacyIcmpCheckThresholdMillis = this.heartbeatIntervalMillis * 10L;
        IcmpFailureDetectorConfig icmpFailureDetectorConfig = ConfigAccessor.getActiveMemberNetworkConfig(node.config).getIcmpFailureDetectorConfig();
        this.icmpTtl = icmpFailureDetectorConfig == null ? hazelcastProperties.getInteger(GroupProperty.ICMP_TTL) : icmpFailureDetectorConfig.getTtl();
        this.icmpTimeoutMillis = icmpFailureDetectorConfig == null ? (int)hazelcastProperties.getMillis(GroupProperty.ICMP_TIMEOUT) : icmpFailureDetectorConfig.getTimeoutMilliseconds();
        this.icmpIntervalMillis = icmpFailureDetectorConfig == null ? (int)hazelcastProperties.getMillis(GroupProperty.ICMP_INTERVAL) : icmpFailureDetectorConfig.getIntervalMilliseconds();
        this.icmpMaxAttempts = icmpFailureDetectorConfig == null ? hazelcastProperties.getInteger(GroupProperty.ICMP_MAX_ATTEMPTS) : icmpFailureDetectorConfig.getMaxAttempts();
        boolean bl = this.icmpEnabled = icmpFailureDetectorConfig == null ? hazelcastProperties.getBoolean(GroupProperty.ICMP_ENABLED) : icmpFailureDetectorConfig.isEnabled();
        boolean bl2 = this.icmpEnabled && (icmpFailureDetectorConfig == null ? hazelcastProperties.getBoolean(GroupProperty.ICMP_PARALLEL_MODE) : icmpFailureDetectorConfig.isParallelMode()) ? true : (this.icmpParallelMode = false);
        if (this.icmpTimeoutMillis > this.icmpIntervalMillis) {
            throw new IllegalStateException("ICMP timeout is set to a value greater than the ICMP interval, this is not allowed.");
        }
        if ((long)this.icmpIntervalMillis < MIN_ICMP_INTERVAL_MILLIS) {
            throw new IllegalStateException("ICMP interval is set to a value less than the min allowed, " + MIN_ICMP_INTERVAL_MILLIS + "ms");
        }
        this.icmpFailureDetector = this.createIcmpFailureDetectorIfNeeded(hazelcastProperties);
        this.heartbeatFailureDetector = this.createHeartbeatFailureDetector(hazelcastProperties);
    }

    private PingFailureDetector createIcmpFailureDetectorIfNeeded(HazelcastProperties properties) {
        boolean icmpEchoFailFast;
        IcmpFailureDetectorConfig icmpFailureDetectorConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.node.config).getIcmpFailureDetectorConfig();
        boolean bl = icmpEchoFailFast = icmpFailureDetectorConfig == null ? properties.getBoolean(GroupProperty.ICMP_ECHO_FAIL_FAST) : icmpFailureDetectorConfig.isFailFastOnStartup();
        if (this.icmpParallelMode) {
            if (icmpEchoFailFast) {
                this.logger.info("Checking that ICMP failure-detector is permitted. Attempting to create a raw-socket using JNI.");
                if (!ICMPHelper.isRawSocketPermitted()) {
                    throw new IllegalStateException("ICMP failure-detector can't be used in this environment. Check Hazelcast Documentation Chapter on the Ping Failure Detector for supported platforms and how to enable this capability for your operating system");
                }
                this.logger.info("ICMP failure-detector is supported, enabling.");
            }
            return new PingFailureDetector(this.icmpMaxAttempts);
        }
        return null;
    }

    private ClusterFailureDetector createHeartbeatFailureDetector(HazelcastProperties properties) {
        String type = properties.getString(GroupProperty.HEARTBEAT_FAILURE_DETECTOR_TYPE);
        ClusterFailureDetectorType fdType = ClusterFailureDetectorType.of(type);
        switch (fdType) {
            case DEADLINE: {
                return new DeadlineClusterFailureDetector(this.maxNoHeartbeatMillis);
            }
            case PHI_ACCRUAL: {
                int defaultValue = Integer.parseInt(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getDefaultValue());
                if (this.maxNoHeartbeatMillis == TimeUnit.SECONDS.toMillis(defaultValue)) {
                    this.logger.warning("When using Phi-Accrual Failure Detector, please consider using a lower '" + GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName() + "' value. Current is: " + defaultValue + " seconds.");
                }
                return new PhiAccrualClusterFailureDetector(this.maxNoHeartbeatMillis, this.heartbeatIntervalMillis, properties);
            }
        }
        throw new IllegalArgumentException("Unknown failure detector type: " + type);
    }

    public long getHeartbeatIntervalMillis() {
        return this.heartbeatIntervalMillis;
    }

    public long getLastHeartbeatTime(Member member) {
        return this.heartbeatFailureDetector.lastHeartbeat(member);
    }

    private static long getHeartbeatInterval(HazelcastProperties hazelcastProperties) {
        long heartbeatInterval = hazelcastProperties.getMillis(GroupProperty.HEARTBEAT_INTERVAL_SECONDS);
        return heartbeatInterval > 0L ? heartbeatInterval : TimeUnit.SECONDS.toMillis(1L);
    }

    void init() {
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        executionService.scheduleWithRepetition("hz:cluster", new Runnable(){

            @Override
            public void run() {
                ClusterHeartbeatManager.this.heartbeat();
            }
        }, this.heartbeatIntervalMillis, this.heartbeatIntervalMillis, TimeUnit.MILLISECONDS);
        if (this.icmpParallelMode) {
            this.startPeriodicPinger();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleHeartbeat(MembersViewMetadata senderMembersViewMetadata, String receiverUuid, long timestamp) {
        Address senderAddress = senderMembersViewMetadata.getMemberAddress();
        try {
            long timeout = Math.min(TimeUnit.SECONDS.toMillis(1L), this.heartbeatIntervalMillis / 2L);
            if (!this.clusterServiceLock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                this.logger.warning("Cannot handle heartbeat from " + senderAddress + ", could not acquire lock in time.");
                return;
            }
        }
        catch (InterruptedException e) {
            this.logger.warning("Cannot handle heartbeat from " + senderAddress + ", thread interrupted.");
            Thread.currentThread().interrupt();
            return;
        }
        try {
            if (!this.clusterService.isJoined()) {
                if (this.clusterService.getThisUuid().equals(receiverUuid)) {
                    this.logger.fine("Ignoring heartbeat of sender: " + senderMembersViewMetadata + ", because node is not joined!");
                } else {
                    this.logger.fine("Sending explicit suspicion to " + senderAddress + " for heartbeat " + senderMembersViewMetadata + ", because this node has received an invalid heartbeat before it joins to the cluster");
                    InternalOperationService operationService = this.nodeEngine.getOperationService();
                    ExplicitSuspicionOp op = new ExplicitSuspicionOp(senderMembersViewMetadata);
                    operationService.send(op, senderAddress);
                }
                return;
            }
            MembershipManager membershipManager = this.clusterService.getMembershipManager();
            MemberImpl member = membershipManager.getMember(senderAddress, senderMembersViewMetadata.getMemberUuid());
            if (member != null) {
                if (this.clusterService.getThisUuid().equals(receiverUuid)) {
                    this.onHeartbeat(member, timestamp);
                    return;
                }
                this.logger.warning("Local UUID mismatch on received heartbeat. local UUID: " + this.clusterService.getThisUuid() + " received UUID: " + receiverUuid + " with " + senderMembersViewMetadata);
            }
            this.onInvalidHeartbeat(senderMembersViewMetadata);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void onInvalidHeartbeat(MembersViewMetadata senderMembersViewMetadata) {
        Address senderAddress = senderMembersViewMetadata.getMemberAddress();
        if (this.clusterService.isMaster()) {
            if (!this.clusterService.getClusterJoinManager().isMastershipClaimInProgress()) {
                this.logger.fine("Sending explicit suspicion to " + senderAddress + " for heartbeat " + senderMembersViewMetadata + ", because it is not a member of this cluster or its heartbeat cannot be validated!");
                this.clusterService.sendExplicitSuspicion(senderMembersViewMetadata);
            }
        } else {
            Address masterAddress = this.clusterService.getMasterAddress();
            if (this.clusterService.getMembershipManager().isMemberSuspected(masterAddress)) {
                this.logger.fine("Not sending heartbeat complaint for " + senderMembersViewMetadata + " to suspected master: " + masterAddress);
                return;
            }
            this.logger.fine("Sending heartbeat complaint to master " + masterAddress + " for heartbeat " + senderMembersViewMetadata + ", because it is not a member of this cluster or its heartbeat cannot be validated!");
            this.sendHeartbeatComplaintToMaster(senderMembersViewMetadata);
        }
    }

    private void sendHeartbeatComplaintToMaster(MembersViewMetadata senderMembersViewMetadata) {
        if (this.clusterService.isMaster()) {
            this.logger.warning("Cannot send heartbeat complaint for " + senderMembersViewMetadata + " to itself.");
            return;
        }
        Address masterAddress = this.clusterService.getMasterAddress();
        if (masterAddress == null) {
            this.logger.fine("Cannot send heartbeat complaint for " + senderMembersViewMetadata.getMemberAddress() + ", master address is not set.");
            return;
        }
        MembersViewMetadata localMembersViewMetadata = this.clusterService.getMembershipManager().createLocalMembersViewMetadata();
        HeartbeatComplaintOp op = new HeartbeatComplaintOp(localMembersViewMetadata, senderMembersViewMetadata);
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        operationService.send(op, masterAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleHeartbeatComplaint(MembersViewMetadata receiverMVMetadata, MembersViewMetadata senderMVMetadata) {
        this.clusterServiceLock.lock();
        try {
            if (!this.clusterService.isJoined()) {
                this.logger.warning("Ignoring heartbeat complaint of receiver: " + receiverMVMetadata + " and sender: " + senderMVMetadata + " because not joined!");
                return;
            }
            MembershipManager membershipManager = this.clusterService.getMembershipManager();
            ClusterJoinManager clusterJoinManager = this.clusterService.getClusterJoinManager();
            if (!this.clusterService.isMaster()) {
                this.logger.warning("Ignoring heartbeat complaint of receiver: " + receiverMVMetadata + " for sender: " + senderMVMetadata + " because this node is not master");
                return;
            }
            if (clusterJoinManager.isMastershipClaimInProgress()) {
                this.logger.fine("Ignoring heartbeat complaint of receiver: " + receiverMVMetadata + " for sender: " + senderMVMetadata + " because mastership claim process is ongoing");
                return;
            }
            if (senderMVMetadata.getMemberAddress().equals(receiverMVMetadata.getMemberAddress())) {
                this.logger.warning("Ignoring heartbeat complaint of receiver: " + receiverMVMetadata + " for sender: " + senderMVMetadata + " because they are same member");
                return;
            }
            if (membershipManager.validateMembersViewMetadata(senderMVMetadata)) {
                if (membershipManager.validateMembersViewMetadata(receiverMVMetadata)) {
                    this.logger.fine("Sending latest member list to " + senderMVMetadata.getMemberAddress() + " and " + receiverMVMetadata.getMemberAddress() + " after heartbeat complaint.");
                    membershipManager.sendMemberListToMember(senderMVMetadata.getMemberAddress());
                    membershipManager.sendMemberListToMember(receiverMVMetadata.getMemberAddress());
                } else {
                    this.logger.fine("Complainer " + receiverMVMetadata.getMemberAddress() + " will explicitly suspect from " + this.node.getThisAddress() + " and " + senderMVMetadata.getMemberAddress());
                    this.clusterService.sendExplicitSuspicion(receiverMVMetadata);
                    this.clusterService.sendExplicitSuspicionTrigger(senderMVMetadata.getMemberAddress(), receiverMVMetadata);
                }
            } else if (membershipManager.validateMembersViewMetadata(receiverMVMetadata)) {
                this.logger.fine("Complainee " + senderMVMetadata.getMemberAddress() + " will explicitly suspect from " + this.node.getThisAddress() + " and " + receiverMVMetadata.getMemberAddress());
                this.clusterService.sendExplicitSuspicion(senderMVMetadata);
                this.clusterService.sendExplicitSuspicionTrigger(receiverMVMetadata.getMemberAddress(), senderMVMetadata);
            } else {
                this.logger.fine("Both complainer " + receiverMVMetadata.getMemberAddress() + " and complainee " + senderMVMetadata.getMemberAddress() + " will explicitly suspect from " + this.node.getThisAddress());
                this.clusterService.sendExplicitSuspicion(senderMVMetadata);
                this.clusterService.sendExplicitSuspicion(receiverMVMetadata);
            }
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    public void onHeartbeat(MemberImpl member, long timestamp) {
        if (member == null) {
            return;
        }
        long clusterTime = this.clusterClock.getClusterTime();
        if (this.logger.isFineEnabled()) {
            this.logger.fine(String.format("Received heartbeat from %s (now: %s, timestamp: %s)", member, StringUtil.timeToString(clusterTime), StringUtil.timeToString(timestamp)));
        }
        if (clusterTime - timestamp > this.maxNoHeartbeatMillis / 2L) {
            this.logger.warning(String.format("Ignoring heartbeat from %s since it is expired (now: %s, timestamp: %s)", member, StringUtil.timeToString(clusterTime), StringUtil.timeToString(timestamp)));
            return;
        }
        if (this.isMaster(member)) {
            this.clusterClock.setMasterTime(timestamp);
        }
        this.heartbeatFailureDetector.heartbeat(member, this.clusterClock.getClusterTime());
        MembershipManager membershipManager = this.clusterService.getMembershipManager();
        membershipManager.clearMemberSuspicion(member.getAddress(), "Valid heartbeat");
        this.nodeEngine.getQuorumService().onHeartbeat(member, timestamp);
    }

    void heartbeat() {
        if (!this.clusterService.isJoined()) {
            return;
        }
        this.checkClockDrift(this.heartbeatIntervalMillis);
        long clusterTime = this.clusterClock.getClusterTime();
        if (this.clusterService.isMaster()) {
            this.heartbeatWhenMaster(clusterTime);
        } else {
            this.heartbeatWhenSlave(clusterTime);
        }
    }

    private void checkClockDrift(long intervalMillis) {
        long now = Clock.currentTimeMillis();
        if (this.lastHeartbeat != 0L) {
            long clockJump = now - this.lastHeartbeat - intervalMillis;
            long absoluteClockJump = Math.abs(clockJump);
            if (absoluteClockJump > CLOCK_JUMP_THRESHOLD) {
                this.logger.info(String.format("System clock apparently jumped from %s to %s since last heartbeat (%+d ms)", StringUtil.timeToString(this.lastHeartbeat), StringUtil.timeToString(now), clockJump));
                long currentClusterTimeDiff = this.clusterClock.getClusterTimeDiff();
                if (Math.abs(this.lastClusterTimeDiff - currentClusterTimeDiff) < CLOCK_JUMP_THRESHOLD) {
                    this.clusterClock.setClusterTimeDiff(currentClusterTimeDiff - clockJump);
                }
            }
            if (absoluteClockJump >= this.maxNoHeartbeatMillis / 2L) {
                this.logger.warning(String.format("Resetting heartbeat timestamps because of huge system clock jump! Clock-Jump: %d ms, Heartbeat-Timeout: %d ms", clockJump, this.maxNoHeartbeatMillis));
                this.resetHeartbeats();
            }
        }
        this.lastClusterTimeDiff = this.clusterClock.getClusterTimeDiff();
        this.lastHeartbeat = now;
    }

    private void heartbeatWhenMaster(long now) {
        Collection<MemberImpl> members = this.clusterService.getMemberImpls();
        for (MemberImpl member : members) {
            if (member.localMember()) continue;
            try {
                this.logIfConnectionToEndpointIsMissing(now, member);
                if (this.suspectMemberIfNotHeartBeating(now, member)) continue;
                this.pingMemberIfRequired(now, member);
                this.sendHeartbeat(member);
            }
            catch (Throwable e) {
                this.logger.severe(e);
            }
        }
    }

    private boolean suspectMemberIfNotHeartBeating(long now, Member member) {
        if (this.clusterService.getMembershipManager().isMemberSuspected(member.getAddress())) {
            return true;
        }
        long lastHeartbeat = this.heartbeatFailureDetector.lastHeartbeat(member);
        if (!this.heartbeatFailureDetector.isAlive(member, now)) {
            double suspicionLevel = this.heartbeatFailureDetector.suspicionLevel(member, now);
            String reason = String.format("Suspecting %s because it has not sent any heartbeats since %s. Now: %s, heartbeat timeout: %d ms, suspicion level: %.2f", member, StringUtil.timeToString(lastHeartbeat), StringUtil.timeToString(now), this.maxNoHeartbeatMillis, suspicionLevel);
            this.logger.warning(reason);
            this.clusterService.suspectMember(member, reason, true);
            return true;
        }
        if (this.logger.isFineEnabled() && now - lastHeartbeat > this.heartbeatIntervalMillis * 10L) {
            double suspicionLevel = this.heartbeatFailureDetector.suspicionLevel(member, now);
            this.logger.fine(String.format("Not receiving any heartbeats from %s since %s, suspicion level: %.2f", member, StringUtil.timeToString(lastHeartbeat), suspicionLevel));
        }
        return false;
    }

    private void heartbeatWhenSlave(long now) {
        MembershipManager membershipManager = this.clusterService.getMembershipManager();
        Collection<Member> members = this.clusterService.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
        for (Member member : members) {
            try {
                this.logIfConnectionToEndpointIsMissing(now, member);
                if (this.suspectMemberIfNotHeartBeating(now, member) || membershipManager.isMemberSuspected(member.getAddress())) continue;
                this.pingMemberIfRequired(now, member);
                this.sendHeartbeat(member);
            }
            catch (Throwable e) {
                this.logger.severe(e);
            }
        }
    }

    private boolean isMaster(MemberImpl member) {
        return member.getAddress().equals(this.clusterService.getMasterAddress());
    }

    private void pingMemberIfRequired(long now, Member member) {
        if (!this.icmpEnabled || this.icmpParallelMode) {
            return;
        }
        long lastHeartbeat = this.heartbeatFailureDetector.lastHeartbeat(member);
        if (now - lastHeartbeat >= this.legacyIcmpCheckThresholdMillis) {
            this.runPingTask(member);
        }
    }

    private void startPeriodicPinger() {
        this.nodeEngine.getExecutionService().scheduleWithRepetition("hz:cluster", new Runnable(){

            @Override
            public void run() {
                Collection<Member> members = ClusterHeartbeatManager.this.clusterService.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
                for (Member member : members) {
                    try {
                        ClusterHeartbeatManager.this.runPingTask(member);
                    }
                    catch (Throwable e) {
                        ClusterHeartbeatManager.this.logger.severe(e);
                    }
                }
            }
        }, this.icmpIntervalMillis, this.icmpIntervalMillis, TimeUnit.MILLISECONDS);
    }

    private void runPingTask(Member member) {
        this.nodeEngine.getExecutionService().execute("hz:system", this.icmpParallelMode ? new PeriodicPingTask(member) : new PingTask(member));
    }

    private void sendHeartbeat(Member target) {
        block3: {
            if (target == null) {
                return;
            }
            try {
                MembersViewMetadata membersViewMetadata = this.clusterService.getMembershipManager().createLocalMembersViewMetadata();
                HeartbeatOp op = new HeartbeatOp(membersViewMetadata, target.getUuid(), this.clusterClock.getClusterTime());
                op.setCallerUuid(this.clusterService.getThisUuid());
                this.node.nodeEngine.getOperationService().send(op, target.getAddress());
            }
            catch (Exception e) {
                if (!this.logger.isFineEnabled()) break block3;
                this.logger.fine(String.format("Error while sending heartbeat -> %s[%s]", e.getClass().getName(), e.getMessage()));
            }
        }
    }

    private void logIfConnectionToEndpointIsMissing(long now, Member member) {
        Object conn;
        long heartbeatTime = this.heartbeatFailureDetector.lastHeartbeat(member);
        if (!(now - heartbeatTime < this.heartbeatIntervalMillis * 10L || (conn = this.node.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(member.getAddress())) != null && conn.isAlive())) {
            this.logger.warning("This node does not have a connection to " + member);
        }
    }

    public PingFailureDetector getIcmpFailureDetector() {
        return this.icmpFailureDetector;
    }

    private void resetHeartbeats() {
        QuorumServiceImpl quorumService = this.nodeEngine.getQuorumService();
        long now = this.clusterClock.getClusterTime();
        for (MemberImpl member : this.clusterService.getMemberImpls()) {
            this.heartbeatFailureDetector.heartbeat(member, now);
            quorumService.onHeartbeat(member, now);
        }
    }

    void removeMember(MemberImpl member) {
        this.heartbeatFailureDetector.remove(member);
        if (this.icmpParallelMode) {
            this.icmpFailureDetector.remove(member);
        }
    }

    void reset() {
        this.heartbeatFailureDetector.reset();
        if (this.icmpParallelMode) {
            this.icmpFailureDetector.reset();
        }
    }

    private class PeriodicPingTask
    extends PingTask {
        final QuorumServiceImpl quorumService;

        PeriodicPingTask(Member member) {
            super(member);
            this.quorumService = ClusterHeartbeatManager.this.nodeEngine.getQuorumService();
        }

        @Override
        public void run() {
            Address address = this.member.getAddress();
            ClusterHeartbeatManager.this.logger.fine(String.format("%s will ping %s", ClusterHeartbeatManager.this.node.getThisAddress(), address));
            if (this.doPing(address, Level.FINE)) {
                boolean pingRestored;
                boolean bl = pingRestored = ClusterHeartbeatManager.this.icmpFailureDetector.heartbeat(this.member) > 0;
                if (pingRestored) {
                    this.quorumService.onPingRestored(this.member);
                }
                return;
            }
            ClusterHeartbeatManager.this.icmpFailureDetector.logAttempt(this.member);
            this.quorumService.onPingLost(this.member);
            String reason = String.format("%s could not ping %s", ClusterHeartbeatManager.this.node.getThisAddress(), address);
            ClusterHeartbeatManager.this.logger.warning(reason);
            if (!ClusterHeartbeatManager.this.icmpFailureDetector.isAlive(this.member)) {
                ClusterHeartbeatManager.this.clusterService.suspectMember(this.member, reason, true);
            }
        }
    }

    private class PingTask
    implements Runnable {
        final Member member;

        PingTask(Member member) {
            this.member = member;
        }

        @Override
        public void run() {
            Address address = this.member.getAddress();
            ClusterHeartbeatManager.this.logger.warning(String.format("%s will ping %s", ClusterHeartbeatManager.this.node.getThisAddress(), address));
            for (int i = 0; i < 5; ++i) {
                if (!this.doPing(address, Level.INFO)) continue;
                return;
            }
            String reason = String.format("%s could not ping %s", ClusterHeartbeatManager.this.node.getThisAddress(), address);
            ClusterHeartbeatManager.this.logger.warning(reason);
            ClusterHeartbeatManager.this.clusterService.suspectMember(this.member, reason, true);
        }

        boolean doPing(Address address, Level level) {
            block4: {
                try {
                    if (address.getInetAddress().isReachable(null, ClusterHeartbeatManager.this.icmpTtl, ClusterHeartbeatManager.this.icmpTimeoutMillis)) {
                        String msg = String.format("%s pinged %s successfully", ClusterHeartbeatManager.this.node.getThisAddress(), address);
                        ClusterHeartbeatManager.this.logger.log(level, msg);
                        return true;
                    }
                }
                catch (ConnectException ignored) {
                    EmptyStatement.ignore(ignored);
                }
                catch (IOException e) {
                    if (!ClusterHeartbeatManager.this.logger.isFinestEnabled()) break block4;
                    ClusterHeartbeatManager.this.logger.finest("Failed while pinging " + address, e);
                }
            }
            return false;
        }
    }
}

