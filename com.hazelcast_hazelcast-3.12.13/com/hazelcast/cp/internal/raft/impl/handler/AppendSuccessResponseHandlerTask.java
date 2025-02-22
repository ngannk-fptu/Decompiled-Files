/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.handler;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.handler.AbstractResponseHandlerTask;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.state.FollowerState;
import com.hazelcast.cp.internal.raft.impl.state.LeaderState;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import java.util.Arrays;

public class AppendSuccessResponseHandlerTask
extends AbstractResponseHandlerTask {
    private final AppendSuccessResponse resp;

    public AppendSuccessResponseHandlerTask(RaftNodeImpl raftNode, AppendSuccessResponse response) {
        super(raftNode);
        this.resp = response;
    }

    @Override
    protected void handleResponse() {
        RaftState state = this.raftNode.state();
        if (state.role() != RaftRole.LEADER) {
            this.logger.warning("Ignored " + this.resp + ". We are not LEADER anymore.");
            return;
        }
        assert (this.resp.term() <= state.term()) : "Invalid " + this.resp + " for current term: " + state.term();
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Received " + this.resp);
        }
        if (!this.updateFollowerIndices(state)) {
            return;
        }
        long commitIndex = state.commitIndex();
        RaftLog raftLog = state.log();
        for (long quorumMatchIndex = this.findQuorumMatchIndex(state); quorumMatchIndex > commitIndex; --quorumMatchIndex) {
            LogEntry entry = raftLog.getLogEntry(quorumMatchIndex);
            if (entry.term() == state.term()) {
                this.commitEntries(state, quorumMatchIndex);
                break;
            }
            if (!this.logger.isFineEnabled()) continue;
            this.logger.fine("Cannot commit " + entry + " since an entry from the current term: " + state.term() + " is needed.");
        }
    }

    private boolean updateFollowerIndices(RaftState state) {
        Endpoint follower = this.resp.follower();
        LeaderState leaderState = state.leaderState();
        FollowerState followerState = leaderState.getFollowerState(follower);
        long matchIndex = followerState.matchIndex();
        long followerLastLogIndex = this.resp.lastLogIndex();
        if (followerLastLogIndex > matchIndex) {
            followerState.appendRequestAckReceived();
            long newNextIndex = followerLastLogIndex + 1L;
            followerState.matchIndex(followerLastLogIndex);
            followerState.nextIndex(newNextIndex);
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Updated match index: " + followerLastLogIndex + " and next index: " + newNextIndex + " for follower: " + follower);
            }
            if (state.log().lastLogOrSnapshotIndex() > followerLastLogIndex || state.commitIndex() == followerLastLogIndex) {
                this.raftNode.sendAppendRequest(follower);
            }
            return true;
        }
        if (followerLastLogIndex == matchIndex) {
            followerState.appendRequestAckReceived();
        } else if (this.logger.isFineEnabled()) {
            this.logger.fine("Will not update match index for follower: " + follower + ". follower last log index: " + followerLastLogIndex + ", match index: " + matchIndex);
        }
        return false;
    }

    private long findQuorumMatchIndex(RaftState state) {
        LeaderState leaderState = state.leaderState();
        long[] indices = leaderState.matchIndices();
        if (this.raftNode.state().isKnownMember(this.raftNode.getLocalMember())) {
            indices[indices.length - 1] = state.log().lastLogOrSnapshotIndex();
        } else {
            indices = Arrays.copyOf(indices, indices.length - 1);
        }
        Arrays.sort(indices);
        long quorumMatchIndex = indices[(indices.length - 1) / 2];
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Quorum match index: " + quorumMatchIndex + ", indices: " + Arrays.toString(indices));
        }
        return quorumMatchIndex;
    }

    private void commitEntries(RaftState state, long commitIndex) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Setting commit index: " + commitIndex);
        }
        state.commitIndex(commitIndex);
        this.raftNode.broadcastAppendRequest();
        this.raftNode.applyLogEntries();
    }

    @Override
    protected Endpoint sender() {
        return this.resp.follower();
    }
}

