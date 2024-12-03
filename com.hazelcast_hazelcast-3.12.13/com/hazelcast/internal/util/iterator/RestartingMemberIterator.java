/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.iterator;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.ClusterTopologyChangedException;
import com.hazelcast.internal.util.futures.ChainingFuture;
import com.hazelcast.spi.exception.TargetNotMemberException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RestartingMemberIterator
implements Iterator<Member>,
ChainingFuture.ExceptionHandler {
    private final Queue<Member> memberQueue = new ConcurrentLinkedQueue<Member>();
    private final AtomicInteger retryCounter = new AtomicInteger();
    private final ClusterService clusterService;
    private final int maxRetries;
    private volatile boolean topologyChanged;
    private volatile Member nextMember;
    private volatile Set<Member> initialMembers;

    public RestartingMemberIterator(ClusterService clusterService, int maxRetries) {
        this.clusterService = clusterService;
        this.maxRetries = maxRetries;
        Set<Member> currentMembers = clusterService.getMembers();
        this.startNewRound(currentMembers);
    }

    private void startNewRound(Set<Member> currentMembers) {
        this.topologyChanged = false;
        this.memberQueue.addAll(currentMembers);
        this.nextMember = this.memberQueue.poll();
        this.initialMembers = currentMembers;
    }

    @Override
    public boolean hasNext() {
        if (this.nextMember != null) {
            return true;
        }
        return this.advance();
    }

    private boolean advance() {
        Set<Member> currentMembers = this.clusterService.getMembers();
        if (this.topologyChanged(currentMembers)) {
            this.retry(currentMembers);
            assert (this.nextMember != null);
            return true;
        }
        this.nextMember = this.memberQueue.poll();
        return this.nextMember != null;
    }

    private void retry(Set<Member> currentMembers) {
        if (this.retryCounter.incrementAndGet() > this.maxRetries) {
            throw new HazelcastException(String.format("Cluster topology was not stable for %d retries, invoke on stable cluster failed", this.maxRetries));
        }
        this.memberQueue.clear();
        this.startNewRound(currentMembers);
    }

    private boolean topologyChanged(Set<Member> currentMembers) {
        return this.topologyChanged || !currentMembers.equals(this.initialMembers);
    }

    @Override
    public Member next() {
        Member memberToReturn = this.nextMember;
        this.nextMember = null;
        if (memberToReturn != null) {
            return memberToReturn;
        }
        if (!this.advance()) {
            throw new NoSuchElementException("no more elements");
        }
        memberToReturn = this.nextMember;
        this.nextMember = null;
        return memberToReturn;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <T extends Throwable> void handle(T throwable) throws T {
        if (throwable instanceof ClusterTopologyChangedException) {
            this.topologyChanged = true;
            return;
        }
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException || throwable instanceof HazelcastInstanceNotActiveException) {
            return;
        }
        throw throwable;
    }

    public int getRetryCount() {
        return this.retryCounter.get();
    }
}

