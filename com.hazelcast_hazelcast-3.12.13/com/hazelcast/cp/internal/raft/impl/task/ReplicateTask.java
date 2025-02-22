/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.exception.CPSubsystemException;
import com.hazelcast.cp.exception.CannotReplicateException;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.internal.raft.command.DestroyRaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.command.UpdateRaftGroupMembersCmd;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;

public class ReplicateTask
implements Runnable {
    private final RaftNodeImpl raftNode;
    private final Object operation;
    private final SimpleCompletableFuture resultFuture;
    private final ILogger logger;

    public ReplicateTask(RaftNodeImpl raftNode, Object operation, SimpleCompletableFuture resultFuture) {
        this.raftNode = raftNode;
        this.operation = operation;
        this.logger = raftNode.getLogger(this.getClass());
        this.resultFuture = resultFuture;
    }

    @Override
    public void run() {
        try {
            RaftLog log;
            if (!this.verifyRaftNodeStatus()) {
                return;
            }
            RaftState state = this.raftNode.state();
            if (state.role() != RaftRole.LEADER) {
                this.resultFuture.setResult(new NotLeaderException(this.raftNode.getGroupId(), this.raftNode.getLocalMember(), state.leader()));
                return;
            }
            if (!this.raftNode.canReplicateNewEntry(this.operation)) {
                this.resultFuture.setResult(new CannotReplicateException(this.raftNode.getLocalMember()));
                return;
            }
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Replicating: " + this.operation + " in term: " + state.term());
            }
            if (!(log = state.log()).checkAvailableCapacity(1)) {
                this.resultFuture.setResult(new IllegalStateException("Not enough capacity in RaftLog!"));
                return;
            }
            long newEntryLogIndex = log.lastLogOrSnapshotIndex() + 1L;
            this.raftNode.registerFuture(newEntryLogIndex, this.resultFuture);
            log.appendEntries(new LogEntry(state.term(), newEntryLogIndex, this.operation));
            this.preApplyRaftGroupCmd(newEntryLogIndex, this.operation);
            this.raftNode.broadcastAppendRequest();
        }
        catch (Throwable t) {
            this.logger.severe(this.operation + " could not be replicated to leader: " + this.raftNode.getLocalMember(), t);
            this.resultFuture.setResult(new CPSubsystemException("Internal failure", this.raftNode.getLeader(), t));
        }
    }

    private boolean verifyRaftNodeStatus() {
        if (this.raftNode.getStatus() == RaftNodeStatus.TERMINATED) {
            this.resultFuture.setResult(new CPGroupDestroyedException(this.raftNode.getGroupId()));
            this.logger.fine("Won't run " + this.operation + ", since raft node is terminated");
            return false;
        }
        if (this.raftNode.getStatus() == RaftNodeStatus.STEPPED_DOWN) {
            this.logger.fine("Won't run " + this.operation + ", since raft node is stepped down");
            this.resultFuture.setResult(new NotLeaderException(this.raftNode.getGroupId(), this.raftNode.getLocalMember(), null));
            return false;
        }
        return true;
    }

    private void preApplyRaftGroupCmd(long logIndex, Object operation) {
        if (operation instanceof DestroyRaftGroupCmd) {
            this.raftNode.setStatus(RaftNodeStatus.TERMINATING);
        } else if (operation instanceof UpdateRaftGroupMembersCmd) {
            this.raftNode.setStatus(RaftNodeStatus.UPDATING_GROUP_MEMBER_LIST);
            UpdateRaftGroupMembersCmd op = (UpdateRaftGroupMembersCmd)operation;
            this.raftNode.updateGroupMembers(logIndex, op.getMembers());
        }
    }
}

