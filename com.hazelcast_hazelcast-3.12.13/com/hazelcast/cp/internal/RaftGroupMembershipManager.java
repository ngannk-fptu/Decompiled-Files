/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.CPGroupInfo;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.MembershipChangeSchedule;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raft.exception.MismatchingGroupMembersCommitIndexException;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raftop.metadata.CompleteDestroyRaftGroupsOp;
import com.hazelcast.cp.internal.raftop.metadata.CompleteRaftGroupMembershipChangesOp;
import com.hazelcast.cp.internal.raftop.metadata.DestroyRaftNodesOp;
import com.hazelcast.cp.internal.raftop.metadata.GetDestroyingRaftGroupIdsOp;
import com.hazelcast.cp.internal.raftop.metadata.GetMembershipChangeScheduleOp;
import com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.internal.util.SimpleCompletedFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class RaftGroupMembershipManager {
    static final long MANAGEMENT_TASK_PERIOD_IN_MILLIS = TimeUnit.SECONDS.toMillis(1L);
    private static final long CHECK_LOCAL_RAFT_NODES_TASK_PERIOD_IN_MILLIS = TimeUnit.SECONDS.toMillis(10L);
    private final NodeEngine nodeEngine;
    private final RaftService raftService;
    private final ILogger logger;
    private volatile RaftInvocationManager invocationManager;

    RaftGroupMembershipManager(NodeEngine nodeEngine, RaftService raftService) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.raftService = raftService;
    }

    void init() {
        if (this.raftService.getLocalCPMember() == null) {
            return;
        }
        this.invocationManager = this.raftService.getInvocationManager();
        ExecutionService executionService = this.nodeEngine.getExecutionService();
        executionService.scheduleWithRepetition(new RaftGroupDestroyHandlerTask(), MANAGEMENT_TASK_PERIOD_IN_MILLIS, MANAGEMENT_TASK_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS);
        executionService.scheduleWithRepetition(new RaftGroupMembershipChangeHandlerTask(), MANAGEMENT_TASK_PERIOD_IN_MILLIS, MANAGEMENT_TASK_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS);
        executionService.scheduleWithRepetition(new CheckLocalRaftNodesTask(), CHECK_LOCAL_RAFT_NODES_TASK_PERIOD_IN_MILLIS, CHECK_LOCAL_RAFT_NODES_TASK_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    private boolean skipRunningTask() {
        return !this.raftService.getMetadataGroupManager().isMetadataGroupLeader();
    }

    private <T> InternalCompletableFuture<T> queryMetadata(RaftOp op) {
        return this.invocationManager.query(this.raftService.getMetadataGroupId(), op, QueryPolicy.LEADER_LOCAL);
    }

    private class RaftGroupMembershipChangeHandlerTask
    implements Runnable {
        private static final int NA_MEMBERS_COMMIT_INDEX = -1;

        private RaftGroupMembershipChangeHandlerTask() {
        }

        @Override
        public void run() {
            if (RaftGroupMembershipManager.this.skipRunningTask()) {
                return;
            }
            MembershipChangeSchedule schedule = this.getMembershipChangeSchedule();
            if (schedule == null) {
                return;
            }
            if (RaftGroupMembershipManager.this.logger.isFineEnabled()) {
                RaftGroupMembershipManager.this.logger.fine("Handling " + schedule);
            }
            List<MembershipChangeSchedule.CPGroupMembershipChange> changes = schedule.getChanges();
            CountDownLatch latch = new CountDownLatch(changes.size());
            ConcurrentHashMap<CPGroupId, Tuple2<Long, Long>> changedGroups = new ConcurrentHashMap<CPGroupId, Tuple2<Long, Long>>();
            for (MembershipChangeSchedule.CPGroupMembershipChange change : changes) {
                this.applyOnRaftGroup(latch, changedGroups, change);
            }
            try {
                latch.await();
                this.completeMembershipChanges(changedGroups);
            }
            catch (InterruptedException e) {
                RaftGroupMembershipManager.this.logger.warning("Membership changes interrupted while executing " + schedule + ". completed: " + changedGroups, e);
                Thread.currentThread().interrupt();
            }
        }

        private MembershipChangeSchedule getMembershipChangeSchedule() {
            InternalCompletableFuture f = RaftGroupMembershipManager.this.queryMetadata(new GetMembershipChangeScheduleOp());
            return (MembershipChangeSchedule)f.join();
        }

        private void applyOnRaftGroup(final CountDownLatch latch, final Map<CPGroupId, Tuple2<Long, Long>> changedGroups, final MembershipChangeSchedule.CPGroupMembershipChange change) {
            InternalCompletableFuture<Long> future = change.getMemberToRemove() != null ? RaftGroupMembershipManager.this.invocationManager.changeMembership(change.getGroupId(), change.getMembersCommitIndex(), change.getMemberToRemove(), MembershipChangeMode.REMOVE) : new SimpleCompletedFuture<Long>(change.getMembersCommitIndex());
            future.andThen(new ExecutionCallback<Long>(){

                @Override
                public void onResponse(Long removeCommitIndex) {
                    if (change.getMemberToAdd() != null) {
                        RaftGroupMembershipChangeHandlerTask.this.addMember(latch, changedGroups, change, removeCommitIndex);
                    } else {
                        changedGroups.put(change.getGroupId(), Tuple2.of(change.getMembersCommitIndex(), removeCommitIndex));
                        latch.countDown();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    long removeCommitIndex = RaftGroupMembershipChangeHandlerTask.this.checkMemberRemoveCommitIndex(changedGroups, change, t);
                    if (removeCommitIndex != -1L) {
                        this.onResponse(removeCommitIndex);
                    } else {
                        latch.countDown();
                    }
                }
            });
        }

        private void addMember(final CountDownLatch latch, final Map<CPGroupId, Tuple2<Long, Long>> changedGroups, final MembershipChangeSchedule.CPGroupMembershipChange change, long currentCommitIndex) {
            InternalCompletableFuture future = RaftGroupMembershipManager.this.invocationManager.changeMembership(change.getGroupId(), currentCommitIndex, change.getMemberToAdd(), MembershipChangeMode.ADD);
            future.andThen(new ExecutionCallback<Long>(){

                @Override
                public void onResponse(Long addCommitIndex) {
                    changedGroups.put(change.getGroupId(), Tuple2.of(change.getMembersCommitIndex(), addCommitIndex));
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable t) {
                    RaftGroupMembershipChangeHandlerTask.this.checkMemberAddCommitIndex(changedGroups, change, t);
                    latch.countDown();
                }
            });
        }

        private void checkMemberAddCommitIndex(Map<CPGroupId, Tuple2<Long, Long>> changedGroups, MembershipChangeSchedule.CPGroupMembershipChange change, Throwable t) {
            CPMemberInfo memberToAdd = change.getMemberToAdd();
            if (t instanceof MismatchingGroupMembersCommitIndexException) {
                MismatchingGroupMembersCommitIndexException m = (MismatchingGroupMembersCommitIndexException)t;
                String msg = "MEMBER ADD commit of " + change + " failed. Actual group members: " + m.getMembers() + " with commit index: " + m.getCommitIndex();
                if (!m.getMembers().contains(memberToAdd)) {
                    RaftGroupMembershipManager.this.logger.severe(msg);
                    return;
                }
                if (change.getMemberToRemove() != null) {
                    if (m.getMembers().contains(change.getMemberToRemove())) {
                        RaftGroupMembershipManager.this.logger.severe(msg);
                        return;
                    }
                    if (m.getMembers().size() != change.getMembers().size()) {
                        RaftGroupMembershipManager.this.logger.severe(msg);
                        return;
                    }
                } else if (m.getMembers().size() != change.getMembers().size() + 1) {
                    RaftGroupMembershipManager.this.logger.severe(msg);
                    return;
                }
                for (CPMemberInfo member : change.getMembers()) {
                    if (member.equals(change.getMemberToRemove()) || m.getMembers().contains(member)) continue;
                    RaftGroupMembershipManager.this.logger.severe(msg);
                    return;
                }
                changedGroups.put(change.getGroupId(), Tuple2.of(change.getMembersCommitIndex(), m.getCommitIndex()));
                return;
            }
            RaftGroupMembershipManager.this.logger.severe("Cannot get MEMBER ADD result of " + memberToAdd + " to " + change.getGroupId() + " with members commit index: " + change.getMembersCommitIndex(), t);
        }

        private long checkMemberRemoveCommitIndex(Map<CPGroupId, Tuple2<Long, Long>> changedGroups, MembershipChangeSchedule.CPGroupMembershipChange change, Throwable t) {
            CPMemberInfo removedMember = change.getMemberToRemove();
            if (t instanceof MismatchingGroupMembersCommitIndexException) {
                MismatchingGroupMembersCommitIndexException m = (MismatchingGroupMembersCommitIndexException)t;
                String msg = "MEMBER REMOVE commit of " + change + " failed. Actual group members: " + m.getMembers() + " with commit index: " + m.getCommitIndex();
                if (m.getMembers().contains(removedMember)) {
                    RaftGroupMembershipManager.this.logger.severe(msg);
                    return -1L;
                }
                if (change.getMemberToAdd() != null && m.getMembers().contains(change.getMemberToAdd())) {
                    if (m.getMembers().size() != change.getMembers().size()) {
                        RaftGroupMembershipManager.this.logger.severe(msg);
                        return -1L;
                    }
                    for (CPMemberInfo member : change.getMembers()) {
                        if (member.equals(removedMember) || m.getMembers().contains(member)) continue;
                        RaftGroupMembershipManager.this.logger.severe(msg);
                        return -1L;
                    }
                    changedGroups.put(change.getGroupId(), Tuple2.of(change.getMembersCommitIndex(), m.getCommitIndex()));
                    return -1L;
                }
                if (m.getMembers().size() != change.getMembers().size() - 1) {
                    RaftGroupMembershipManager.this.logger.severe(msg);
                    return -1L;
                }
                for (CPMemberInfo member : change.getMembers()) {
                    if (member.equals(removedMember) || m.getMembers().contains(member)) continue;
                    RaftGroupMembershipManager.this.logger.severe(msg);
                    return -1L;
                }
                return m.getCommitIndex();
            }
            RaftGroupMembershipManager.this.logger.severe("Cannot get MEMBER REMOVE result of " + removedMember + " to " + change.getGroupId(), t);
            return -1L;
        }

        private void completeMembershipChanges(Map<CPGroupId, Tuple2<Long, Long>> changedGroups) {
            CompleteRaftGroupMembershipChangesOp op = new CompleteRaftGroupMembershipChangesOp(changedGroups);
            RaftGroupId metadataGroupId = RaftGroupMembershipManager.this.raftService.getMetadataGroupId();
            InternalCompletableFuture future = RaftGroupMembershipManager.this.invocationManager.invoke(metadataGroupId, op);
            try {
                future.get();
            }
            catch (Exception e) {
                RaftGroupMembershipManager.this.logger.severe("Cannot commit CP group membership changes: " + changedGroups, e);
            }
        }
    }

    private class RaftGroupDestroyHandlerTask
    implements Runnable {
        private RaftGroupDestroyHandlerTask() {
        }

        @Override
        public void run() {
            if (RaftGroupMembershipManager.this.skipRunningTask()) {
                return;
            }
            Set<CPGroupId> destroyedGroupIds = this.destroyRaftGroups();
            if (destroyedGroupIds.isEmpty()) {
                return;
            }
            if (!this.commitDestroyedRaftGroups(destroyedGroupIds)) {
                return;
            }
            for (CPGroupId groupId : destroyedGroupIds) {
                RaftGroupMembershipManager.this.raftService.destroyRaftNode(groupId);
            }
            OperationService operationService = RaftGroupMembershipManager.this.nodeEngine.getOperationService();
            for (CPMemberInfo member : RaftGroupMembershipManager.this.raftService.getMetadataGroupManager().getActiveMembers()) {
                if (member.equals(RaftGroupMembershipManager.this.raftService.getLocalCPMember())) continue;
                operationService.send(new DestroyRaftNodesOp(destroyedGroupIds), member.getAddress());
            }
        }

        private Set<CPGroupId> destroyRaftGroups() {
            Collection<CPGroupId> destroyingRaftGroupIds = this.getDestroyingRaftGroupIds();
            if (destroyingRaftGroupIds.isEmpty()) {
                return Collections.emptySet();
            }
            HashMap<CPGroupId, InternalCompletableFuture<Object>> futures = new HashMap<CPGroupId, InternalCompletableFuture<Object>>();
            for (CPGroupId groupId : destroyingRaftGroupIds) {
                InternalCompletableFuture<Object> future = RaftGroupMembershipManager.this.invocationManager.destroy(groupId);
                futures.put(groupId, future);
            }
            HashSet<CPGroupId> destroyedGroupIds = new HashSet<CPGroupId>();
            for (Map.Entry e : futures.entrySet()) {
                if (!this.isRaftGroupDestroyed((CPGroupId)e.getKey(), (Future)e.getValue())) continue;
                destroyedGroupIds.add((CPGroupId)e.getKey());
            }
            return destroyedGroupIds;
        }

        private Collection<CPGroupId> getDestroyingRaftGroupIds() {
            InternalCompletableFuture f = RaftGroupMembershipManager.this.queryMetadata(new GetDestroyingRaftGroupIdsOp());
            return (Collection)f.join();
        }

        private boolean isRaftGroupDestroyed(CPGroupId groupId, Future<Object> future) {
            try {
                future.get();
                return true;
            }
            catch (InterruptedException e) {
                RaftGroupMembershipManager.this.logger.severe("Cannot get result of DESTROY commit to " + groupId, e);
                return false;
            }
            catch (ExecutionException e) {
                if (ExceptionUtil.peel(e) instanceof CPGroupDestroyedException) {
                    return true;
                }
                RaftGroupMembershipManager.this.logger.severe("Cannot get result of DESTROY commit to " + groupId, e);
                return false;
            }
        }

        private boolean commitDestroyedRaftGroups(Set<CPGroupId> destroyedGroupIds) {
            CompleteDestroyRaftGroupsOp op = new CompleteDestroyRaftGroupsOp(destroyedGroupIds);
            RaftGroupId metadataGroupId = RaftGroupMembershipManager.this.raftService.getMetadataGroupId();
            InternalCompletableFuture f = RaftGroupMembershipManager.this.invocationManager.invoke(metadataGroupId, op);
            try {
                f.get();
                RaftGroupMembershipManager.this.logger.info("Terminated CP groups: " + destroyedGroupIds + " are committed.");
                return true;
            }
            catch (Exception e) {
                RaftGroupMembershipManager.this.logger.severe("Cannot commit terminated CP groups: " + destroyedGroupIds, e);
                return false;
            }
        }
    }

    private class CheckLocalRaftNodesTask
    implements Runnable {
        private CheckLocalRaftNodesTask() {
        }

        @Override
        public void run() {
            for (RaftNode raftNode : RaftGroupMembershipManager.this.raftService.getAllRaftNodes()) {
                final CPGroupId groupId = raftNode.getGroupId();
                if (groupId.equals(RaftGroupMembershipManager.this.raftService.getMetadataGroupId())) continue;
                if (raftNode.getStatus() == RaftNodeStatus.TERMINATED) {
                    RaftGroupMembershipManager.this.raftService.destroyRaftNode(groupId);
                    continue;
                }
                if (raftNode.getStatus() == RaftNodeStatus.STEPPED_DOWN) {
                    RaftGroupMembershipManager.this.raftService.stepDownRaftNode(groupId);
                    continue;
                }
                InternalCompletableFuture f = RaftGroupMembershipManager.this.queryMetadata(new GetRaftGroupOp(groupId));
                f.andThen(new ExecutionCallback<CPGroupInfo>(){

                    @Override
                    public void onResponse(CPGroupInfo group) {
                        if (group == null) {
                            RaftGroupMembershipManager.this.logger.severe("Could not find CP group for local raft node of " + groupId);
                        } else if (group.status() == CPGroup.CPGroupStatus.DESTROYED) {
                            RaftGroupMembershipManager.this.raftService.destroyRaftNode(groupId);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        RaftGroupMembershipManager.this.logger.warning("Could not get CP group info of " + groupId, t);
                    }
                });
            }
        }
    }
}

