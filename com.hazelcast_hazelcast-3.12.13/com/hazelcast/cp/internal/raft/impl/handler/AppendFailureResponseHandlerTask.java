/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.handler.AbstractResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.state.FollowerState;
import com.hazelcast.cp.internal.raft.impl.state.LeaderState;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;

public class AppendFailureResponseHandlerTask
extends AbstractResponseHandlerTask {
    private final AppendFailureResponse resp;

    public AppendFailureResponseHandlerTask(RaftNodeImpl raftNode, AppendFailureResponse response) {
        super(raftNode);
        this.resp = response;
    }

    @Override
    protected void handleResponse() {
        RaftState state = this.raftNode.state();
        if (state.role() != RaftRole.LEADER) {
            this.logger.warning(this.resp + " is ignored since we are not LEADER.");
            return;
        }
        if (this.resp.term() > state.term()) {
            this.logger.info("Demoting to FOLLOWER after " + this.resp + " from current term: " + state.term());
            state.toFollower(this.resp.term());
            this.raftNode.printMemberState();
            return;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Received " + this.resp);
        }
        if (this.updateNextIndex(state)) {
            this.raftNode.sendAppendRequest(this.resp.follower());
        }
    }

    private boolean updateNextIndex(RaftState state) {
        LeaderState leaderState = state.leaderState();
        FollowerState followerState = leaderState.getFollowerState(this.resp.follower());
        long nextIndex = followerState.nextIndex();
        long matchIndex = followerState.matchIndex();
        if (this.resp.expectedNextIndex() == nextIndex) {
            followerState.appendRequestAckReceived();
            if (--nextIndex <= matchIndex) {
                this.logger.severe("Cannot decrement next index: " + nextIndex + " below match index: " + matchIndex + " for follower: " + this.resp.follower());
                return false;
            }
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Updating next index: " + nextIndex + " for follower: " + this.resp.follower());
            }
            followerState.nextIndex(nextIndex);
            return true;
        }
        return false;
    }

    @Override
    protected Endpoint sender() {
        return this.resp.follower();
    }
}

