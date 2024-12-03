/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl;

import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.Endpoint;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.LeaderDemotedException;
import com.hazelcast.cp.exception.StaleAppendRequestException;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raft.command.DestroyRaftGroupCmd;
import com.hazelcast.cp.internal.raft.command.RaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.RaftIntegration;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.command.UpdateRaftGroupMembersCmd;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.cp.internal.raft.impl.handler.AppendFailureResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.handler.AppendRequestHandlerTask;
import com.hazelcast.cp.internal.raft.impl.handler.AppendSuccessResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.handler.InstallSnapshotHandlerTask;
import com.hazelcast.cp.internal.raft.impl.handler.PreVoteRequestHandlerTask;
import com.hazelcast.cp.internal.raft.impl.handler.PreVoteResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.handler.VoteRequestHandlerTask;
import com.hazelcast.cp.internal.raft.impl.handler.VoteResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.log.SnapshotEntry;
import com.hazelcast.cp.internal.raft.impl.state.FollowerState;
import com.hazelcast.cp.internal.raft.impl.state.LeaderState;
import com.hazelcast.cp.internal.raft.impl.state.RaftGroupMembers;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.MembershipChangeTask;
import com.hazelcast.cp.internal.raft.impl.task.PreVoteTask;
import com.hazelcast.cp.internal.raft.impl.task.QueryTask;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;
import com.hazelcast.cp.internal.raft.impl.task.ReplicateTask;
import com.hazelcast.cp.internal.raft.impl.util.PostponedResponse;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.RandomPicker;
import com.hazelcast.util.collection.Long2ObjectHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RaftNodeImpl
implements RaftNode {
    private static final int LEADER_ELECTION_TIMEOUT_RANGE = 1000;
    private static final long RAFT_NODE_INIT_DELAY_MILLIS = 500L;
    private static final float RATIO_TO_KEEP_LOGS_AFTER_SNAPSHOT = 0.1f;
    private final CPGroupId groupId;
    private final ILogger logger;
    private final RaftState state;
    private final RaftIntegration raftIntegration;
    private final Endpoint localMember;
    private final Long2ObjectHashMap<SimpleCompletableFuture> futures = new Long2ObjectHashMap();
    private final long heartbeatPeriodInMillis;
    private final int leaderElectionTimeout;
    private final int maxUncommittedEntryCount;
    private final int appendRequestMaxEntryCount;
    private final int commitIndexAdvanceCountToSnapshot;
    private final int maxMissedLeaderHeartbeatCount;
    private final long appendRequestBackoffTimeoutInMillis;
    private final int maxNumberOfLogsToKeepAfterSnapshot;
    private final Runnable appendRequestBackoffResetTask;
    private long lastAppendEntriesTimestamp;
    private boolean appendRequestBackoffResetTaskScheduled;
    private volatile RaftNodeStatus status = RaftNodeStatus.ACTIVE;

    public RaftNodeImpl(CPGroupId groupId, Endpoint localMember, Collection<Endpoint> members, RaftAlgorithmConfig raftAlgorithmConfig, RaftIntegration raftIntegration) {
        Preconditions.checkNotNull(groupId);
        Preconditions.checkNotNull(localMember);
        Preconditions.checkNotNull(members);
        this.groupId = groupId;
        this.raftIntegration = raftIntegration;
        this.localMember = localMember;
        this.maxUncommittedEntryCount = raftAlgorithmConfig.getUncommittedEntryCountToRejectNewAppends();
        this.appendRequestMaxEntryCount = raftAlgorithmConfig.getAppendRequestMaxEntryCount();
        this.commitIndexAdvanceCountToSnapshot = raftAlgorithmConfig.getCommitIndexAdvanceCountToSnapshot();
        this.leaderElectionTimeout = (int)raftAlgorithmConfig.getLeaderElectionTimeoutInMillis();
        this.heartbeatPeriodInMillis = raftAlgorithmConfig.getLeaderHeartbeatPeriodInMillis();
        this.maxMissedLeaderHeartbeatCount = raftAlgorithmConfig.getMaxMissedLeaderHeartbeatCount();
        this.maxNumberOfLogsToKeepAfterSnapshot = (int)((float)this.commitIndexAdvanceCountToSnapshot * 0.1f);
        this.appendRequestBackoffTimeoutInMillis = raftAlgorithmConfig.getAppendRequestBackoffTimeoutInMillis();
        int logCapacity = this.commitIndexAdvanceCountToSnapshot + this.maxUncommittedEntryCount + this.maxNumberOfLogsToKeepAfterSnapshot;
        this.state = new RaftState(groupId, localMember, members, logCapacity);
        this.logger = this.getLogger(RaftNode.class);
        this.appendRequestBackoffResetTask = new AppendRequestBackoffResetTask();
    }

    public ILogger getLogger(Class clazz) {
        String name = this.state.name();
        return this.raftIntegration.getLogger(clazz.getName() + "(" + name + ")");
    }

    @Override
    public CPGroupId getGroupId() {
        return this.groupId;
    }

    @Override
    public Endpoint getLocalMember() {
        return this.localMember;
    }

    @Override
    public Endpoint getLeader() {
        return this.state.leader();
    }

    @Override
    public RaftNodeStatus getStatus() {
        return this.status;
    }

    @Override
    public Collection<Endpoint> getInitialMembers() {
        return this.state.initialMembers();
    }

    @Override
    public Collection<Endpoint> getCommittedMembers() {
        return this.state.committedGroupMembers().members();
    }

    @Override
    public Collection<Endpoint> getAppliedMembers() {
        return this.state.lastGroupMembers().members();
    }

    @Override
    public void forceSetTerminatedStatus() {
        this.execute(new Runnable(){

            @Override
            public void run() {
                if (!RaftNodeImpl.this.isTerminatedOrSteppedDown()) {
                    RaftNodeImpl.this.setStatus(RaftNodeStatus.TERMINATED);
                    if (RaftNodeImpl.this.localMember.equals(RaftNodeImpl.this.state.leader())) {
                        RaftNodeImpl.this.invalidateFuturesFrom(RaftNodeImpl.this.state.commitIndex() + 1L);
                    }
                }
            }
        });
    }

    public void start() {
        if (!this.raftIntegration.isReady()) {
            this.raftIntegration.schedule(new Runnable(){

                @Override
                public void run() {
                    RaftNodeImpl.this.start();
                }
            }, 500L, TimeUnit.MILLISECONDS);
            return;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Starting raft node: " + this.localMember + " for " + this.groupId + " with " + this.state.memberCount() + " members: " + this.state.members());
        }
        this.raftIntegration.execute(new PreVoteTask(this, 0));
        this.scheduleLeaderFailureDetection();
    }

    @Override
    public void handlePreVoteRequest(PreVoteRequest request) {
        this.execute(new PreVoteRequestHandlerTask(this, request));
    }

    @Override
    public void handlePreVoteResponse(PreVoteResponse response) {
        this.execute(new PreVoteResponseHandlerTask(this, response));
    }

    @Override
    public void handleVoteRequest(VoteRequest request) {
        this.execute(new VoteRequestHandlerTask(this, request));
    }

    @Override
    public void handleVoteResponse(VoteResponse response) {
        this.execute(new VoteResponseHandlerTask(this, response));
    }

    @Override
    public void handleAppendRequest(AppendRequest request) {
        this.execute(new AppendRequestHandlerTask(this, request));
    }

    @Override
    public void handleAppendResponse(AppendSuccessResponse response) {
        this.execute(new AppendSuccessResponseHandlerTask(this, response));
    }

    @Override
    public void handleAppendResponse(AppendFailureResponse response) {
        this.execute(new AppendFailureResponseHandlerTask(this, response));
    }

    @Override
    public void handleInstallSnapshot(InstallSnapshot request) {
        this.execute(new InstallSnapshotHandlerTask(this, request));
    }

    @Override
    public ICompletableFuture replicate(Object operation) {
        SimpleCompletableFuture resultFuture = this.raftIntegration.newCompletableFuture();
        this.raftIntegration.execute(new ReplicateTask(this, operation, resultFuture));
        return resultFuture;
    }

    @Override
    public ICompletableFuture replicateMembershipChange(Endpoint member, MembershipChangeMode mode) {
        SimpleCompletableFuture resultFuture = this.raftIntegration.newCompletableFuture();
        this.raftIntegration.execute(new MembershipChangeTask(this, resultFuture, member, mode));
        return resultFuture;
    }

    @Override
    public ICompletableFuture replicateMembershipChange(Endpoint member, MembershipChangeMode mode, long groupMembersCommitIndex) {
        SimpleCompletableFuture resultFuture = this.raftIntegration.newCompletableFuture();
        this.raftIntegration.execute(new MembershipChangeTask(this, resultFuture, member, mode, groupMembersCommitIndex));
        return resultFuture;
    }

    @Override
    public ICompletableFuture query(Object operation, QueryPolicy queryPolicy) {
        SimpleCompletableFuture resultFuture = this.raftIntegration.newCompletableFuture();
        this.raftIntegration.execute(new QueryTask(this, operation, queryPolicy, resultFuture));
        return resultFuture;
    }

    @Override
    public boolean isTerminatedOrSteppedDown() {
        return this.status == RaftNodeStatus.TERMINATED || this.status == RaftNodeStatus.STEPPED_DOWN;
    }

    public void setStatus(RaftNodeStatus newStatus) {
        if (this.status == RaftNodeStatus.TERMINATED || this.status == RaftNodeStatus.STEPPED_DOWN) {
            throw new IllegalStateException("Cannot set status: " + (Object)((Object)newStatus) + " since already " + (Object)((Object)this.status));
        }
        RaftNodeStatus prevStatus = this.status;
        this.status = newStatus;
        if (prevStatus != newStatus) {
            if (newStatus == RaftNodeStatus.ACTIVE) {
                this.logger.info("Status is set to: " + (Object)((Object)newStatus));
            } else {
                this.logger.warning("Status is set to: " + (Object)((Object)newStatus));
            }
        }
        this.raftIntegration.onNodeStatusChange(newStatus);
    }

    public long getLeaderElectionTimeoutInMillis() {
        return RandomPicker.getInt(this.leaderElectionTimeout, this.leaderElectionTimeout + 1000);
    }

    public Object getAppendedEntryOnLeaderElection() {
        return this.raftIntegration.getAppendedEntryOnLeaderElection();
    }

    public boolean canReplicateNewEntry(Object operation) {
        long commitIndex;
        if (this.isTerminatedOrSteppedDown()) {
            return false;
        }
        RaftLog log = this.state.log();
        long lastLogIndex = log.lastLogOrSnapshotIndex();
        if (lastLogIndex - (commitIndex = this.state.commitIndex()) >= (long)this.maxUncommittedEntryCount) {
            return false;
        }
        if (this.status == RaftNodeStatus.TERMINATING) {
            return false;
        }
        if (this.status == RaftNodeStatus.UPDATING_GROUP_MEMBER_LIST) {
            return this.state.lastGroupMembers().isKnownMember(this.getLocalMember()) && !(operation instanceof RaftGroupCmd);
        }
        if (operation instanceof UpdateRaftGroupMembersCmd) {
            LogEntry lastCommittedEntry;
            LogEntry logEntry = lastCommittedEntry = commitIndex == log.snapshotIndex() ? log.snapshot() : log.getLogEntry(commitIndex);
            assert (lastCommittedEntry != null);
            return lastCommittedEntry.term() == this.state.term();
        }
        return true;
    }

    private void scheduleLeaderFailureDetection() {
        this.schedule(new LeaderFailureDetectionTask(), this.getLeaderElectionTimeoutInMillis());
    }

    public void scheduleHeartbeat() {
        this.broadcastAppendRequest();
        this.schedule(new HeartbeatTask(), this.heartbeatPeriodInMillis);
    }

    public void send(PreVoteRequest request, Endpoint target) {
        this.raftIntegration.send(request, target);
    }

    public void send(PreVoteResponse response, Endpoint target) {
        this.raftIntegration.send(response, target);
    }

    public void send(VoteRequest request, Endpoint target) {
        this.raftIntegration.send(request, target);
    }

    public void send(VoteResponse response, Endpoint target) {
        this.raftIntegration.send(response, target);
    }

    public void send(AppendRequest request, Endpoint target) {
        this.raftIntegration.send(request, target);
    }

    public void send(AppendSuccessResponse response, Endpoint target) {
        this.raftIntegration.send(response, target);
    }

    public void send(AppendFailureResponse response, Endpoint target) {
        this.raftIntegration.send(response, target);
    }

    public void broadcastAppendRequest() {
        for (Endpoint follower : this.state.remoteMembers()) {
            this.sendAppendRequest(follower);
        }
        this.updateLastAppendEntriesTimestamp();
    }

    public void sendAppendRequest(Endpoint follower) {
        LogEntry[] entries;
        if (!this.raftIntegration.isReachable(follower)) {
            return;
        }
        RaftLog raftLog = this.state.log();
        LeaderState leaderState = this.state.leaderState();
        FollowerState followerState = leaderState.getFollowerState(follower);
        if (followerState.isAppendRequestBackoffSet()) {
            return;
        }
        long nextIndex = followerState.nextIndex();
        if (nextIndex <= raftLog.snapshotIndex() && (!raftLog.containsLogEntry(nextIndex) || nextIndex > 1L && !raftLog.containsLogEntry(nextIndex - 1L))) {
            InstallSnapshot installSnapshot = new InstallSnapshot(this.localMember, this.state.term(), raftLog.snapshot());
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Sending " + installSnapshot + " to " + follower + " since next index: " + nextIndex + " <= snapshot index: " + raftLog.snapshotIndex());
            }
            followerState.setMaxAppendRequestBackoff();
            this.scheduleAppendAckResetTask();
            this.raftIntegration.send(installSnapshot, follower);
            return;
        }
        int prevEntryTerm = 0;
        long prevEntryIndex = 0L;
        boolean setAppendRequestBackoff = true;
        if (nextIndex > 1L) {
            LogEntry prevEntry;
            prevEntryIndex = nextIndex - 1L;
            LogEntry logEntry = prevEntry = raftLog.snapshotIndex() == prevEntryIndex ? raftLog.snapshot() : raftLog.getLogEntry(prevEntryIndex);
            assert (prevEntry != null) : "Prev entry index: " + prevEntryIndex + ", snapshot: " + raftLog.snapshotIndex();
            prevEntryTerm = prevEntry.term();
            long matchIndex = followerState.matchIndex();
            if (matchIndex == 0L) {
                entries = new LogEntry[]{};
            } else if (nextIndex <= raftLog.lastLogOrSnapshotIndex()) {
                long end = Math.min(nextIndex + (long)this.appendRequestMaxEntryCount, raftLog.lastLogOrSnapshotIndex());
                entries = raftLog.getEntriesBetween(nextIndex, end);
            } else {
                entries = new LogEntry[]{};
                setAppendRequestBackoff = false;
            }
        } else if (nextIndex == 1L && raftLog.lastLogOrSnapshotIndex() > 0L) {
            long end = Math.min(nextIndex + (long)this.appendRequestMaxEntryCount, raftLog.lastLogOrSnapshotIndex());
            entries = raftLog.getEntriesBetween(nextIndex, end);
        } else {
            entries = new LogEntry[]{};
            setAppendRequestBackoff = false;
        }
        AppendRequest request = new AppendRequest(this.getLocalMember(), this.state.term(), prevEntryTerm, prevEntryIndex, this.state.commitIndex(), entries);
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Sending " + request + " to " + follower + " with next index: " + nextIndex);
        }
        if (setAppendRequestBackoff) {
            followerState.setAppendRequestBackoff();
            this.scheduleAppendAckResetTask();
        }
        this.send(request, follower);
    }

    public void applyLogEntries() {
        long lastApplied;
        long commitIndex = this.state.commitIndex();
        if (commitIndex == (lastApplied = this.state.lastApplied())) {
            return;
        }
        assert (commitIndex > lastApplied) : "commit index: " + commitIndex + " cannot be smaller than last applied: " + lastApplied;
        RaftLog raftLog = this.state.log();
        for (long idx = this.state.lastApplied() + 1L; idx <= commitIndex; ++idx) {
            LogEntry entry = raftLog.getLogEntry(idx);
            if (entry == null) {
                String msg = "Failed to get log entry at index: " + idx;
                this.logger.severe(msg);
                throw new AssertionError((Object)msg);
            }
            this.applyLogEntry(entry);
            this.state.lastApplied(idx);
        }
        assert (this.status != RaftNodeStatus.TERMINATED || commitIndex == raftLog.lastLogOrSnapshotIndex()) : "commit index: " + commitIndex + " must be equal to " + raftLog.lastLogOrSnapshotIndex() + " on termination.";
        if (this.state.role() == RaftRole.LEADER || this.state.role() == RaftRole.FOLLOWER) {
            this.takeSnapshotIfCommitIndexAdvanced();
        }
    }

    private void applyLogEntry(LogEntry entry) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Processing " + entry);
        }
        Object response = null;
        Object operation = entry.operation();
        if (operation instanceof RaftGroupCmd) {
            if (operation instanceof DestroyRaftGroupCmd) {
                this.setStatus(RaftNodeStatus.TERMINATED);
            } else if (operation instanceof UpdateRaftGroupMembersCmd) {
                if (this.state.lastGroupMembers().index() < entry.index()) {
                    this.setStatus(RaftNodeStatus.UPDATING_GROUP_MEMBER_LIST);
                    UpdateRaftGroupMembersCmd op = (UpdateRaftGroupMembersCmd)operation;
                    this.updateGroupMembers(entry.index(), op.getMembers());
                }
                assert (this.status == RaftNodeStatus.UPDATING_GROUP_MEMBER_LIST) : "STATUS: " + (Object)((Object)this.status);
                assert (this.state.lastGroupMembers().index() == entry.index());
                this.state.commitGroupMembers();
                UpdateRaftGroupMembersCmd cmd = (UpdateRaftGroupMembersCmd)operation;
                if (cmd.getMember().equals(this.localMember) && cmd.getMode() == MembershipChangeMode.REMOVE) {
                    this.setStatus(RaftNodeStatus.STEPPED_DOWN);
                    this.invalidateFuturesUntil(entry.index() - 1L, new LeaderDemotedException(this.localMember, null));
                } else {
                    this.setStatus(RaftNodeStatus.ACTIVE);
                }
                response = entry.index();
            } else {
                response = new IllegalArgumentException("Invalid command: " + operation);
            }
        } else {
            response = this.raftIntegration.runOperation(operation, entry.index());
        }
        if (response == PostponedResponse.INSTANCE) {
            return;
        }
        this.completeFuture(entry.index(), response);
    }

    public void updateLastAppendEntriesTimestamp() {
        this.lastAppendEntriesTimestamp = Clock.currentTimeMillis();
    }

    public long lastAppendEntriesTimestamp() {
        return this.lastAppendEntriesTimestamp;
    }

    public RaftState state() {
        return this.state;
    }

    public void runQueryOperation(Object operation, SimpleCompletableFuture resultFuture) {
        long commitIndex = this.state.commitIndex();
        Object result = this.raftIntegration.runOperation(operation, commitIndex);
        resultFuture.setResult(result);
    }

    public void execute(Runnable task) {
        this.raftIntegration.execute(task);
    }

    public void schedule(Runnable task, long delayInMillis) {
        if (this.isTerminatedOrSteppedDown()) {
            return;
        }
        this.raftIntegration.schedule(task, delayInMillis, TimeUnit.MILLISECONDS);
    }

    public void registerFuture(long entryIndex, SimpleCompletableFuture future) {
        SimpleCompletableFuture f = this.futures.put(entryIndex, future);
        assert (f == null) : "Future object is already registered for entry index: " + entryIndex;
    }

    public void completeFuture(long entryIndex, Object response) {
        SimpleCompletableFuture f = this.futures.remove(entryIndex);
        if (f != null) {
            f.setResult(response);
        }
    }

    public void invalidateFuturesFrom(long entryIndex) {
        int count = 0;
        Iterator<Map.Entry<Long, SimpleCompletableFuture>> iterator = this.futures.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, SimpleCompletableFuture> entry = iterator.next();
            long index = entry.getKey();
            if (index < entryIndex) continue;
            entry.getValue().setResult(new LeaderDemotedException(this.localMember, this.state.leader()));
            iterator.remove();
            ++count;
        }
        if (count > 0) {
            this.logger.warning("Invalidated " + count + " futures from log index: " + entryIndex);
        }
    }

    private void invalidateFuturesUntil(long entryIndex, Object response) {
        int count = 0;
        Iterator<Map.Entry<Long, SimpleCompletableFuture>> iterator = this.futures.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, SimpleCompletableFuture> entry = iterator.next();
            long index = entry.getKey();
            if (index > entryIndex) continue;
            entry.getValue().setResult(response);
            iterator.remove();
            ++count;
        }
        if (count > 0) {
            this.logger.warning("Invalidated " + count + " futures until log index: " + entryIndex);
        }
    }

    private void takeSnapshotIfCommitIndexAdvanced() {
        long commitIndex = this.state.commitIndex();
        if (commitIndex - this.state.log().snapshotIndex() < (long)this.commitIndexAdvanceCountToSnapshot) {
            return;
        }
        if (this.isTerminatedOrSteppedDown()) {
            return;
        }
        RaftLog log = this.state.log();
        Object snapshot = this.raftIntegration.takeSnapshot(commitIndex);
        if (snapshot instanceof Throwable) {
            Throwable t = (Throwable)snapshot;
            this.logger.severe("Could not take snapshot at commit index: " + commitIndex, t);
            return;
        }
        int snapshotTerm = log.getLogEntry(commitIndex).term();
        RaftGroupMembers members = this.state.committedGroupMembers();
        SnapshotEntry snapshotEntry = new SnapshotEntry(snapshotTerm, commitIndex, snapshot, members.index(), members.members());
        long highestLogIndexToTruncate = commitIndex - (long)this.maxNumberOfLogsToKeepAfterSnapshot;
        LeaderState leaderState = this.state.leaderState();
        if (leaderState != null) {
            long[] matchIndices = leaderState.matchIndices();
            boolean allMatchIndicesKnown = true;
            for (int i = 0; i < matchIndices.length - 1; ++i) {
                allMatchIndicesKnown &= matchIndices[i] > 0L;
            }
            if (allMatchIndicesKnown) {
                highestLogIndexToTruncate = commitIndex;
                for (long matchIndex : matchIndices) {
                    if (matchIndex <= commitIndex - (long)this.maxNumberOfLogsToKeepAfterSnapshot || matchIndex >= highestLogIndexToTruncate) continue;
                    highestLogIndexToTruncate = matchIndex;
                }
                --highestLogIndexToTruncate;
            }
        }
        int truncatedEntryCount = log.setSnapshot(snapshotEntry, highestLogIndexToTruncate);
        if (this.logger.isFineEnabled()) {
            this.logger.fine(snapshotEntry + " is taken, " + truncatedEntryCount + " entries are truncated.");
        }
    }

    public boolean installSnapshot(SnapshotEntry snapshot) {
        long commitIndex = this.state.commitIndex();
        if (commitIndex > snapshot.index()) {
            this.logger.info("Ignored stale " + snapshot + ", commit index at: " + commitIndex);
            return false;
        }
        if (commitIndex == snapshot.index()) {
            this.logger.info("Ignored " + snapshot + " since commit index is same.");
            return true;
        }
        this.state.commitIndex(snapshot.index());
        int truncated = this.state.log().setSnapshot(snapshot);
        if (truncated > 0) {
            this.logger.info(truncated + " entries are truncated to install " + snapshot);
        }
        this.raftIntegration.restoreSnapshot(snapshot.operation(), snapshot.index());
        this.setStatus(RaftNodeStatus.ACTIVE);
        this.state.restoreGroupMembers(snapshot.groupMembersLogIndex(), snapshot.groupMembers());
        this.printMemberState();
        this.state.lastApplied(snapshot.index());
        this.invalidateFuturesUntil(snapshot.index(), new StaleAppendRequestException(this.state.leader()));
        this.logger.info(snapshot + " is installed.");
        return true;
    }

    public void printMemberState() {
        CPGroupId groupId = this.state.groupId();
        StringBuilder sb = new StringBuilder("\n\nCP Group Members {").append("groupId: ").append(groupId.name()).append("(").append(groupId.id()).append(")").append(", size:").append(this.state.memberCount()).append(", term:").append(this.state.term()).append(", logIndex:").append(this.state.membersLogIndex()).append("} [");
        for (Endpoint member : this.state.members()) {
            sb.append("\n\t").append(member);
            if (this.localMember.equals(member)) {
                sb.append(" - ").append((Object)this.state.role()).append(" this");
                continue;
            }
            if (!member.equals(this.state.leader())) continue;
            sb.append(" - ").append((Object)RaftRole.LEADER);
        }
        sb.append("\n]\n");
        this.logger.info(sb.toString());
    }

    public void updateGroupMembers(long logIndex, Collection<Endpoint> members) {
        this.state.updateGroupMembers(logIndex, members);
        this.printMemberState();
    }

    public void resetGroupMembers() {
        this.state.resetGroupMembers();
        this.printMemberState();
    }

    private void scheduleAppendAckResetTask() {
        if (this.appendRequestBackoffResetTaskScheduled) {
            return;
        }
        this.appendRequestBackoffResetTaskScheduled = true;
        this.schedule(this.appendRequestBackoffResetTask, this.appendRequestBackoffTimeoutInMillis);
    }

    private boolean isHeartbeatTimedOut(long timestamp) {
        long missedHeartbeatThreshold = (long)this.maxMissedLeaderHeartbeatCount * this.heartbeatPeriodInMillis;
        return timestamp + missedHeartbeatThreshold < Clock.currentTimeMillis();
    }

    private class AppendRequestBackoffResetTask
    extends RaftNodeStatusAwareTask {
        AppendRequestBackoffResetTask() {
            super(RaftNodeImpl.this);
        }

        @Override
        protected void innerRun() {
            RaftNodeImpl.this.appendRequestBackoffResetTaskScheduled = false;
            LeaderState leaderState = RaftNodeImpl.this.state.leaderState();
            if (leaderState != null) {
                Map<Endpoint, FollowerState> followerStates = leaderState.getFollowerStates();
                for (Map.Entry<Endpoint, FollowerState> entry : followerStates.entrySet()) {
                    FollowerState followerState = entry.getValue();
                    if (!followerState.isAppendRequestBackoffSet()) continue;
                    if (followerState.completeAppendRequestBackoffRound()) {
                        RaftNodeImpl.this.sendAppendRequest(entry.getKey());
                    }
                    RaftNodeImpl.this.scheduleAppendAckResetTask();
                }
            }
        }
    }

    private class LeaderFailureDetectionTask
    extends RaftNodeStatusAwareTask {
        LeaderFailureDetectionTask() {
            super(RaftNodeImpl.this);
        }

        @Override
        protected void innerRun() {
            try {
                Endpoint leader = RaftNodeImpl.this.state.leader();
                if (leader == null) {
                    if (RaftNodeImpl.this.state.role() == RaftRole.FOLLOWER) {
                        this.logger.warning("We are FOLLOWER and there is no current leader. Will start new election round...");
                        this.runPreVoteTask();
                    }
                } else if (!RaftNodeImpl.this.raftIntegration.isReachable(leader)) {
                    this.logger.warning("Current leader " + leader + " is not reachable. Will start new election round...");
                    this.resetLeaderAndStartElection();
                } else if (RaftNodeImpl.this.isHeartbeatTimedOut(RaftNodeImpl.this.lastAppendEntriesTimestamp)) {
                    this.logger.warning("Current leader " + leader + "'s heartbeats are timed-out. Will start new election round...");
                    this.resetLeaderAndStartElection();
                } else if (!RaftNodeImpl.this.state.committedGroupMembers().isKnownMember(leader)) {
                    this.logger.warning("Current leader " + leader + " is not member anymore. Will start new election round...");
                    this.resetLeaderAndStartElection();
                }
            }
            finally {
                RaftNodeImpl.this.scheduleLeaderFailureDetection();
            }
        }

        final void resetLeaderAndStartElection() {
            RaftNodeImpl.this.state.leader(null);
            RaftNodeImpl.this.printMemberState();
            this.runPreVoteTask();
        }

        private void runPreVoteTask() {
            if (RaftNodeImpl.this.state.preCandidateState() == null) {
                new PreVoteTask(RaftNodeImpl.this, RaftNodeImpl.this.state.term()).run();
            }
        }
    }

    private class HeartbeatTask
    extends RaftNodeStatusAwareTask {
        HeartbeatTask() {
            super(RaftNodeImpl.this);
        }

        @Override
        protected void innerRun() {
            if (RaftNodeImpl.this.state.role() == RaftRole.LEADER) {
                if (RaftNodeImpl.this.isHeartbeatTimedOut(RaftNodeImpl.this.state.leaderState().majorityAppendRequestAckTimestamp(RaftNodeImpl.this.state.majority()))) {
                    this.logger.warning("Demoting to " + (Object)((Object)RaftRole.FOLLOWER) + " since not received acks from majority recently...");
                    RaftNodeImpl.this.state.toFollower(RaftNodeImpl.this.state.term());
                    this.raftNode.printMemberState();
                    RaftNodeImpl.this.invalidateFuturesUntil(RaftNodeImpl.this.state.log().lastLogOrSnapshotIndex(), new StaleAppendRequestException(null));
                    return;
                }
                if (RaftNodeImpl.this.lastAppendEntriesTimestamp < Clock.currentTimeMillis() - RaftNodeImpl.this.heartbeatPeriodInMillis) {
                    RaftNodeImpl.this.broadcastAppendRequest();
                }
                RaftNodeImpl.this.scheduleHeartbeat();
            }
        }
    }
}

