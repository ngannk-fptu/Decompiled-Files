/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.CRDTReplicationConfig;
import com.hazelcast.core.Member;
import com.hazelcast.crdt.CRDTMigrationTask;
import com.hazelcast.crdt.CRDTReplicationAwareService;
import com.hazelcast.crdt.CRDTReplicationContainer;
import com.hazelcast.crdt.CRDTReplicationTask;
import com.hazelcast.crdt.ReplicatedVectorClocks;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.GracefulShutdownAwareService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CRDTReplicationMigrationService
implements ManagedService,
MembershipAwareService,
GracefulShutdownAwareService {
    public static final String SERVICE_NAME = "hz:impl:CRDTReplicationMigrationService";
    public static final String CRDT_REPLICATION_MIGRATION_EXECUTOR = "hz:CRDTReplicationMigration";
    private ScheduledFuture<?> replicationTask;
    private NodeEngine nodeEngine;
    private ILogger logger;
    private ReplicatedVectorClocks replicationVectorClocks;

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        CRDTReplicationConfig replicationConfig = nodeEngine.getConfig().getCRDTReplicationConfig();
        int replicationPeriod = replicationConfig != null ? replicationConfig.getReplicationPeriodMillis() : 1000;
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.replicationVectorClocks = new ReplicatedVectorClocks();
        int maxTargets = replicationConfig != null ? replicationConfig.getMaxConcurrentReplicationTargets() : 1;
        this.replicationTask = nodeEngine.getExecutionService().scheduleWithRepetition(CRDT_REPLICATION_MIGRATION_EXECUTOR, new CRDTReplicationTask(nodeEngine, maxTargets, this), replicationPeriod, replicationPeriod, TimeUnit.MILLISECONDS);
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
        ScheduledFuture<?> task = this.replicationTask;
        if (task != null) {
            this.replicationTask = null;
            task.cancel(terminate);
        }
    }

    @Override
    public boolean onShutdown(long timeout, TimeUnit unit) {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            return true;
        }
        long timeoutNanos = unit.toNanos(timeout);
        for (CRDTReplicationAwareService service : this.getReplicationServices()) {
            service.prepareToSafeShutdown();
            CRDTReplicationContainer replicationOperation = service.prepareReplicationOperation(this.replicationVectorClocks.getLatestReplicatedVectorClock(service.getName()), 0);
            if (replicationOperation == null) {
                this.logger.fine("Skipping replication since all CRDTs are replicated");
                continue;
            }
            long start = System.nanoTime();
            if (!this.tryProcessOnOtherMembers(replicationOperation.getOperation(), service.getName(), timeoutNanos)) {
                this.logger.warning("Failed replication of CRDTs for " + service.getName() + ". CRDT state may be lost.");
            }
            if ((timeoutNanos -= System.nanoTime() - start) >= 0L) continue;
            return false;
        }
        return true;
    }

    private boolean tryProcessOnOtherMembers(Operation operation, String serviceName, long timeoutNanos) {
        OperationService operationService = this.nodeEngine.getOperationService();
        Collection<Member> targets = this.nodeEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        Member localMember = this.nodeEngine.getLocalMember();
        for (Member target : targets) {
            if (target.equals(localMember)) continue;
            long start = System.nanoTime();
            try {
                this.logger.fine("Replicating " + serviceName + " to " + target);
                InternalCompletableFuture future = operationService.createInvocationBuilder(null, operation, target.getAddress()).setTryCount(1).invoke();
                future.get(timeoutNanos, TimeUnit.NANOSECONDS);
                return true;
            }
            catch (Exception e) {
                this.logger.fine("Failed replication of " + serviceName + " for target " + target, e);
                if ((timeoutNanos -= System.nanoTime() - start) >= 0L) continue;
                break;
            }
        }
        return false;
    }

    Collection<CRDTReplicationAwareService> getReplicationServices() {
        return this.nodeEngine.getServices(CRDTReplicationAwareService.class);
    }

    Map<String, VectorClock> getReplicatedVectorClocks(String serviceName, String memberUUID) {
        return this.replicationVectorClocks.getReplicatedVectorClock(serviceName, memberUUID);
    }

    void setReplicatedVectorClocks(String serviceName, String memberUUID, Map<String, VectorClock> vectorClocks) {
        this.replicationVectorClocks.setReplicatedVectorClocks(serviceName, memberUUID, vectorClocks);
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
        this.scheduleMigrationTask(0L);
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        this.scheduleMigrationTask(0L);
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    void scheduleMigrationTask(long delaySeconds) {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            return;
        }
        this.nodeEngine.getExecutionService().schedule(CRDT_REPLICATION_MIGRATION_EXECUTOR, new CRDTMigrationTask(this.nodeEngine, this), delaySeconds, TimeUnit.SECONDS);
    }

    public String toString() {
        return "CRDTReplicationMigrationService{}";
    }
}

