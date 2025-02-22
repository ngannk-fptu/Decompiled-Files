/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;
import com.hazelcast.util.Clock;

public class VoteRequestHandlerTask
extends RaftNodeStatusAwareTask
implements Runnable {
    private final VoteRequest req;

    public VoteRequestHandlerTask(RaftNodeImpl raftNode, VoteRequest req) {
        super(raftNode);
        this.req = req;
    }

    @Override
    protected void innerRun() {
        RaftState state = this.raftNode.state();
        Endpoint localEndpoint = this.raftNode.getLocalMember();
        if (this.raftNode.lastAppendEntriesTimestamp() > Clock.currentTimeMillis() - this.raftNode.getLeaderElectionTimeoutInMillis()) {
            this.logger.info("Rejecting " + this.req + " since received append entries recently.");
            this.raftNode.send(new VoteResponse(localEndpoint, state.term(), false), this.req.candidate());
            return;
        }
        if (state.term() > this.req.term()) {
            this.logger.info("Rejecting " + this.req + " since current term: " + state.term() + " is bigger");
            this.raftNode.send(new VoteResponse(localEndpoint, state.term(), false), this.req.candidate());
            return;
        }
        if (state.term() < this.req.term()) {
            if (state.role() != RaftRole.FOLLOWER) {
                this.logger.info("Demoting to FOLLOWER after " + this.req + " since current term: " + state.term() + " is smaller");
            } else {
                this.logger.info("Moving to new term: " + this.req.term() + " from current term: " + state.term() + " after " + this.req);
            }
            state.toFollower(this.req.term());
            this.raftNode.printMemberState();
        }
        if (state.leader() != null && !this.req.candidate().equals(state.leader())) {
            this.logger.warning("Rejecting " + this.req + " since we have a leader: " + state.leader());
            this.raftNode.send(new VoteResponse(localEndpoint, this.req.term(), false), this.req.candidate());
            return;
        }
        if (state.lastVoteTerm() == this.req.term() && state.votedFor() != null) {
            boolean granted = this.req.candidate().equals(state.votedFor());
            if (granted) {
                this.logger.info("Vote granted for duplicate" + this.req);
            } else {
                this.logger.info("Duplicate " + this.req + ". currently voted-for: " + state.votedFor());
            }
            this.raftNode.send(new VoteResponse(localEndpoint, this.req.term(), granted), this.req.candidate());
            return;
        }
        RaftLog raftLog = state.log();
        if (raftLog.lastLogOrSnapshotTerm() > this.req.lastLogTerm()) {
            this.logger.info("Rejecting " + this.req + " since our last log term: " + raftLog.lastLogOrSnapshotTerm() + " is greater");
            this.raftNode.send(new VoteResponse(localEndpoint, this.req.term(), false), this.req.candidate());
            return;
        }
        if (raftLog.lastLogOrSnapshotTerm() == this.req.lastLogTerm() && raftLog.lastLogOrSnapshotIndex() > this.req.lastLogIndex()) {
            this.logger.info("Rejecting " + this.req + " since our last log index: " + raftLog.lastLogOrSnapshotIndex() + " is greater");
            this.raftNode.send(new VoteResponse(localEndpoint, this.req.term(), false), this.req.candidate());
            return;
        }
        this.logger.info("Granted vote for " + this.req);
        state.persistVote(this.req.term(), this.req.candidate());
        this.raftNode.send(new VoteResponse(localEndpoint, this.req.term(), true), this.req.candidate());
    }
}

