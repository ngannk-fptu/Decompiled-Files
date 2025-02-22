/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionListener;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionStateGenerator;
import com.hazelcast.internal.partition.PartitionTableView;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.NopPartitionListener;
import com.hazelcast.internal.partition.impl.PartitionStateGeneratorImpl;
import com.hazelcast.logging.ILogger;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.partition.membergroup.MemberGroupFactoryFactory;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PartitionStateManager {
    private final Node node;
    private final ILogger logger;
    private final InternalPartitionServiceImpl partitionService;
    private final int partitionCount;
    private final InternalPartitionImpl[] partitions;
    @Probe
    private final AtomicInteger stateVersion = new AtomicInteger();
    private final PartitionStateGenerator partitionStateGenerator;
    private final MemberGroupFactory memberGroupFactory;
    private volatile boolean initialized;
    @Probe
    private volatile int memberGroupsSize;

    public PartitionStateManager(Node node, InternalPartitionServiceImpl partitionService, PartitionListener listener) {
        this.node = node;
        this.logger = node.getLogger(this.getClass());
        this.partitionService = partitionService;
        this.partitionCount = partitionService.getPartitionCount();
        this.partitions = new InternalPartitionImpl[this.partitionCount];
        PartitionReplica localReplica = PartitionReplica.from(node.getLocalMember());
        for (int i = 0; i < this.partitionCount; ++i) {
            this.partitions[i] = new InternalPartitionImpl(i, listener, localReplica);
        }
        this.memberGroupFactory = MemberGroupFactoryFactory.newMemberGroupFactory(node.getConfig().getPartitionGroupConfig(), node.getDiscoveryService());
        this.partitionStateGenerator = new PartitionStateGeneratorImpl();
    }

    @Probe
    private int localPartitionCount() {
        int count = 0;
        for (InternalPartitionImpl partition : this.partitions) {
            if (!partition.isLocal()) continue;
            ++count;
        }
        return count;
    }

    private Collection<MemberGroup> createMemberGroups(final Set<Member> excludedMembers) {
        MemberSelector exclude = new MemberSelector(){

            @Override
            public boolean select(Member member) {
                return !excludedMembers.contains(member);
            }
        };
        MemberSelector selector = MemberSelectors.and(MemberSelectors.DATA_MEMBER_SELECTOR, exclude);
        Collection<Member> members = this.node.getClusterService().getMembers(selector);
        return this.memberGroupFactory.createMemberGroups(members);
    }

    private Collection<MemberGroup> createMemberGroups() {
        Collection<Member> members = this.node.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        return this.memberGroupFactory.createMemberGroups(members);
    }

    boolean initializePartitionAssignments(Set<Member> excludedMembers) {
        if (!this.isPartitionAssignmentAllowed()) {
            return false;
        }
        Collection<MemberGroup> memberGroups = this.createMemberGroups(excludedMembers);
        if (memberGroups.isEmpty()) {
            this.logger.warning("No member group is available to assign partition ownership...");
            return false;
        }
        this.logger.info("Initializing cluster partition table arrangement...");
        PartitionReplica[][] newState = this.partitionStateGenerator.arrange(memberGroups, this.partitions);
        if (newState.length != this.partitionCount) {
            throw new HazelcastException("Invalid partition count! Expected: " + this.partitionCount + ", Actual: " + newState.length);
        }
        this.stateVersion.incrementAndGet();
        ClusterState clusterState = this.node.getClusterService().getClusterState();
        if (!clusterState.isMigrationAllowed()) {
            this.stateVersion.decrementAndGet();
            this.logger.warning("Partitions can't be assigned since cluster-state= " + (Object)((Object)clusterState));
            return false;
        }
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            InternalPartitionImpl partition = this.partitions[partitionId];
            PartitionReplica[] replicas = newState[partitionId];
            partition.setReplicas(replicas);
        }
        this.setInitialized();
        return true;
    }

    private boolean isPartitionAssignmentAllowed() {
        if (!this.node.getNodeExtension().isStartCompleted()) {
            this.logger.warning("Partitions can't be assigned since startup is not completed yet.");
            return false;
        }
        ClusterState clusterState = this.node.getClusterService().getClusterState();
        if (!clusterState.isMigrationAllowed()) {
            this.logger.warning("Partitions can't be assigned since cluster-state= " + (Object)((Object)clusterState));
            return false;
        }
        if (this.partitionService.isFetchMostRecentPartitionTableTaskRequired()) {
            this.logger.warning("Partitions can't be assigned since most recent partition table is not decided yet.");
            return false;
        }
        return true;
    }

    void setInitialState(PartitionTableView partitionTable) {
        if (this.initialized) {
            throw new IllegalStateException("Partition table is already initialized!");
        }
        this.logger.info("Setting cluster partition table...");
        boolean foundReplica = false;
        PartitionReplica localReplica = PartitionReplica.from(this.node.getLocalMember());
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            InternalPartitionImpl partition = this.partitions[partitionId];
            PartitionReplica[] replicas = partitionTable.getReplicas(partitionId);
            if (!foundReplica && replicas != null) {
                for (int i = 0; i < 7; ++i) {
                    foundReplica |= replicas[i] != null;
                }
            }
            partition.reset(localReplica);
            partition.setInitialReplicas(replicas);
        }
        this.stateVersion.set(partitionTable.getVersion());
        if (foundReplica) {
            this.setInitialized();
        }
    }

    void updateMemberGroupsSize() {
        Collection<MemberGroup> groups = this.createMemberGroups();
        int size = 0;
        for (MemberGroup group : groups) {
            if (group.size() <= 0) continue;
            ++size;
        }
        this.memberGroupsSize = size;
    }

    int getMemberGroupsSize() {
        int size = this.memberGroupsSize;
        if (size > 0) {
            return size;
        }
        return this.node.isLiteMember() ? 0 : 1;
    }

    void removeUnknownMembers() {
        ClusterServiceImpl clusterService = this.node.getClusterService();
        for (InternalPartitionImpl partition : this.partitions) {
            for (int i = 0; i < 7; ++i) {
                PartitionReplica replica = partition.getReplica(i);
                if (replica == null || clusterService.getMember(replica.address(), replica.uuid()) != null) continue;
                partition.setReplica(i, null);
                if (!this.logger.isFinestEnabled()) continue;
                this.logger.finest("PartitionId=" + partition.getPartitionId() + " " + replica + " is removed from replica index: " + i + ", partition: " + partition);
            }
        }
    }

    boolean isAbsentInPartitionTable(Member member) {
        PartitionReplica replica = PartitionReplica.from(member);
        for (InternalPartitionImpl partition : this.partitions) {
            if (!partition.isOwnerOrBackup(replica)) continue;
            return false;
        }
        return true;
    }

    InternalPartition[] getPartitions() {
        return this.partitions;
    }

    public InternalPartition[] getPartitionsCopy() {
        NopPartitionListener listener = new NopPartitionListener();
        InternalPartition[] result = new InternalPartition[this.partitions.length];
        for (int i = 0; i < this.partitionCount; ++i) {
            result[i] = this.partitions[i].copy(listener);
        }
        return result;
    }

    public InternalPartitionImpl getPartitionImpl(int partitionId) {
        return this.partitions[partitionId];
    }

    PartitionReplica[][] repartition(Set<Member> excludedMembers, Collection<Integer> partitionInclusionSet) {
        if (!this.initialized) {
            return null;
        }
        Collection<MemberGroup> memberGroups = this.createMemberGroups(excludedMembers);
        PartitionReplica[][] newState = this.partitionStateGenerator.arrange(memberGroups, this.partitions, partitionInclusionSet);
        if (newState == null && this.logger.isFinestEnabled()) {
            this.logger.finest("Partition rearrangement failed. Number of member groups: " + memberGroups.size());
        }
        return newState;
    }

    public boolean trySetMigratingFlag(int partitionId) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Setting partition-migrating flag. partitionId=" + partitionId);
        }
        return this.partitions[partitionId].setMigrating();
    }

    public void clearMigratingFlag(int partitionId) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Clearing partition-migrating flag. partitionId=" + partitionId);
        }
        this.partitions[partitionId].resetMigrating();
    }

    public boolean isMigrating(int partitionId) {
        return this.partitions[partitionId].isMigrating();
    }

    void updateReplicas(int partitionId, PartitionReplica[] replicas) {
        InternalPartitionImpl partition = this.partitions[partitionId];
        partition.setReplicas(replicas);
    }

    void setVersion(int version) {
        this.stateVersion.set(version);
    }

    public int getVersion() {
        return this.stateVersion.get();
    }

    void incrementVersion(int delta) {
        assert (delta > 0) : "Delta: " + delta;
        this.stateVersion.addAndGet(delta);
    }

    void incrementVersion() {
        this.stateVersion.incrementAndGet();
    }

    boolean setInitialized() {
        if (!this.initialized) {
            this.initialized = true;
            this.node.getNodeExtension().onPartitionStateChange();
            return true;
        }
        return false;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    void reset() {
        this.initialized = false;
        this.stateVersion.set(0);
        PartitionReplica localReplica = PartitionReplica.from(this.node.getLocalMember());
        for (InternalPartitionImpl partition : this.partitions) {
            partition.reset(localReplica);
        }
    }

    int replaceMember(Member oldMember, Member newMember) {
        if (!this.initialized) {
            return 0;
        }
        PartitionReplica oldReplica = PartitionReplica.from(oldMember);
        PartitionReplica newReplica = PartitionReplica.from(newMember);
        int count = 0;
        for (InternalPartitionImpl partition : this.partitions) {
            if (partition.replaceReplica(oldReplica, newReplica) <= -1) continue;
            ++count;
        }
        if (count > 0) {
            this.node.getNodeExtension().onPartitionStateChange();
            this.logger.info("Replaced " + oldMember + " with " + newMember + " in partition table in " + count + " partitions.");
        }
        return count;
    }

    PartitionTableView getPartitionTable() {
        if (!this.initialized) {
            return new PartitionTableView(new PartitionReplica[this.partitions.length][7], 0);
        }
        return new PartitionTableView(this.partitions, this.stateVersion.get());
    }
}

