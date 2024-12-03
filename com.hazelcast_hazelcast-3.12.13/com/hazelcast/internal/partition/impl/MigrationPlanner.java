/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MigrationPlanner {
    private static final boolean ASSERTION_ENABLED = MigrationPlanner.class.desiredAssertionStatus();
    private final ILogger logger;
    private final PartitionReplica[] state = new PartitionReplica[7];
    private final Set<PartitionReplica> verificationSet = ASSERTION_ENABLED ? new HashSet() : Collections.emptySet();

    MigrationPlanner() {
        this.logger = Logger.getLogger(this.getClass());
    }

    MigrationPlanner(ILogger logger) {
        this.logger = logger;
    }

    void planMigrations(int partitionId, PartitionReplica[] oldReplicas, PartitionReplica[] newReplicas, MigrationDecisionCallback callback) {
        assert (oldReplicas.length == newReplicas.length) : "Replica addresses with different lengths! Old: " + Arrays.toString(oldReplicas) + ", New: " + Arrays.toString(newReplicas);
        if (this.logger.isFinestEnabled()) {
            this.logger.finest(String.format("partitionId=%d, Initial state: %s", partitionId, Arrays.toString(oldReplicas)));
            this.logger.finest(String.format("partitionId=%d, Final state: %s", partitionId, Arrays.toString(newReplicas)));
        }
        this.initState(oldReplicas);
        this.assertNoDuplicate(partitionId, oldReplicas, newReplicas);
        if (this.fixCycle(oldReplicas, newReplicas) && this.logger.isFinestEnabled()) {
            this.logger.finest(String.format("partitionId=%d, Final state (after cycle fix): %s", partitionId, Arrays.toString(newReplicas)));
        }
        int currentIndex = 0;
        while (currentIndex < oldReplicas.length) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest(String.format("partitionId=%d, Current index: %d, state: %s", partitionId, currentIndex, Arrays.toString(this.state)));
            }
            this.assertNoDuplicate(partitionId, oldReplicas, newReplicas);
            if (newReplicas[currentIndex] == null) {
                if (this.state[currentIndex] != null) {
                    this.trace("partitionId=%d, New address is null at index: %d", partitionId, currentIndex);
                    callback.migrate(this.state[currentIndex], currentIndex, -1, null, -1, -1);
                    this.state[currentIndex] = null;
                }
                ++currentIndex;
                continue;
            }
            if (this.state[currentIndex] == null) {
                int i = InternalPartitionImpl.getReplicaIndex(this.state, newReplicas[currentIndex]);
                if (i == -1) {
                    this.trace("partitionId=%d, COPY %s to index: %d", partitionId, newReplicas[currentIndex], currentIndex);
                    callback.migrate(null, -1, -1, newReplicas[currentIndex], -1, currentIndex);
                    this.state[currentIndex] = newReplicas[currentIndex];
                    ++currentIndex;
                    continue;
                }
                if (i > currentIndex) {
                    this.trace("partitionId=%d, SHIFT UP-2 %s from old addresses index: %d to index: %d", partitionId, this.state[i], i, currentIndex);
                    callback.migrate(null, -1, -1, this.state[i], i, currentIndex);
                    this.state[currentIndex] = this.state[i];
                    this.state[i] = null;
                    continue;
                }
                throw new AssertionError((Object)("partitionId=" + partitionId + "Migration decision algorithm failed during SHIFT UP! INITIAL: " + Arrays.toString(oldReplicas) + ", CURRENT: " + Arrays.toString(this.state) + ", FINAL: " + Arrays.toString(newReplicas)));
            }
            if (newReplicas[currentIndex].equals(this.state[currentIndex])) {
                ++currentIndex;
                continue;
            }
            if (InternalPartitionImpl.getReplicaIndex(newReplicas, this.state[currentIndex]) == -1 && InternalPartitionImpl.getReplicaIndex(this.state, newReplicas[currentIndex]) == -1) {
                this.trace("partitionId=%d, MOVE %s to index: %d", partitionId, newReplicas[currentIndex], currentIndex);
                callback.migrate(this.state[currentIndex], currentIndex, -1, newReplicas[currentIndex], -1, currentIndex);
                this.state[currentIndex] = newReplicas[currentIndex];
                ++currentIndex;
                continue;
            }
            if (InternalPartitionImpl.getReplicaIndex(this.state, newReplicas[currentIndex]) == -1) {
                int newIndex = InternalPartitionImpl.getReplicaIndex(newReplicas, this.state[currentIndex]);
                assert (newIndex > currentIndex) : "partitionId=" + partitionId + ", Migration decision algorithm failed during SHIFT DOWN! INITIAL: " + Arrays.toString(oldReplicas) + ", CURRENT: " + Arrays.toString(this.state) + ", FINAL: " + Arrays.toString(newReplicas);
                if (this.state[newIndex] == null) {
                    this.trace("partitionId=%d, SHIFT DOWN %s to index: %d, COPY %s to index: %d", partitionId, this.state[currentIndex], newIndex, newReplicas[currentIndex], currentIndex);
                    callback.migrate(this.state[currentIndex], currentIndex, newIndex, newReplicas[currentIndex], -1, currentIndex);
                    this.state[newIndex] = this.state[currentIndex];
                } else {
                    this.trace("partitionId=%d, MOVE-3 %s to index: %d", partitionId, newReplicas[currentIndex], currentIndex);
                    callback.migrate(this.state[currentIndex], currentIndex, -1, newReplicas[currentIndex], -1, currentIndex);
                }
                this.state[currentIndex] = newReplicas[currentIndex];
                ++currentIndex;
                continue;
            }
            this.planMigrations(partitionId, oldReplicas, newReplicas, callback, currentIndex);
        }
        assert (Arrays.equals(this.state, newReplicas)) : "partitionId=" + partitionId + ", Migration decisions failed! INITIAL: " + Arrays.toString(oldReplicas) + " CURRENT: " + Arrays.toString(this.state) + ", FINAL: " + Arrays.toString(newReplicas);
    }

    private void planMigrations(int partitionId, PartitionReplica[] oldMembers, PartitionReplica[] newReplicas, MigrationDecisionCallback callback, int currentIndex) {
        while (true) {
            int targetIndex = InternalPartitionImpl.getReplicaIndex(this.state, newReplicas[currentIndex]);
            assert (targetIndex != -1) : "partitionId=" + partitionId + ", Migration algorithm failed during SHIFT UP! " + newReplicas[currentIndex] + " is not present in " + Arrays.toString(this.state) + ". INITIAL: " + Arrays.toString(oldMembers) + ", FINAL: " + Arrays.toString(newReplicas);
            if (newReplicas[targetIndex] == null) {
                if (this.state[currentIndex] == null) {
                    this.trace("partitionId=%d, SHIFT UP %s from old addresses index: %d to index: %d", partitionId, this.state[targetIndex], targetIndex, currentIndex);
                    callback.migrate(this.state[currentIndex], currentIndex, -1, this.state[targetIndex], targetIndex, currentIndex);
                    this.state[currentIndex] = this.state[targetIndex];
                } else {
                    int newIndex = InternalPartitionImpl.getReplicaIndex(newReplicas, this.state[currentIndex]);
                    if (newIndex == -1) {
                        this.trace("partitionId=%d, SHIFT UP %s from old addresses index: %d to index: %d with source: %s", partitionId, this.state[targetIndex], targetIndex, currentIndex, this.state[currentIndex]);
                        callback.migrate(this.state[currentIndex], currentIndex, -1, this.state[targetIndex], targetIndex, currentIndex);
                        this.state[currentIndex] = this.state[targetIndex];
                    } else if (this.state[newIndex] == null) {
                        this.trace("partitionId=%d, SHIFT UP %s from old addresses index: %d to index: %d and SHIFT DOWN %s to index: %d", partitionId, this.state[targetIndex], targetIndex, currentIndex, this.state[currentIndex], newIndex);
                        callback.migrate(this.state[currentIndex], currentIndex, newIndex, this.state[targetIndex], targetIndex, currentIndex);
                        this.state[newIndex] = this.state[currentIndex];
                        this.state[currentIndex] = this.state[targetIndex];
                    } else {
                        this.trace("partitionId=%d, SHIFT UP %s from old addresses index: %d to index: %d with source: %s will get another MOVE migration to index: %d", partitionId, this.state[targetIndex], targetIndex, currentIndex, this.state[currentIndex], newIndex);
                        callback.migrate(this.state[currentIndex], currentIndex, -1, this.state[targetIndex], targetIndex, currentIndex);
                        this.state[currentIndex] = this.state[targetIndex];
                    }
                }
                this.state[targetIndex] = null;
                break;
            }
            if (InternalPartitionImpl.getReplicaIndex(this.state, newReplicas[targetIndex]) == -1) {
                this.trace("partitionId=%d, MOVE-2 %s  to index: %d", partitionId, newReplicas[targetIndex], targetIndex);
                callback.migrate(this.state[targetIndex], targetIndex, -1, newReplicas[targetIndex], -1, targetIndex);
                this.state[targetIndex] = newReplicas[targetIndex];
                break;
            }
            currentIndex = targetIndex;
        }
    }

    void prioritizeCopiesAndShiftUps(List<MigrationInfo> migrations) {
        for (int i = 0; i < migrations.size(); ++i) {
            this.prioritize(migrations, i);
        }
        if (this.logger.isFinestEnabled()) {
            StringBuilder s = new StringBuilder("Migration order after prioritization: [");
            int ix = 0;
            for (MigrationInfo migration : migrations) {
                s.append("\n\t").append(ix++).append("- ").append(migration).append(",");
            }
            s.deleteCharAt(s.length() - 1);
            s.append("]");
            this.logger.finest(s.toString());
        }
    }

    private void prioritize(List<MigrationInfo> migrations, int i) {
        int k;
        MigrationInfo migration = migrations.get(i);
        this.trace("Trying to prioritize migration: %s", migration);
        if (migration.getSourceCurrentReplicaIndex() != -1) {
            this.trace("Skipping non-copy migration: %s", migration);
            return;
        }
        for (k = i - 1; k >= 0; --k) {
            MigrationInfo other = migrations.get(k);
            if (other.getSourceCurrentReplicaIndex() == -1) {
                this.trace("Cannot prioritize against a copy / shift up. other: %s", other);
                break;
            }
            if (migration.getDestination().equals(other.getSource()) || migration.getDestination().equals(other.getDestination())) {
                this.trace("Cannot prioritize against a conflicting migration. other: %s", other);
                break;
            }
            if (other.getSourceNewReplicaIndex() == -1 || other.getSourceNewReplicaIndex() >= migration.getDestinationNewReplicaIndex()) continue;
            this.trace("Cannot prioritize against a hotter shift down. other: %s", other);
            break;
        }
        if (k + 1 != i) {
            this.trace("Prioritizing migration %s to: %d", migration, k + 1);
            migrations.remove(i);
            migrations.add(k + 1, migration);
        }
    }

    private void initState(PartitionReplica[] oldAddresses) {
        Arrays.fill(this.state, null);
        System.arraycopy(oldAddresses, 0, this.state, 0, oldAddresses.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void assertNoDuplicate(int partitionId, PartitionReplica[] oldReplicas, PartitionReplica[] newReplicas) {
        if (!ASSERTION_ENABLED) {
            return;
        }
        try {
            for (PartitionReplica replica : this.state) {
                if (replica != null) assert (this.verificationSet.add(replica)) : "partitionId=" + partitionId + ", Migration decision algorithm failed! DUPLICATE REPLICA ADDRESSES! INITIAL: " + Arrays.toString(oldReplicas) + ", CURRENT: " + Arrays.toString(this.state) + ", FINAL: " + Arrays.toString(newReplicas);
            }
        }
        finally {
            this.verificationSet.clear();
        }
    }

    boolean isCyclic(PartitionReplica[] oldReplicas, PartitionReplica[] newReplicas) {
        for (int i = 0; i < oldReplicas.length; ++i) {
            PartitionReplica oldAddress = oldReplicas[i];
            PartitionReplica newAddress = newReplicas[i];
            if (oldAddress == null || newAddress == null || oldAddress.equals(newAddress) || !this.isCyclic(oldReplicas, newReplicas, i)) continue;
            return true;
        }
        return false;
    }

    boolean fixCycle(PartitionReplica[] oldReplicas, PartitionReplica[] newReplicas) {
        boolean cyclic = false;
        for (int i = 0; i < oldReplicas.length; ++i) {
            PartitionReplica oldAddress = oldReplicas[i];
            PartitionReplica newAddress = newReplicas[i];
            if (oldAddress == null || newAddress == null || oldAddress.equals(newAddress) || !this.isCyclic(oldReplicas, newReplicas, i)) continue;
            this.fixCycle(oldReplicas, newReplicas, i);
            cyclic = true;
        }
        return cyclic;
    }

    private boolean isCyclic(PartitionReplica[] oldReplicas, PartitionReplica[] newReplicas, int index) {
        PartitionReplica newOwner = newReplicas[index];
        int firstIndex = index;
        int nextIndex;
        while ((nextIndex = InternalPartitionImpl.getReplicaIndex(newReplicas, oldReplicas[firstIndex])) != -1) {
            if (firstIndex == nextIndex) {
                return false;
            }
            if (newOwner.equals(oldReplicas[nextIndex])) {
                return true;
            }
            firstIndex = nextIndex;
        }
        return false;
    }

    private void fixCycle(PartitionReplica[] oldReplicas, PartitionReplica[] newReplicas, int index) {
        while (true) {
            int nextIndex = InternalPartitionImpl.getReplicaIndex(newReplicas, oldReplicas[index]);
            newReplicas[index] = oldReplicas[index];
            if (nextIndex == -1) {
                return;
            }
            index = nextIndex;
        }
    }

    private void trace(String log, Object ... args) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest(String.format(log, args));
        }
    }

    static interface MigrationDecisionCallback {
        public void migrate(PartitionReplica var1, int var2, int var3, PartitionReplica var4, int var5, int var6);
    }
}

