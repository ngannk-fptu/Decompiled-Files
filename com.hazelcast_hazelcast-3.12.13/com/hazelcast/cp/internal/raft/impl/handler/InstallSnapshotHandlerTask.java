/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.log.SnapshotEntry;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.RaftNodeStatusAwareTask;

public class InstallSnapshotHandlerTask
extends RaftNodeStatusAwareTask
implements Runnable {
    private final InstallSnapshot req;

    public InstallSnapshotHandlerTask(RaftNodeImpl raftNode, InstallSnapshot req) {
        super(raftNode);
        this.req = req;
    }

    @Override
    protected void innerRun() {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Received " + this.req);
        }
        RaftState state = this.raftNode.state();
        SnapshotEntry snapshot = this.req.snapshot();
        if (this.req.term() < state.term()) {
            if (this.logger.isFineEnabled()) {
                this.logger.warning("Stale snapshot: " + this.req + " received in current term: " + state.term());
            }
            AppendFailureResponse resp = new AppendFailureResponse(this.raftNode.getLocalMember(), state.term(), snapshot.index() + 1L);
            this.raftNode.send(resp, this.req.leader());
            return;
        }
        if (this.req.term() > state.term() || state.role() != RaftRole.FOLLOWER) {
            this.logger.info("Demoting to FOLLOWER from current role: " + (Object)((Object)state.role()) + ", term: " + state.term() + " to new term: " + this.req.term() + " and leader: " + this.req.leader());
            state.toFollower(this.req.term());
            this.raftNode.printMemberState();
        }
        if (!this.req.leader().equals(state.leader())) {
            this.logger.info("Setting leader: " + this.req.leader());
            state.leader(this.req.leader());
            this.raftNode.printMemberState();
        }
        this.raftNode.updateLastAppendEntriesTimestamp();
        if (this.raftNode.installSnapshot(snapshot)) {
            this.raftNode.send(new AppendSuccessResponse(this.raftNode.getLocalMember(), this.req.term(), snapshot.index()), this.req.leader());
        }
    }
}

