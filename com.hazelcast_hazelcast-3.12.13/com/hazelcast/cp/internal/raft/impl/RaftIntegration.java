/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;
import java.util.concurrent.TimeUnit;

public interface RaftIntegration {
    public ILogger getLogger(String var1);

    public boolean isReady();

    public boolean isReachable(Endpoint var1);

    public boolean send(PreVoteRequest var1, Endpoint var2);

    public boolean send(PreVoteResponse var1, Endpoint var2);

    public boolean send(VoteRequest var1, Endpoint var2);

    public boolean send(VoteResponse var1, Endpoint var2);

    public boolean send(AppendRequest var1, Endpoint var2);

    public boolean send(AppendSuccessResponse var1, Endpoint var2);

    public boolean send(AppendFailureResponse var1, Endpoint var2);

    public boolean send(InstallSnapshot var1, Endpoint var2);

    public Object runOperation(Object var1, long var2);

    public Object takeSnapshot(long var1);

    public void restoreSnapshot(Object var1, long var2);

    public void execute(Runnable var1);

    public void schedule(Runnable var1, long var2, TimeUnit var4);

    public SimpleCompletableFuture newCompletableFuture();

    public Object getAppendedEntryOnLeaderElection();

    public void onNodeStatusChange(RaftNodeStatus var1);
}

