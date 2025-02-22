/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.PreVoteTimeoutTask;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;
import java.util.Collection;

public class PreVoteTask
extends RaftNodeStatusAwareTask
implements Runnable {
    private int term;

    public PreVoteTask(RaftNodeImpl raftNode, int term) {
        super(raftNode);
        this.term = term;
    }

    @Override
    protected void innerRun() {
        RaftState state = this.raftNode.state();
        if (state.leader() != null) {
            this.logger.fine("No new pre-vote phase, we already have a LEADER: " + state.leader());
            return;
        }
        if (state.term() != this.term) {
            this.logger.fine("No new pre-vote phase for term= " + this.term + " because of new term: " + state.term());
            return;
        }
        Collection<Endpoint> remoteMembers = state.remoteMembers();
        if (remoteMembers.isEmpty()) {
            this.logger.fine("Remote members is empty. No need for pre-voting.");
            return;
        }
        state.initPreCandidateState();
        int nextTerm = state.term() + 1;
        RaftLog log = state.log();
        PreVoteRequest request = new PreVoteRequest(this.raftNode.getLocalMember(), nextTerm, log.lastLogOrSnapshotTerm(), log.lastLogOrSnapshotIndex());
        this.logger.info("Pre-vote started for next term: " + request.nextTerm() + ", last log index: " + request.lastLogIndex() + ", last log term: " + request.lastLogTerm());
        this.raftNode.printMemberState();
        for (Endpoint endpoint : remoteMembers) {
            this.raftNode.send(request, endpoint);
        }
        this.schedulePreVoteTimeout();
    }

    private void schedulePreVoteTimeout() {
        this.raftNode.schedule(new PreVoteTimeoutTask(this.raftNode, this.term), this.raftNode.getLeaderElectionTimeoutInMillis());
    }
}

