/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.cp.internal.raft.impl.handler.AbstractResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.CandidateState;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;

public class VoteResponseHandlerTask
extends AbstractResponseHandlerTask {
    private final VoteResponse resp;

    public VoteResponseHandlerTask(RaftNodeImpl raftNode, VoteResponse response) {
        super(raftNode);
        this.resp = response;
    }

    @Override
    protected void handleResponse() {
        RaftState state = this.raftNode.state();
        if (state.role() != RaftRole.CANDIDATE) {
            this.logger.info("Ignored " + this.resp + ". We are not CANDIDATE anymore.");
            return;
        }
        if (this.resp.term() > state.term()) {
            this.logger.info("Demoting to FOLLOWER from current term: " + state.term() + " to new term: " + this.resp.term() + " after " + this.resp);
            state.toFollower(this.resp.term());
            this.raftNode.printMemberState();
            return;
        }
        if (this.resp.term() < state.term()) {
            this.logger.warning("Stale " + this.resp + " is received, current term: " + state.term());
            return;
        }
        CandidateState candidateState = state.candidateState();
        if (this.resp.granted() && candidateState.grantVote(this.resp.voter())) {
            this.logger.info("Vote granted from " + this.resp.voter() + " for term: " + state.term() + ", number of votes: " + candidateState.voteCount() + ", majority: " + candidateState.majority());
        }
        if (candidateState.isMajorityGranted()) {
            this.logger.info("We are the LEADER!");
            state.toLeader();
            this.appendEntryAfterLeaderElection();
            this.raftNode.printMemberState();
            this.raftNode.scheduleHeartbeat();
        }
    }

    private void appendEntryAfterLeaderElection() {
        Object entry = this.raftNode.getAppendedEntryOnLeaderElection();
        if (entry != null) {
            RaftState state = this.raftNode.state();
            RaftLog log = state.log();
            log.appendEntries(new LogEntry(state.term(), log.lastLogOrSnapshotIndex() + 1L, entry));
        }
    }

    @Override
    protected Endpoint sender() {
        return this.resp.voter();
    }
}

