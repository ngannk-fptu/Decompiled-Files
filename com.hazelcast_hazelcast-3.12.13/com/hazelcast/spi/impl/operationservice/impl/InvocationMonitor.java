/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.CallsPerMember;
import com.hazelcast.spi.CanCancelOperations;
import com.hazelcast.spi.LiveOperationsTracker;
import com.hazelcast.spi.OperationControl;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationexecutor.OperationHostileThread;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.InvocationRegistry;
import com.hazelcast.spi.impl.servicemanager.ServiceManager;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.function.Consumer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class InvocationMonitor
implements Consumer<Packet>,
MetricsProvider {
    private static final int HEARTBEAT_CALL_TIMEOUT_RATIO = 4;
    private static final long MAX_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(10L);
    private final NodeEngineImpl nodeEngine;
    private final InternalSerializationService serializationService;
    private final ServiceManager serviceManager;
    private final InvocationRegistry invocationRegistry;
    private final ILogger logger;
    private final ScheduledExecutorService scheduler;
    private final Address thisAddress;
    private final ConcurrentMap<Address, AtomicLong> heartbeatPerMember = new ConcurrentHashMap<Address, AtomicLong>();
    @Probe(name="backupTimeouts", level=ProbeLevel.MANDATORY)
    private final SwCounter backupTimeoutsCount = SwCounter.newSwCounter();
    @Probe(name="normalTimeouts", level=ProbeLevel.MANDATORY)
    private final SwCounter normalTimeoutsCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter heartbeatPacketsReceived = SwCounter.newSwCounter();
    @Probe
    private final SwCounter heartbeatPacketsSend = SwCounter.newSwCounter();
    @Probe
    private final SwCounter delayedExecutionCount = SwCounter.newSwCounter();
    @Probe
    private final long backupTimeoutMillis;
    @Probe
    private final long invocationTimeoutMillis;
    @Probe
    private final long heartbeatBroadcastPeriodMillis;
    @Probe
    private final long invocationScanPeriodMillis = TimeUnit.SECONDS.toMillis(1L);

    InvocationMonitor(NodeEngineImpl nodeEngine, Address thisAddress, HazelcastProperties properties, InvocationRegistry invocationRegistry, ILogger logger, InternalSerializationService serializationService, ServiceManager serviceManager) {
        this.nodeEngine = nodeEngine;
        this.thisAddress = thisAddress;
        this.serializationService = serializationService;
        this.serviceManager = serviceManager;
        this.invocationRegistry = invocationRegistry;
        this.logger = logger;
        this.backupTimeoutMillis = this.backupTimeoutMillis(properties);
        this.invocationTimeoutMillis = this.invocationTimeoutMillis(properties);
        this.heartbeatBroadcastPeriodMillis = this.heartbeatBroadcastPeriodMillis(properties);
        this.scheduler = InvocationMonitor.newScheduler(nodeEngine.getHazelcastInstance().getName());
    }

    public ConcurrentMap<Address, AtomicLong> getHeartbeatPerMember() {
        return this.heartbeatPerMember;
    }

    public long getHeartbeatBroadcastPeriodMillis() {
        return this.heartbeatBroadcastPeriodMillis;
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "operation.invocations");
    }

    private static ScheduledExecutorService newScheduler(final String hzName) {
        return new ScheduledThreadPoolExecutor(1, new ThreadFactory(){

            @Override
            public Thread newThread(Runnable r) {
                return new InvocationMonitorThread(r, hzName);
            }
        });
    }

    private long invocationTimeoutMillis(HazelcastProperties properties) {
        long heartbeatTimeoutMillis = properties.getMillis(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS);
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Operation invocation timeout is " + heartbeatTimeoutMillis + " ms");
        }
        return heartbeatTimeoutMillis;
    }

    private long backupTimeoutMillis(HazelcastProperties properties) {
        long backupTimeoutMillis = properties.getMillis(GroupProperty.OPERATION_BACKUP_TIMEOUT_MILLIS);
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Operation backup timeout is " + backupTimeoutMillis + " ms");
        }
        return backupTimeoutMillis;
    }

    private long heartbeatBroadcastPeriodMillis(HazelcastProperties properties) {
        int callTimeoutMs = properties.getInteger(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS);
        long periodMs = Math.max(TimeUnit.SECONDS.toMillis(1L), (long)(callTimeoutMs / 4));
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Operation heartbeat period is " + periodMs + " ms");
        }
        return periodMs;
    }

    void onMemberLeft(MemberImpl member) {
        int memberListVersion = this.nodeEngine.getClusterService().getMemberListVersion();
        this.scheduler.execute(new OnMemberLeftTask(member, memberListVersion));
    }

    void onEndpointLeft(Address endpoint) {
        this.scheduler.execute(new OnEndpointLeftTask(endpoint));
    }

    void execute(Runnable runnable) {
        this.scheduler.execute(runnable);
    }

    void schedule(Runnable command, long delayMillis) {
        this.scheduler.schedule(command, delayMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void accept(Packet packet) {
        this.scheduler.execute(new ProcessOperationControlTask(packet));
    }

    public void start() {
        MonitorInvocationsTask monitorInvocationsTask = new MonitorInvocationsTask(this.invocationScanPeriodMillis);
        this.scheduler.scheduleAtFixedRate(monitorInvocationsTask, 0L, monitorInvocationsTask.periodMillis, TimeUnit.MILLISECONDS);
        BroadcastOperationControlTask broadcastOperationControlTask = new BroadcastOperationControlTask(this.heartbeatBroadcastPeriodMillis);
        this.scheduler.scheduleAtFixedRate(broadcastOperationControlTask, 0L, broadcastOperationControlTask.periodMillis, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        this.scheduler.shutdown();
    }

    public void awaitTermination(long timeoutMillis) throws InterruptedException {
        this.scheduler.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    long getLastMemberHeartbeatMillis(Address memberAddress) {
        if (memberAddress == null) {
            return 0L;
        }
        AtomicLong heartbeat = (AtomicLong)this.heartbeatPerMember.get(memberAddress);
        return heartbeat == null ? 0L : heartbeat.get();
    }

    private static final class InvocationMonitorThread
    extends Thread
    implements OperationHostileThread {
        private InvocationMonitorThread(Runnable task, String hzName) {
            super(task, ThreadUtil.createThreadName(hzName, "InvocationMonitorThread"));
        }
    }

    private final class BroadcastOperationControlTask
    extends FixedRateMonitorTask {
        private final CallsPerMember calls;

        private BroadcastOperationControlTask(long periodMillis) {
            super(periodMillis);
            this.calls = new CallsPerMember(InvocationMonitor.this.thisAddress);
        }

        @Override
        public void run0() {
            CallsPerMember calls = this.populate();
            Set<Address> addresses = calls.addresses();
            if (InvocationMonitor.this.logger.isFinestEnabled()) {
                InvocationMonitor.this.logger.finest("Broadcasting operation control packets to: " + addresses.size() + " members");
            }
            for (Address address : addresses) {
                this.sendOpControlPacket(address, calls.toOpControl(address));
            }
        }

        private CallsPerMember populate() {
            this.calls.clear();
            ClusterService clusterService = InvocationMonitor.this.nodeEngine.getClusterService();
            this.calls.ensureMember(InvocationMonitor.this.thisAddress);
            for (Member member : clusterService.getMembers()) {
                this.calls.ensureMember(member.getAddress());
            }
            for (LiveOperationsTracker tracker : InvocationMonitor.this.serviceManager.getServices(LiveOperationsTracker.class)) {
                tracker.populate(this.calls);
            }
            for (Invocation invocation : InvocationMonitor.this.invocationRegistry) {
                if (!invocation.future.isCancelled()) continue;
                this.calls.addOpToCancel(invocation.getTargetAddress(), invocation.op.getCallId());
            }
            return this.calls;
        }

        private void sendOpControlPacket(Address address, OperationControl opControl) {
            InvocationMonitor.this.heartbeatPacketsSend.inc();
            if (address.equals(InvocationMonitor.this.thisAddress)) {
                InvocationMonitor.this.scheduler.execute(new ProcessOperationControlTask(opControl));
            } else {
                Packet packet = new Packet(InvocationMonitor.this.serializationService.toBytes(opControl)).setPacketType(Packet.Type.OPERATION).raiseFlags(80);
                InvocationMonitor.this.nodeEngine.getNode().getNetworkingService().getEndpointManager(EndpointQualifier.MEMBER).transmit(packet, address);
            }
        }
    }

    private final class ProcessOperationControlTask
    extends MonitorTask {
        private final Object payload;
        private final Address sender;

        ProcessOperationControlTask(OperationControl payload) {
            this.payload = payload;
            this.sender = InvocationMonitor.this.thisAddress;
        }

        ProcessOperationControlTask(Packet payload) {
            this.payload = payload;
            this.sender = payload.getConn().getEndPoint();
        }

        @Override
        public void run0() {
            InvocationMonitor.this.heartbeatPacketsReceived.inc();
            long nowMillis = Clock.currentTimeMillis();
            this.updateMemberHeartbeat(nowMillis);
            OperationControl opControl = (OperationControl)InvocationMonitor.this.serializationService.toObject(this.payload);
            for (long callId : opControl.runningOperations()) {
                this.updateHeartbeat(callId, nowMillis);
            }
            Object object = InvocationMonitor.this.serviceManager.getServices(CanCancelOperations.class).iterator();
            while (object.hasNext()) {
                CanCancelOperations service = (CanCancelOperations)object.next();
                long[] opsToCancel = opControl.operationsToCancel();
                for (int i = 0; i < opsToCancel.length; ++i) {
                    if (opsToCancel[i] == -1L || !service.cancelOperation(this.sender, opsToCancel[i])) continue;
                    opsToCancel[i] = -1L;
                }
            }
        }

        private void updateMemberHeartbeat(long nowMillis) {
            AtomicLong heartbeat = (AtomicLong)InvocationMonitor.this.heartbeatPerMember.get(this.sender);
            if (heartbeat == null) {
                heartbeat = new AtomicLong(nowMillis);
                InvocationMonitor.this.heartbeatPerMember.put(this.sender, heartbeat);
                return;
            }
            heartbeat.set(nowMillis);
        }

        private void updateHeartbeat(long callId, long nowMillis) {
            Invocation invocation = InvocationMonitor.this.invocationRegistry.get(callId);
            if (invocation == null) {
                return;
            }
            invocation.lastHeartbeatMillis = nowMillis;
        }
    }

    private final class OnMemberLeftTask
    extends MonitorTask {
        private final MemberImpl leftMember;
        private final int memberListVersion;

        private OnMemberLeftTask(MemberImpl leftMember, int memberListVersion) {
            this.leftMember = leftMember;
            this.memberListVersion = memberListVersion;
        }

        @Override
        public void run0() {
            InvocationMonitor.this.heartbeatPerMember.remove(this.leftMember.getAddress());
            for (Invocation invocation : InvocationMonitor.this.invocationRegistry) {
                if (this.hasTargetLeft(invocation)) {
                    this.onTargetLoss(invocation);
                    continue;
                }
                this.onPotentialBackupLoss(invocation);
            }
        }

        private boolean hasTargetLeft(Invocation invocation) {
            Member targetMember = invocation.getTargetMember();
            if (targetMember == null) {
                Address invTarget = invocation.getTargetAddress();
                return this.leftMember.getAddress().equals(invTarget);
            }
            return this.leftMember.getUuid().equals(targetMember.getUuid());
        }

        private void onTargetLoss(Invocation invocation) {
            if (invocation.getMemberListVersion() < this.memberListVersion) {
                invocation.notifyError(new MemberLeftException(this.leftMember));
            }
        }

        private void onPotentialBackupLoss(Invocation invocation) {
            invocation.notifyBackupComplete();
        }
    }

    private final class OnEndpointLeftTask
    extends MonitorTask {
        private final Address endpoint;

        private OnEndpointLeftTask(Address endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public void run0() {
            InvocationMonitor.this.heartbeatPerMember.remove(this.endpoint);
            for (Invocation invocation : InvocationMonitor.this.invocationRegistry) {
                if (!this.endpoint.equals(invocation.getTargetAddress())) continue;
                invocation.notifyError(new MemberLeftException("Endpoint " + this.endpoint + " has left"));
            }
        }
    }

    private final class MonitorInvocationsTask
    extends FixedRateMonitorTask {
        private MonitorInvocationsTask(long periodMillis) {
            super(periodMillis);
        }

        @Override
        public void run0() {
            if (InvocationMonitor.this.logger.isFinestEnabled()) {
                InvocationMonitor.this.logger.finest("Scanning all invocations");
            }
            if (InvocationMonitor.this.invocationRegistry.size() == 0) {
                return;
            }
            int backupTimeouts = 0;
            int normalTimeouts = 0;
            int invocationCount = 0;
            for (Map.Entry<Long, Invocation> e : InvocationMonitor.this.invocationRegistry.entrySet()) {
                ++invocationCount;
                Invocation inv = e.getValue();
                try {
                    if (inv.detectAndHandleTimeout(InvocationMonitor.this.invocationTimeoutMillis)) {
                        ++normalTimeouts;
                        continue;
                    }
                    if (!inv.detectAndHandleBackupTimeout(InvocationMonitor.this.backupTimeoutMillis)) continue;
                    ++backupTimeouts;
                }
                catch (Throwable t) {
                    OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(t);
                    InvocationMonitor.this.logger.severe("Failed to check invocation:" + inv, t);
                }
            }
            InvocationMonitor.this.backupTimeoutsCount.inc(backupTimeouts);
            InvocationMonitor.this.normalTimeoutsCount.inc(normalTimeouts);
            this.log(invocationCount, backupTimeouts, normalTimeouts);
        }

        private void log(int invocationCount, int backupTimeouts, int invocationTimeouts) {
            Level logLevel = null;
            if (backupTimeouts > 0 || invocationTimeouts > 0) {
                logLevel = Level.INFO;
            } else if (InvocationMonitor.this.logger.isFineEnabled()) {
                logLevel = Level.FINE;
            }
            if (logLevel != null) {
                InvocationMonitor.this.logger.log(logLevel, "Invocations:" + invocationCount + " timeouts:" + invocationTimeouts + " backup-timeouts:" + backupTimeouts);
            }
        }
    }

    abstract class FixedRateMonitorTask
    implements Runnable {
        final long periodMillis;
        private long expectedNextMillis = System.currentTimeMillis();

        FixedRateMonitorTask(long periodMillis) {
            this.periodMillis = periodMillis;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            try {
                if (this.expectedNextMillis + MAX_DELAY_MILLIS < currentTimeMillis) {
                    InvocationMonitor.this.logger.warning(this.getClass().getSimpleName() + " delayed " + (currentTimeMillis - this.expectedNextMillis) + " ms");
                    InvocationMonitor.this.delayedExecutionCount.inc();
                }
                this.run0();
            }
            catch (Throwable t) {
                OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(t);
                InvocationMonitor.this.logger.severe(t);
            }
            finally {
                this.expectedNextMillis = currentTimeMillis + this.periodMillis;
            }
        }

        protected abstract void run0();
    }

    private abstract class MonitorTask
    implements Runnable {
        private MonitorTask() {
        }

        @Override
        public void run() {
            try {
                this.run0();
            }
            catch (Throwable t) {
                OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(t);
                InvocationMonitor.this.logger.severe(t);
            }
        }

        protected abstract void run0();
    }
}

