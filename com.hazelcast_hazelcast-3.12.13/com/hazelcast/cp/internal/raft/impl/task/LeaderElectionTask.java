/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.LeaderElectionTimeoutTask;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;

public class LeaderElectionTask
extends RaftNodeStatusAwareTask
implements Runnable {
    public LeaderElectionTask(RaftNodeImpl raftNode) {
        super(raftNode);
    }

    @Override
    protected void innerRun() {
        RaftState state = this.raftNode.state();
        if (state.leader() != null) {
            this.logger.warning("No new election round, we already have a LEADER: " + state.leader());
            return;
        }
        VoteRequest request = state.toCandidate();
        this.logger.info("Leader election started for term: " + request.term() + ", last log index: " + request.lastLogIndex() + ", last log term: " + request.lastLogTerm());
        this.raftNode.printMemberState();
        for (Endpoint endpoint : state.remoteMembers()) {
            this.raftNode.send(request, endpoint);
        }
        this.scheduleLeaderElectionTimeout();
    }

    private void scheduleLeaderElectionTimeout() {
        this.raftNode.schedule(new LeaderElectionTimeoutTask(this.raftNode), this.raftNode.getLeaderElectionTimeoutInMillis());
    }
}

