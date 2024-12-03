/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.cp.internal.raft.command.DestroyRaftGroupCmd;
import com.hazelcast.cp.internal.raft.command.RaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.command.UpdateRaftGroupMembersCmd;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppendRequestHandlerTask
extends RaftNodeStatusAwareTask
implements Runnable {
    private final AppendRequest req;

    public AppendRequestHandlerTask(RaftNodeImpl raftNode, AppendRequest req) {
        super(raftNode);
        this.req = req;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void innerRun() {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Received " + this.req);
        }
        RaftState state = this.raftNode.state();
        if (this.req.term() < state.term()) {
            if (this.logger.isFineEnabled()) {
                this.logger.warning("Stale " + this.req + " received in current term: " + state.term());
            }
            this.raftNode.send(this.createFailureResponse(state.term()), this.req.leader());
            return;
        }
        RaftLog raftLog = state.log();
        if (this.req.term() > state.term() || state.role() != RaftRole.FOLLOWER) {
            this.logger.info("Demoting to FOLLOWER from current role: " + (Object)((Object)state.role()) + ", term: " + state.term() + " to new term: " + this.req.term() + " and leader: " + this.req.leader());
            state.toFollower(this.req.term());
            this.raftNode.printMemberState();
        }
        if (!this.req.leader().equals(state.leader())) {
            this.logger.info("Setting leader: " + this.req.leader());
            state.leader(this.req.leader());
            this.raftNode.printMemberState();
        }
        if (this.req.prevLogIndex() > 0L) {
            int prevLogTerm;
            long lastLogIndex = raftLog.lastLogOrSnapshotIndex();
            int lastLogTerm = raftLog.lastLogOrSnapshotTerm();
            if (this.req.prevLogIndex() == lastLogIndex) {
                prevLogTerm = lastLogTerm;
            } else {
                LogEntry prevLog = raftLog.getLogEntry(this.req.prevLogIndex());
                if (prevLog == null) {
                    if (this.logger.isFineEnabled()) {
                        this.logger.warning("Failed to get previous log index for " + this.req + ", last log index: " + lastLogIndex);
                    }
                    this.raftNode.send(this.createFailureResponse(this.req.term()), this.req.leader());
                    return;
                }
                prevLogTerm = prevLog.term();
            }
            if (this.req.prevLogTerm() != prevLogTerm) {
                if (this.logger.isFineEnabled()) {
                    this.logger.warning("Previous log term of " + this.req + " is different than ours: " + prevLogTerm);
                }
                this.raftNode.send(this.createFailureResponse(this.req.term()), this.req.leader());
                return;
            }
        }
        int truncatedAppendRequestEntryCount = 0;
        Object[] newEntries = null;
        if (this.req.entryCount() > 0) {
            long lastLogIndex = raftLog.lastLogOrSnapshotIndex();
            for (int i = 0; i < this.req.entryCount(); ++i) {
                LogEntry reqEntry = this.req.entries()[i];
                if (reqEntry.index() > lastLogIndex) {
                    newEntries = Arrays.copyOfRange(this.req.entries(), i, this.req.entryCount());
                    break;
                }
                LogEntry localEntry = raftLog.getLogEntry(reqEntry.index());
                assert (localEntry != null) : "Entry not found on log index: " + reqEntry.index() + " for " + this.req;
                if (reqEntry.term() == localEntry.term()) continue;
                List<LogEntry> truncatedEntries = raftLog.truncateEntriesFrom(reqEntry.index());
                if (this.logger.isFineEnabled()) {
                    this.logger.warning("Truncated " + truncatedEntries.size() + " entries from entry index: " + reqEntry.index() + " => " + truncatedEntries);
                } else {
                    this.logger.warning("Truncated " + truncatedEntries.size() + " entries from entry index: " + reqEntry.index());
                }
                this.raftNode.invalidateFuturesFrom(reqEntry.index());
                this.revertPreAppliedRaftGroupCmd(truncatedEntries);
                newEntries = Arrays.copyOfRange(this.req.entries(), i, this.req.entryCount());
                break;
            }
            if (newEntries != null && newEntries.length > 0) {
                if (raftLog.availableCapacity() < newEntries.length) {
                    if (this.logger.isFineEnabled()) {
                        this.logger.warning("Truncating " + newEntries.length + " entries to " + raftLog.availableCapacity() + " to fit into the available capacity of the Raft log");
                    }
                    truncatedAppendRequestEntryCount = newEntries.length - raftLog.availableCapacity();
                    newEntries = Arrays.copyOf(newEntries, raftLog.availableCapacity());
                }
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Appending " + newEntries.length + " entries: " + Arrays.toString(newEntries));
                }
                raftLog.appendEntries((LogEntry[])newEntries);
            }
        }
        long lastLogIndex = this.req.prevLogIndex() + (long)this.req.entryCount() - (long)truncatedAppendRequestEntryCount;
        long oldCommitIndex = state.commitIndex();
        if (this.req.leaderCommitIndex() > oldCommitIndex) {
            long newCommitIndex = Math.min(this.req.leaderCommitIndex(), lastLogIndex);
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Setting commit index: " + newCommitIndex);
            }
            state.commitIndex(newCommitIndex);
        }
        this.raftNode.updateLastAppendEntriesTimestamp();
        try {
            if (this.req.entryCount() > 0 || oldCommitIndex == state.commitIndex()) {
                AppendSuccessResponse resp = new AppendSuccessResponse(this.raftNode.getLocalMember(), state.term(), lastLogIndex);
                this.raftNode.send(resp, this.req.leader());
            }
        }
        finally {
            if (state.commitIndex() > oldCommitIndex) {
                this.raftNode.applyLogEntries();
            }
            if (newEntries != null) {
                this.preApplyRaftGroupCmd((LogEntry[])newEntries, state.commitIndex());
            }
        }
    }

    private void preApplyRaftGroupCmd(LogEntry[] entries, long commitIndex) {
        for (LogEntry entry : entries) {
            Object operation = entry.operation();
            if (entry.index() <= commitIndex || !(operation instanceof RaftGroupCmd)) continue;
            if (operation instanceof DestroyRaftGroupCmd) {
                this.raftNode.setStatus(RaftNodeStatus.TERMINATING);
            } else if (operation instanceof UpdateRaftGroupMembersCmd) {
                this.raftNode.setStatus(RaftNodeStatus.UPDATING_GROUP_MEMBER_LIST);
                UpdateRaftGroupMembersCmd op = (UpdateRaftGroupMembersCmd)operation;
                this.raftNode.updateGroupMembers(entry.index(), op.getMembers());
            } else assert (false) : "Invalid command: " + operation + " in " + this.raftNode.getGroupId();
            return;
        }
    }

    private void revertPreAppliedRaftGroupCmd(List<LogEntry> entries) {
        ArrayList<LogEntry> commandEntries = new ArrayList<LogEntry>();
        for (LogEntry entry : entries) {
            if (!(entry.operation() instanceof RaftGroupCmd)) continue;
            commandEntries.add(entry);
        }
        assert (commandEntries.size() <= 1) : "Reverted command entries: " + commandEntries;
        for (LogEntry entry : entries) {
            if (entry.operation() instanceof DestroyRaftGroupCmd) {
                this.raftNode.setStatus(RaftNodeStatus.ACTIVE);
                continue;
            }
            if (!(entry.operation() instanceof UpdateRaftGroupMembersCmd)) continue;
            this.raftNode.setStatus(RaftNodeStatus.ACTIVE);
            this.raftNode.resetGroupMembers();
        }
    }

    private AppendFailureResponse createFailureResponse(int term) {
        return new AppendFailureResponse(this.raftNode.getLocalMember(), term, this.req.prevLogIndex() + 1L);
    }
}

