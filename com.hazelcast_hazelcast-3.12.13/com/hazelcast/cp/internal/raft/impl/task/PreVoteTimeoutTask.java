/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.PreVoteTask;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;

public class PreVoteTimeoutTask
extends RaftNodeStatusAwareTask
implements Runnable {
    private int term;

    PreVoteTimeoutTask(RaftNodeImpl raftNode, int term) {
        super(raftNode);
        this.term = term;
    }

    @Override
    protected void innerRun() {
        RaftState state = this.raftNode.state();
        state.removePreCandidateState();
        if (state.role() != RaftRole.FOLLOWER) {
            return;
        }
        this.logger.fine("Pre-vote for term: " + state.term() + " has timed out!");
        new PreVoteTask(this.raftNode, this.term).run();
    }
}

