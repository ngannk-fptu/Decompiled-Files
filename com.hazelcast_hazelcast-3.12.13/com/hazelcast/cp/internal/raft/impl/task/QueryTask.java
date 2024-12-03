/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.exception.CPSubsystemException;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raft.command.RaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.ReplicateTask;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;

public class QueryTask
implements Runnable {
    private final RaftNodeImpl raftNode;
    private final Object operation;
    private final QueryPolicy queryPolicy;
    private final SimpleCompletableFuture resultFuture;
    private final ILogger logger;

    public QueryTask(RaftNodeImpl raftNode, Object operation, QueryPolicy policy, SimpleCompletableFuture resultFuture) {
        this.raftNode = raftNode;
        this.operation = operation;
        this.logger = raftNode.getLogger(this.getClass());
        this.queryPolicy = policy;
        this.resultFuture = resultFuture;
    }

    @Override
    public void run() {
        try {
            if (!this.verifyOperation()) {
                return;
            }
            if (!this.verifyRaftNodeStatus()) {
                return;
            }
            switch (this.queryPolicy) {
                case LEADER_LOCAL: {
                    this.handleLeaderLocalRead();
                    break;
                }
                case ANY_LOCAL: {
                    this.handleAnyLocalRead();
                    break;
                }
                case LINEARIZABLE: {
                    new ReplicateTask(this.raftNode, this.operation, this.resultFuture).run();
                    break;
                }
                default: {
                    this.resultFuture.setResult(new IllegalArgumentException("Invalid query policy: " + (Object)((Object)this.queryPolicy)));
                    break;
                }
            }
        }
        catch (Throwable t) {
            this.logger.severe((Object)((Object)this.queryPolicy) + " query failed", t);
            this.resultFuture.setResult(new CPSubsystemException("Internal failure", this.raftNode.getLeader(), t));
        }
    }

    private void handleLeaderLocalRead() {
        RaftState state = this.raftNode.state();
        if (state.role() != RaftRole.LEADER) {
            this.resultFuture.setResult(new NotLeaderException(this.raftNode.getGroupId(), this.raftNode.getLocalMember(), state.leader()));
            return;
        }
        this.handleAnyLocalRead();
    }

    private void handleAnyLocalRead() {
        RaftState state = this.raftNode.state();
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Querying: " + this.operation + " with policy: " + (Object)((Object)this.queryPolicy) + " in term: " + state.term());
        }
        this.raftNode.runQueryOperation(this.operation, this.resultFuture);
    }

    private boolean verifyOperation() {
        if (this.operation instanceof RaftGroupCmd) {
            this.resultFuture.setResult(new IllegalArgumentException("cannot run query: " + this.operation));
            return false;
        }
        return true;
    }

    private boolean verifyRaftNodeStatus() {
        if (this.raftNode.getStatus() == RaftNodeStatus.TERMINATED) {
            this.resultFuture.setResult(new CPGroupDestroyedException(this.raftNode.getGroupId()));
            return false;
        }
        if (this.raftNode.getStatus() == RaftNodeStatus.STEPPED_DOWN) {
            this.resultFuture.setResult(new NotLeaderException(this.raftNode.getGroupId(), this.raftNode.getLocalMember(), null));
            return false;
        }
        return true;
    }
}

