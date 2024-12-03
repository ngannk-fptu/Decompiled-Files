/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.crdt.CRDTReplicationAwareService;
import com.hazelcast.crdt.CRDTReplicationContainer;
import com.hazelcast.crdt.CRDTReplicationMigrationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class CRDTReplicationTask
implements Runnable {
    private final NodeEngine nodeEngine;
    private final int maxTargets;
    private final ILogger logger;
    private final CRDTReplicationMigrationService replicationMigrationService;
    private int lastTargetIndex;

    CRDTReplicationTask(NodeEngine nodeEngine, int maxTargets, CRDTReplicationMigrationService replicationMigrationService) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.maxTargets = maxTargets;
        this.replicationMigrationService = replicationMigrationService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            return;
        }
        try {
            List<Member> viableTargets = this.getNonLocalReplicaAddresses();
            if (viableTargets.size() == 0) {
                return;
            }
            Member[] targets = this.pickTargets(viableTargets, this.lastTargetIndex, this.maxTargets);
            this.lastTargetIndex = (this.lastTargetIndex + targets.length) % viableTargets.size();
            for (CRDTReplicationAwareService service : this.replicationMigrationService.getReplicationServices()) {
                for (Member target : targets) {
                    this.replicate(service, target);
                }
            }
        }
        finally {
            Thread.interrupted();
        }
    }

    private List<Member> getNonLocalReplicaAddresses() {
        Collection<Member> dataMembers = this.nodeEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        ArrayList<Member> nonLocalDataMembers = new ArrayList<Member>(dataMembers);
        nonLocalDataMembers.remove(this.nodeEngine.getLocalMember());
        return nonLocalDataMembers;
    }

    private void replicate(CRDTReplicationAwareService service, Member target) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        int targetIndex = this.getDataMemberListIndex(target);
        Map<String, VectorClock> lastSuccessfullyReplicatedClocks = this.replicationMigrationService.getReplicatedVectorClocks(service.getName(), target.getUuid());
        OperationService operationService = this.nodeEngine.getOperationService();
        CRDTReplicationContainer replicationOperation = service.prepareReplicationOperation(lastSuccessfullyReplicatedClocks, targetIndex);
        if (replicationOperation == null) {
            this.logger.finest("Skipping replication of " + service.getName() + " for target " + target);
            return;
        }
        try {
            this.logger.finest("Replicating " + service.getName() + " to " + target);
            operationService.invokeOnTarget(null, replicationOperation.getOperation(), target.getAddress()).join();
            this.replicationMigrationService.setReplicatedVectorClocks(service.getName(), target.getUuid(), replicationOperation.getVectorClocks());
        }
        catch (Exception e) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Failed replication of " + service.getName() + " for target " + target, e);
            }
            this.logger.info("Failed replication of " + service.getName() + " for target " + target);
        }
    }

    private int getDataMemberListIndex(Member member) {
        Collection<Member> dataMembers = this.nodeEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        int index = -1;
        for (Member dataMember : dataMembers) {
            ++index;
            if (!dataMember.equals(member)) continue;
            return index;
        }
        return index;
    }

    private Member[] pickTargets(Collection<Member> members, int startingIndex, int maxTargets) {
        Member[] viableTargetArray = members.toArray(new Member[0]);
        Member[] pickedTargets = new Member[Math.min(maxTargets, viableTargetArray.length)];
        for (int i = 0; i < pickedTargets.length; ++i) {
            startingIndex = (startingIndex + 1) % viableTargetArray.length;
            pickedTargets[i] = viableTargetArray[startingIndex];
        }
        return pickedTargets;
    }
}

