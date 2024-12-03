/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.task;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.exception.CPSubsystemException;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.exception.MemberAlreadyExistsException;
import com.hazelcast.cp.internal.raft.exception.MemberDoesNotExistException;
import com.hazelcast.cp.internal.raft.exception.MismatchingGroupMembersCommitIndexException;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.RaftRole;
import com.hazelcast.cp.internal.raft.impl.command.UpdateRaftGroupMembersCmd;
import com.hazelcast.cp.internal.raft.impl.state.RaftGroupMembers;
import com.hazelcast.cp.internal.raft.impl.state.RaftState;
import com.hazelcast.cp.internal.raft.impl.task.ReplicateTask;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;
import java.util.LinkedHashSet;

public class MembershipChangeTask
implements Runnable {
    private final RaftNodeImpl raftNode;
    private final Long groupMembersCommitIndex;
    private final Endpoint member;
    private final MembershipChangeMode membershipChangeMode;
    private final SimpleCompletableFuture resultFuture;
    private final ILogger logger;

    public MembershipChangeTask(RaftNodeImpl raftNode, SimpleCompletableFuture resultFuture, Endpoint member, MembershipChangeMode membershipChangeMode) {
        this(raftNode, resultFuture, member, membershipChangeMode, null);
    }

    public MembershipChangeTask(RaftNodeImpl raftNode, SimpleCompletableFuture resultFuture, Endpoint member, MembershipChangeMode membershipChangeMode, Long groupMembersCommitIndex) {
        if (membershipChangeMode == null) {
            throw new IllegalArgumentException("Null membership change type");
        }
        this.raftNode = raftNode;
        this.groupMembersCommitIndex = groupMembersCommitIndex;
        this.member = member;
        this.membershipChangeMode = membershipChangeMode;
        this.resultFuture = resultFuture;
        this.logger = raftNode.getLogger(this.getClass());
    }

    @Override
    public void run() {
        try {
            if (!this.verifyRaftNodeStatus()) {
                return;
            }
            RaftState state = this.raftNode.state();
            if (state.role() != RaftRole.LEADER) {
                this.resultFuture.setResult(new NotLeaderException(this.raftNode.getGroupId(), this.raftNode.getLocalMember(), state.leader()));
                return;
            }
            if (!this.isValidGroupMemberCommitIndex()) {
                return;
            }
            LinkedHashSet<Endpoint> members = new LinkedHashSet<Endpoint>(state.members());
            boolean memberExists = members.contains(this.member);
            switch (this.membershipChangeMode) {
                case ADD: {
                    if (memberExists) {
                        this.resultFuture.setResult(new MemberAlreadyExistsException(this.member));
                        return;
                    }
                    members.add(this.member);
                    break;
                }
                case REMOVE: {
                    if (!memberExists) {
                        this.resultFuture.setResult(new MemberDoesNotExistException(this.member));
                        return;
                    }
                    members.remove(this.member);
                    break;
                }
                default: {
                    this.resultFuture.setResult(new IllegalArgumentException("Unknown type: " + (Object)((Object)this.membershipChangeMode)));
                    return;
                }
            }
            this.logger.info("New members after " + (Object)((Object)this.membershipChangeMode) + " " + this.member + " -> " + members);
            new ReplicateTask(this.raftNode, new UpdateRaftGroupMembersCmd(members, this.member, this.membershipChangeMode), this.resultFuture).run();
        }
        catch (Throwable t) {
            this.logger.severe(this + " failed", t);
            this.resultFuture.setResult(new CPSubsystemException("Internal failure", this.raftNode.getLeader(), t));
        }
    }

    private boolean verifyRaftNodeStatus() {
        if (this.raftNode.getStatus() == RaftNodeStatus.TERMINATED) {
            this.resultFuture.setResult(new CPGroupDestroyedException(this.raftNode.getGroupId()));
            this.logger.severe("Cannot " + (Object)((Object)this.membershipChangeMode) + " " + this.member + " with expected members commit index: " + this.groupMembersCommitIndex + " since raft node is terminated.");
            return false;
        }
        if (this.raftNode.getStatus() == RaftNodeStatus.STEPPED_DOWN) {
            this.logger.severe("Cannot " + (Object)((Object)this.membershipChangeMode) + " " + this.member + " with expected members commit index: " + this.groupMembersCommitIndex + " since raft node is stepped down.");
            this.resultFuture.setResult(new NotLeaderException(this.raftNode.getGroupId(), this.raftNode.getLocalMember(), null));
            return false;
        }
        return true;
    }

    private boolean isValidGroupMemberCommitIndex() {
        RaftState state;
        RaftGroupMembers groupMembers;
        if (this.groupMembersCommitIndex != null && (groupMembers = (state = this.raftNode.state()).committedGroupMembers()).index() != this.groupMembersCommitIndex.longValue()) {
            this.logger.severe("Cannot " + (Object)((Object)this.membershipChangeMode) + " " + this.member + " because expected members commit index: " + this.groupMembersCommitIndex + " is different than group members commit index: " + groupMembers.index());
            MismatchingGroupMembersCommitIndexException e = new MismatchingGroupMembersCommitIndexException(groupMembers.index(), groupMembers.members());
            this.resultFuture.setResult(e);
            return false;
        }
        return true;
    }

    public String toString() {
        return "MembershipChangeTask{groupMembersCommitIndex=" + this.groupMembersCommitIndex + ", member=" + this.member + ", membershipChangeMode=" + (Object)((Object)this.membershipChangeMode) + '}';
    }
}

