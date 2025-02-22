/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.handler.AbstractResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.state.CandidateState;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.LeaderElectionTask;

public class PreVoteResponseHandlerTask
extends AbstractResponseHandlerTask {
    private final PreVoteResponse resp;

    public PreVoteResponseHandlerTask(RaftNodeImpl raftNode, PreVoteResponse response) {
        super(raftNode);
        this.resp = response;
    }

    @Override
    protected void handleResponse() {
        RaftState state = this.raftNode.state();
        if (state.role() != RaftRole.FOLLOWER) {
            this.logger.info("Ignored " + this.resp + ". We are not FOLLOWER anymore.");
            return;
        }
        if (this.resp.term() < state.term()) {
            this.logger.warning("Stale " + this.resp + " is received, current term: " + state.term());
            return;
        }
        CandidateState preCandidateState = state.preCandidateState();
        if (preCandidateState == null) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Ignoring " + this.resp + ". We are not interested in pre-votes anymore.");
            }
            return;
        }
        if (this.resp.granted() && preCandidateState.grantVote(this.resp.voter())) {
            this.logger.info("Pre-vote granted from " + this.resp.voter() + " for term: " + this.resp.term() + ", number of votes: " + preCandidateState.voteCount() + ", majority: " + preCandidateState.majority());
        }
        if (preCandidateState.isMajorityGranted()) {
            this.logger.info("We have the majority during pre-vote phase. Let's start real election!");
            new LeaderElectionTask(this.raftNode).run();
        }
    }

    @Override
    protected Endpoint sender() {
        return this.resp.voter();
    }
}

