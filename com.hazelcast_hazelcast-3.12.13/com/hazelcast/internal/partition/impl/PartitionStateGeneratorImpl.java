/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.core.Member;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionStateGenerator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.SingleMemberGroup;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

final class PartitionStateGeneratorImpl
implements PartitionStateGenerator {
    private static final ILogger LOGGER = Logger.getLogger(PartitionStateGenerator.class);
    private static final int DEFAULT_RETRY_MULTIPLIER = 10;
    private static final float RANGE_CHECK_RATIO = 1.1f;
    private static final int MAX_RETRY_COUNT = 3;
    private static final int AGGRESSIVE_RETRY_THRESHOLD = 1;
    private static final int AGGRESSIVE_INDEX_THRESHOLD = 3;
    private static final int MIN_AVG_OWNER_DIFF = 3;

    PartitionStateGeneratorImpl() {
    }

    @Override
    public PartitionReplica[][] arrange(Collection<MemberGroup> memberGroups, InternalPartition[] currentState) {
        return this.arrange(memberGroups, currentState, null);
    }

    @Override
    public PartitionReplica[][] arrange(Collection<MemberGroup> memberGroups, InternalPartition[] currentState, Collection<Integer> partitions) {
        Queue<NodeGroup> groups = this.createNodeGroups(memberGroups);
        if (groups.isEmpty()) {
            return null;
        }
        int partitionCount = currentState.length;
        PartitionReplica[][] state = new PartitionReplica[partitionCount][7];
        this.initialize(currentState, state, partitions);
        int tryCount = 0;
        do {
            boolean aggressive = tryCount >= 1;
            this.tryArrange(state, groups, partitionCount, aggressive, partitions);
            if (tryCount++ <= 0 || !LOGGER.isFineEnabled()) continue;
            LOGGER.fine("Re-trying partition arrangement. Count: " + tryCount);
        } while (tryCount < 3 && !this.areGroupsBalanced(groups, partitionCount));
        return state;
    }

    private void initialize(InternalPartition[] currentState, PartitionReplica[][] state, Collection<Integer> partitions) {
        int partitionCount = currentState.length;
        for (int partitionId = 0; partitionId < partitionCount; ++partitionId) {
            InternalPartition p = currentState[partitionId];
            PartitionReplica[] replicas = state[partitionId];
            boolean empty = true;
            for (int index = 0; index < 7; ++index) {
                replicas[index] = p.getReplica(index);
                empty &= replicas[index] == null;
            }
            if (empty || partitions != null && !partitions.contains(partitionId)) continue;
            int maxReplicaIndex = 6;
            block2: for (int index = 0; index < 7; ++index) {
                if (replicas[index] != null) continue;
                for (int k = maxReplicaIndex; k > index; --k) {
                    if (replicas[k] == null) continue;
                    replicas[index] = replicas[k];
                    replicas[k] = null;
                    maxReplicaIndex = k - 1;
                    continue block2;
                }
            }
        }
    }

    private void tryArrange(PartitionReplica[][] state, Queue<NodeGroup> groups, int partitionCount, boolean aggressive, Collection<Integer> toBeArrangedPartitions) {
        int groupSize = groups.size();
        int replicaCount = Math.min(groupSize, 7);
        int avgPartitionPerGroup = partitionCount / groupSize;
        this.initializeGroupPartitions(state, groups, replicaCount, aggressive, toBeArrangedPartitions);
        for (int index = 0; index < replicaCount; ++index) {
            Queue<Integer> freePartitions = this.getUnownedPartitions(state, index);
            if (toBeArrangedPartitions != null) {
                freePartitions.retainAll(toBeArrangedPartitions);
            }
            LinkedList<NodeGroup> underLoadedGroups = new LinkedList<NodeGroup>();
            LinkedList<NodeGroup> overLoadedGroups = new LinkedList<NodeGroup>();
            int plusOneGroupCount = partitionCount - avgPartitionPerGroup * groupSize;
            for (NodeGroup nodeGroup : groups) {
                int size = nodeGroup.getPartitionCount(index);
                if (size < avgPartitionPerGroup) {
                    underLoadedGroups.add(nodeGroup);
                    continue;
                }
                if (size <= avgPartitionPerGroup) continue;
                overLoadedGroups.add(nodeGroup);
            }
            plusOneGroupCount = this.tryToDistributeUnownedPartitions(underLoadedGroups, freePartitions, avgPartitionPerGroup, index, plusOneGroupCount);
            if (!freePartitions.isEmpty()) {
                this.distributeUnownedPartitions(groups, freePartitions, index);
            }
            assert (freePartitions.isEmpty()) : "There are partitions not-owned yet: " + freePartitions;
            if (toBeArrangedPartitions == null) {
                this.transferPartitionsBetweenGroups(underLoadedGroups, overLoadedGroups, index, avgPartitionPerGroup, plusOneGroupCount);
            }
            this.updatePartitionState(state, groups, index);
        }
    }

    private void transferPartitionsBetweenGroups(Queue<NodeGroup> underLoadedGroups, Collection<NodeGroup> overLoadedGroups, int index, int avgPartitionPerGroup, int plusOneGroupCount) {
        int expectedPartitionCount;
        int maxPartitionPerGroup = avgPartitionPerGroup + 1;
        int maxTries = underLoadedGroups.size() * overLoadedGroups.size() * 10;
        int tries = 0;
        int n = expectedPartitionCount = plusOneGroupCount > 0 ? maxPartitionPerGroup : avgPartitionPerGroup;
        while (tries++ < maxTries && !underLoadedGroups.isEmpty()) {
            NodeGroup toGroup = underLoadedGroups.poll();
            Iterator<NodeGroup> overLoadedGroupsIterator = overLoadedGroups.iterator();
            while (overLoadedGroupsIterator.hasNext()) {
                NodeGroup fromGroup = overLoadedGroupsIterator.next();
                this.selectToGroupPartitions(index, expectedPartitionCount, toGroup, fromGroup);
                int fromCount = fromGroup.getPartitionCount(index);
                if (plusOneGroupCount > 0 && fromCount == maxPartitionPerGroup && --plusOneGroupCount == 0) {
                    expectedPartitionCount = avgPartitionPerGroup;
                }
                if (fromCount <= expectedPartitionCount) {
                    overLoadedGroupsIterator.remove();
                }
                int toCount = toGroup.getPartitionCount(index);
                if (plusOneGroupCount > 0 && toCount == maxPartitionPerGroup && --plusOneGroupCount == 0) {
                    expectedPartitionCount = avgPartitionPerGroup;
                }
                if (toCount < expectedPartitionCount) continue;
                break;
            }
            if (toGroup.getPartitionCount(index) >= avgPartitionPerGroup) continue;
            underLoadedGroups.offer(toGroup);
        }
    }

    private void selectToGroupPartitions(int index, int expectedPartitionCount, NodeGroup toGroup, NodeGroup fromGroup) {
        Iterator<Integer> partitionsIterator = fromGroup.getPartitionsIterator(index);
        while (partitionsIterator.hasNext() && fromGroup.getPartitionCount(index) > expectedPartitionCount && toGroup.getPartitionCount(index) < expectedPartitionCount) {
            Integer partitionId = partitionsIterator.next();
            if (!toGroup.addPartition(index, partitionId)) continue;
            partitionsIterator.remove();
        }
    }

    private void updatePartitionState(PartitionReplica[][] state, Collection<NodeGroup> groups, int index) {
        for (NodeGroup group : groups) {
            group.postProcessPartitionTable(index);
            for (PartitionReplica replica : group.getReplicas()) {
                PartitionTable table = group.getPartitionTable(replica);
                Set<Integer> set = table.getPartitions(index);
                for (Integer partitionId : set) {
                    state[partitionId.intValue()][index] = replica;
                }
            }
        }
    }

    private void distributeUnownedPartitions(Queue<NodeGroup> groups, Queue<Integer> freePartitions, int index) {
        int groupSize = groups.size();
        int maxTries = freePartitions.size() * groupSize * 10;
        int tries = 0;
        Integer partitionId = freePartitions.poll();
        while (partitionId != null && tries++ < maxTries) {
            NodeGroup group = groups.poll();
            if (group.addPartition(index, partitionId)) {
                partitionId = freePartitions.poll();
            }
            groups.offer(group);
        }
    }

    private int tryToDistributeUnownedPartitions(Queue<NodeGroup> underLoadedGroups, Queue<Integer> freePartitions, int avgPartitionPerGroup, int index, int plusOneGroupCount) {
        int maxPartitionPerGroup = avgPartitionPerGroup + 1;
        int maxTries = freePartitions.size() * underLoadedGroups.size();
        int tries = 0;
        while (tries++ < maxTries && !freePartitions.isEmpty() && !underLoadedGroups.isEmpty()) {
            NodeGroup group = underLoadedGroups.poll();
            this.assignFreePartitionsToNodeGroup(freePartitions, index, group);
            int count = group.getPartitionCount(index);
            if (plusOneGroupCount > 0 && count == maxPartitionPerGroup) {
                if (--plusOneGroupCount != 0) continue;
                Iterator underLoaded = underLoadedGroups.iterator();
                while (underLoaded.hasNext()) {
                    if (((NodeGroup)underLoaded.next()).getPartitionCount(index) < avgPartitionPerGroup) continue;
                    underLoaded.remove();
                }
                continue;
            }
            if ((plusOneGroupCount <= 0 || count >= maxPartitionPerGroup) && count >= avgPartitionPerGroup) continue;
            underLoadedGroups.offer(group);
        }
        return plusOneGroupCount;
    }

    private void assignFreePartitionsToNodeGroup(Queue<Integer> freePartitions, int index, NodeGroup group) {
        Integer partitionId;
        int size = freePartitions.size();
        for (int i = 0; i < size && !group.addPartition(index, partitionId = freePartitions.poll()); ++i) {
            freePartitions.offer(partitionId);
        }
    }

    private Queue<Integer> getUnownedPartitions(PartitionReplica[][] state, int replicaIndex) {
        LinkedList<Integer> freePartitions = new LinkedList<Integer>();
        for (int partitionId = 0; partitionId < state.length; ++partitionId) {
            PartitionReplica[] replicas = state[partitionId];
            if (replicas[replicaIndex] != null) continue;
            freePartitions.add(partitionId);
        }
        Collections.shuffle(freePartitions);
        return freePartitions;
    }

    private void initializeGroupPartitions(PartitionReplica[][] state, Queue<NodeGroup> groups, int replicaCount, boolean aggressive, Collection<Integer> toBeArrangedPartitions) {
        for (NodeGroup nodeGroup : groups) {
            nodeGroup.resetPartitions();
        }
        for (int partitionId = 0; partitionId < state.length; ++partitionId) {
            PartitionReplica[] replicas = state[partitionId];
            for (int replicaIndex = 0; replicaIndex < 7; ++replicaIndex) {
                if (replicaIndex >= replicaCount) {
                    replicas[replicaIndex] = null;
                    continue;
                }
                PartitionReplica owner = replicas[replicaIndex];
                boolean valid = false;
                if (owner != null) {
                    valid = this.partitionOwnerAvailable(groups, partitionId, replicaIndex, owner);
                }
                if (!valid) {
                    replicas[replicaIndex] = null;
                    continue;
                }
                if (!aggressive || replicaIndex >= 3 || toBeArrangedPartitions != null && !toBeArrangedPartitions.contains(partitionId)) continue;
                for (int i = 3; i < replicaCount; ++i) {
                    replicas[i] = null;
                }
            }
        }
    }

    private boolean partitionOwnerAvailable(Queue<NodeGroup> groups, int partitionId, int replicaIndex, PartitionReplica owner) {
        for (NodeGroup nodeGroup : groups) {
            if (!nodeGroup.hasNode(owner)) continue;
            if (!nodeGroup.ownPartition(owner, replicaIndex, partitionId)) break;
            return true;
        }
        return false;
    }

    private Queue<NodeGroup> createNodeGroups(Collection<MemberGroup> memberGroups) {
        LinkedList<NodeGroup> nodeGroups = new LinkedList<NodeGroup>();
        if (memberGroups == null || memberGroups.isEmpty()) {
            return nodeGroups;
        }
        for (MemberGroup memberGroup : memberGroups) {
            NodeGroup nodeGroup;
            if (memberGroup.size() == 0) continue;
            if (memberGroup instanceof SingleMemberGroup || memberGroup.size() == 1) {
                nodeGroup = new SingleNodeGroup();
                Member next = memberGroup.iterator().next();
                nodeGroup.addNode(PartitionReplica.from(next));
            } else {
                nodeGroup = new DefaultNodeGroup();
                Iterator<Member> iter = memberGroup.iterator();
                while (iter.hasNext()) {
                    Member next = iter.next();
                    nodeGroup.addNode(PartitionReplica.from(next));
                }
            }
            nodeGroups.add(nodeGroup);
        }
        return nodeGroups;
    }

    private boolean areGroupsBalanced(Collection<NodeGroup> groups, int partitionCount) {
        float ratio = 1.1f;
        int avgPartitionPerGroup = partitionCount / groups.size();
        int replicaCount = Math.min(groups.size(), 7);
        for (NodeGroup group : groups) {
            for (int i = 0; i < replicaCount; ++i) {
                int partitionCountOfGroup = group.getPartitionCount(i);
                if (Math.abs(partitionCountOfGroup - avgPartitionPerGroup) <= 3 || !((float)partitionCountOfGroup < (float)avgPartitionPerGroup / ratio) && !((float)partitionCountOfGroup > (float)avgPartitionPerGroup * ratio)) continue;
                if (LOGGER.isFineEnabled()) {
                    LOGGER.fine("Not well balanced! Replica: " + i + ", PartitionCount: " + partitionCountOfGroup + ", AvgPartitionCount: " + avgPartitionPerGroup);
                }
                return false;
            }
        }
        return true;
    }

    private static class PartitionTable {
        final Set<Integer>[] partitions = new Set[7];

        private PartitionTable() {
        }

        Set<Integer> getPartitions(int index) {
            this.check(index);
            Set<Integer> set = this.partitions[index];
            if (set == null) {
                this.partitions[index] = set = new LinkedHashSet<Integer>();
            }
            return set;
        }

        boolean add(int index, Integer partitionId) {
            return this.getPartitions(index).add(partitionId);
        }

        boolean contains(int index, Integer partitionId) {
            return this.getPartitions(index).contains(partitionId);
        }

        boolean contains(Integer partitionId) {
            for (Set<Integer> set : this.partitions) {
                if (set == null || !set.contains(partitionId)) continue;
                return true;
            }
            return false;
        }

        boolean remove(int index, Integer partitionId) {
            return this.getPartitions(index).remove(partitionId);
        }

        int size(int index) {
            return this.getPartitions(index).size();
        }

        void reset() {
            for (Set<Integer> set : this.partitions) {
                if (set == null) continue;
                set.clear();
            }
        }

        private void check(int index) {
            if (index < 0 || index >= 7) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
        }
    }

    private static class SingleNodeGroup
    implements NodeGroup {
        final PartitionTable nodeTable = new PartitionTable();
        PartitionReplica replica;
        Set<PartitionReplica> replicas;

        private SingleNodeGroup() {
        }

        @Override
        public void addNode(PartitionReplica replica) {
            if (this.replica != null) {
                LOGGER.warning("Single node group already has an address => " + this.replica);
                return;
            }
            this.replica = replica;
            this.replicas = Collections.singleton(replica);
        }

        @Override
        public boolean hasNode(PartitionReplica replica) {
            return this.replica != null && this.replica.equals(replica);
        }

        @Override
        public Set<PartitionReplica> getReplicas() {
            return this.replicas;
        }

        @Override
        public PartitionTable getPartitionTable(PartitionReplica replica) {
            return this.hasNode(replica) ? this.nodeTable : null;
        }

        @Override
        public void resetPartitions() {
            this.nodeTable.reset();
        }

        @Override
        public int getPartitionCount(int index) {
            return this.nodeTable.size(index);
        }

        private boolean containsPartition(Integer partitionId) {
            return this.nodeTable.contains(partitionId);
        }

        @Override
        public boolean ownPartition(PartitionReplica replica, int index, Integer partitionId) {
            if (!this.hasNode(replica)) {
                String error = replica + " is different from this node's " + this.replica;
                LOGGER.warning(error);
                return false;
            }
            if (this.containsPartition(partitionId)) {
                if (LOGGER.isFinestEnabled()) {
                    LOGGER.finest("Partition[" + partitionId + "] is already owned by this node " + replica);
                }
                return false;
            }
            return this.nodeTable.add(index, partitionId);
        }

        @Override
        public boolean addPartition(int replicaIndex, Integer partitionId) {
            if (this.containsPartition(partitionId)) {
                return false;
            }
            return this.nodeTable.add(replicaIndex, partitionId);
        }

        @Override
        public Iterator<Integer> getPartitionsIterator(int index) {
            return this.nodeTable.getPartitions(index).iterator();
        }

        @Override
        public void postProcessPartitionTable(int index) {
        }

        public String toString() {
            return "SingleNodeGroupRegistry [address=" + this.replica + "]";
        }
    }

    private static class DefaultNodeGroup
    implements NodeGroup {
        final PartitionTable groupPartitionTable = new PartitionTable();
        final Map<PartitionReplica, PartitionTable> nodePartitionTables = new HashMap<PartitionReplica, PartitionTable>();
        final LinkedList<Integer> partitionQ = new LinkedList();

        private DefaultNodeGroup() {
        }

        @Override
        public void addNode(PartitionReplica replica) {
            this.nodePartitionTables.put(replica, new PartitionTable());
        }

        @Override
        public boolean hasNode(PartitionReplica replica) {
            return this.nodePartitionTables.containsKey(replica);
        }

        @Override
        public Set<PartitionReplica> getReplicas() {
            return this.nodePartitionTables.keySet();
        }

        @Override
        public PartitionTable getPartitionTable(PartitionReplica replica) {
            return this.nodePartitionTables.get(replica);
        }

        @Override
        public void resetPartitions() {
            this.groupPartitionTable.reset();
            this.partitionQ.clear();
            for (PartitionTable table : this.nodePartitionTables.values()) {
                table.reset();
            }
        }

        @Override
        public int getPartitionCount(int index) {
            return this.groupPartitionTable.size(index);
        }

        private boolean containsPartition(Integer partitionId) {
            return this.groupPartitionTable.contains(partitionId);
        }

        @Override
        public boolean ownPartition(PartitionReplica replica, int index, Integer partitionId) {
            if (!this.hasNode(replica)) {
                String error = "PartitionReplica does not belong to this group: " + replica.toString();
                LOGGER.warning(error);
                return false;
            }
            if (this.containsPartition(partitionId)) {
                if (LOGGER.isFinestEnabled()) {
                    LOGGER.finest("Partition[" + partitionId + "] is already owned by this group!");
                }
                return false;
            }
            this.groupPartitionTable.add(index, partitionId);
            return this.nodePartitionTables.get(replica).add(index, partitionId);
        }

        @Override
        public boolean addPartition(int replicaIndex, Integer partitionId) {
            if (this.containsPartition(partitionId)) {
                return false;
            }
            if (this.groupPartitionTable.add(replicaIndex, partitionId)) {
                this.partitionQ.add(partitionId);
                return true;
            }
            return false;
        }

        @Override
        public Iterator<Integer> getPartitionsIterator(final int index) {
            final Iterator<Integer> iterator = this.groupPartitionTable.getPartitions(index).iterator();
            return new Iterator<Integer>(){
                Integer current;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Integer next() {
                    this.current = (Integer)iterator.next();
                    return this.current;
                }

                @Override
                public void remove() {
                    iterator.remove();
                    this.doRemovePartition(index, this.current);
                }
            };
        }

        private void doRemovePartition(int index, Integer partitionId) {
            for (PartitionTable table : this.nodePartitionTables.values()) {
                if (table.remove(index, partitionId)) break;
            }
        }

        @Override
        public void postProcessPartitionTable(int index) {
            if (this.nodePartitionTables.size() == 1) {
                PartitionTable table = this.nodePartitionTables.values().iterator().next();
                while (!this.partitionQ.isEmpty()) {
                    table.add(index, this.partitionQ.poll());
                }
            } else {
                LinkedList<PartitionTable> underLoadedStates = new LinkedList<PartitionTable>();
                int avgCount = this.slimDownNodesToAvgPartitionTableSize(index, underLoadedStates);
                if (!this.partitionQ.isEmpty()) {
                    for (PartitionTable table : underLoadedStates) {
                        while (table.size(index) < avgCount) {
                            table.add(index, this.partitionQ.poll());
                        }
                    }
                }
                block3: while (!this.partitionQ.isEmpty()) {
                    for (PartitionTable table : this.nodePartitionTables.values()) {
                        table.add(index, this.partitionQ.poll());
                        if (!this.partitionQ.isEmpty()) continue;
                        continue block3;
                    }
                }
            }
        }

        private int slimDownNodesToAvgPartitionTableSize(int index, List<PartitionTable> underLoadedStates) {
            int totalCount = this.getPartitionCount(index);
            int avgCount = totalCount / this.nodePartitionTables.values().size();
            for (PartitionTable table : this.nodePartitionTables.values()) {
                Set<Integer> partitions = table.getPartitions(index);
                if (partitions.size() > avgCount) {
                    Integer[] partitionArray = partitions.toArray(new Integer[0]);
                    while (partitions.size() > avgCount) {
                        int partitionId = partitionArray[partitions.size() - 1];
                        partitions.remove(partitionId);
                        this.partitionQ.add(partitionId);
                    }
                    continue;
                }
                underLoadedStates.add(table);
            }
            return avgCount;
        }

        public String toString() {
            return "DefaultNodeGroupRegistry [nodes=" + this.nodePartitionTables.keySet() + "]";
        }
    }

    private static interface NodeGroup {
        public void addNode(PartitionReplica var1);

        public boolean hasNode(PartitionReplica var1);

        public Set<PartitionReplica> getReplicas();

        public PartitionTable getPartitionTable(PartitionReplica var1);

        public void resetPartitions();

        public int getPartitionCount(int var1);

        public boolean ownPartition(PartitionReplica var1, int var2, Integer var3);

        public boolean addPartition(int var1, Integer var2);

        public Iterator<Integer> getPartitionsIterator(int var1);

        public void postProcessPartitionTable(int var1);
    }
}

