/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl;

import com.hazelcast.core.Endpoint;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import java.util.Collection;

public interface RaftNode {
    public CPGroupId getGroupId();

    public Endpoint getLocalMember();

    public Endpoint getLeader();

    public RaftNodeStatus getStatus();

    public Collection<Endpoint> getInitialMembers();

    public Collection<Endpoint> getCommittedMembers();

    public Collection<Endpoint> getAppliedMembers();

    public boolean isTerminatedOrSteppedDown();

    public void forceSetTerminatedStatus();

    public void handlePreVoteRequest(PreVoteRequest var1);

    public void handlePreVoteResponse(PreVoteResponse var1);

    public void handleVoteRequest(VoteRequest var1);

    public void handleVoteResponse(VoteResponse var1);

    public void handleAppendRequest(AppendRequest var1);

    public void handleAppendResponse(AppendSuccessResponse var1);

    public void handleAppendResponse(AppendFailureResponse var1);

    public void handleInstallSnapshot(InstallSnapshot var1);

    public ICompletableFuture replicate(Object var1);

    public ICompletableFuture replicateMembershipChange(Endpoint var1, MembershipChangeMode var2);

    public ICompletableFuture replicateMembershipChange(Endpoint var1, MembershipChangeMode var2, long var3);

    public ICompletableFuture query(Object var1, QueryPolicy var2);
}

