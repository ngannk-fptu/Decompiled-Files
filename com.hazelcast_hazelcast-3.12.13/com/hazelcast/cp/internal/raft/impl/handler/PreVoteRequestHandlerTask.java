/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;
import com.hazelcast.util.Clock;

public class PreVoteRequestHandlerTask
extends RaftNodeStatusAwareTask
implements Runnable {
    private final PreVoteRequest req;

    public PreVoteRequestHandlerTask(RaftNodeImpl raftNode, PreVoteRequest req) {
        super(raftNode);
        this.req = req;
    }

    @Override
    protected void innerRun() {
        RaftState state = this.raftNode.state();
        Endpoint localEndpoint = this.raftNode.getLocalMember();
        if (state.term() > this.req.nextTerm()) {
            this.logger.info("Rejecting " + this.req + " since current term: " + state.term() + " is bigger");
            this.raftNode.send(new PreVoteResponse(localEndpoint, state.term(), false), this.req.candidate());
            return;
        }
        if (this.raftNode.lastAppendEntriesTimestamp() > Clock.currentTimeMillis() - this.raftNode.getLeaderElectionTimeoutInMillis()) {
            this.logger.info("Rejecting " + this.req + " since received append entries recently.");
            this.raftNode.send(new PreVoteResponse(localEndpoint, state.term(), false), this.req.candidate());
            return;
        }
        RaftLog raftLog = state.log();
        if (raftLog.lastLogOrSnapshotTerm() > this.req.lastLogTerm()) {
            this.logger.info("Rejecting " + this.req + " since our last log term: " + raftLog.lastLogOrSnapshotTerm() + " is greater");
            this.raftNode.send(new PreVoteResponse(localEndpoint, this.req.nextTerm(), false), this.req.candidate());
            return;
        }
        if (raftLog.lastLogOrSnapshotTerm() == this.req.lastLogTerm() && raftLog.lastLogOrSnapshotIndex() > this.req.lastLogIndex()) {
            this.logger.info("Rejecting " + this.req + " since our last log index: " + raftLog.lastLogOrSnapshotIndex() + " is greater");
            this.raftNode.send(new PreVoteResponse(localEndpoint, this.req.nextTerm(), false), this.req.candidate());
            return;
        }
        this.logger.info("Granted pre-vote for " + this.req);
        this.raftNode.send(new PreVoteResponse(localEndpoint, this.req.nextTerm(), true), this.req.candidate());
    }
}

