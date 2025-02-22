/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.task.LeaderElectionTask;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;

public class LeaderElectionTimeoutTask
extends RaftNodeStatusAwareTask
implements Runnable {
    LeaderElectionTimeoutTask(RaftNodeImpl raftNode) {
        super(raftNode);
    }

    @Override
    protected void innerRun() {
        if (this.raftNode.state().role() != RaftRole.CANDIDATE) {
            return;
        }
        this.logger.warning("Leader election for term: " + this.raftNode.state().term() + " has timed out!");
        new LeaderElectionTask(this.raftNode).run();
    }
}

