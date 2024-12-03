/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.crdt.CRDTReplicationAwareService;
import com.hazelcast.crdt.CRDTReplicationContainer;
import com.hazelcast.crdt.CRDTReplicationMigrationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import java.util.Collection;

class CRDTMigrationTask
implements Runnable {
    private static final int MIGRATION_RETRY_DELAY_SECONDS = 1;
    private final NodeEngine nodeEngine;
    private final ILogger logger;
    private final CRDTReplicationMigrationService replicationMigrationService;

    CRDTMigrationTask(NodeEngine nodeEngine, CRDTReplicationMigrationService replicationMigrationService) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.replicationMigrationService = replicationMigrationService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            if (this.nodeEngine.getLocalMember().isLiteMember()) {
                return;
            }
            Collection<Member> members = this.nodeEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
            Member firstDataMember = members.iterator().next();
            if (firstDataMember.equals(this.nodeEngine.getLocalMember())) {
                return;
            }
            int localReplicaIndex = this.getLocalMemberListIndex();
            boolean allMigrated = true;
            for (CRDTReplicationAwareService service : this.replicationMigrationService.getReplicationServices()) {
                allMigrated &= this.migrate(service, firstDataMember, localReplicaIndex + 1);
            }
            if (!allMigrated) {
                this.replicationMigrationService.scheduleMigrationTask(1L);
            }
        }
        finally {
            Thread.interrupted();
        }
    }

    private boolean migrate(CRDTReplicationAwareService service, Member target, int maxConfiguredReplicaCount) {
        if (Thread.currentThread().isInterrupted()) {
            return false;
        }
        OperationService operationService = this.nodeEngine.getOperationService();
        CRDTReplicationContainer migrationOperation = service.prepareMigrationOperation(maxConfiguredReplicaCount);
        if (migrationOperation == null) {
            this.logger.finest("Skipping migration of " + service.getName() + " for target " + target);
            return true;
        }
        try {
            this.logger.finest("Migrating " + service.getName() + " to " + target);
            operationService.invokeOnTarget(null, migrationOperation.getOperation(), target.getAddress()).join();
            boolean allMigrated = service.clearCRDTState(migrationOperation.getVectorClocks());
            if (!allMigrated) {
                this.logger.fine(service.getName() + " CRDTs have been mutated since migrated to target " + target + ". Rescheduling migration in " + 1 + " second(s).");
            }
            return allMigrated;
        }
        catch (Exception e) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Failed migration of " + service.getName() + " for target " + target + ". Rescheduling migration in " + 1 + " second(s).", e);
            } else {
                this.logger.info("Failed migration of " + service.getName() + " for target " + target + ". Rescheduling migration in " + 1 + " second(s).");
            }
            return false;
        }
    }

    private int getLocalMemberListIndex() {
        Collection<Member> dataMembers = this.nodeEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        int index = -1;
        for (Member dataMember : dataMembers) {
            ++index;
            if (!dataMember.equals(this.nodeEngine.getLocalMember())) continue;
            return index;
        }
        return index;
    }
}

