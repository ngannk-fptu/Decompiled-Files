/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.sequence.CallIdSequence;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InvocationRegistry
implements Iterable<Invocation>,
MetricsProvider {
    private static final int CORE_SIZE_CHECK = 8;
    private static final int CORE_SIZE_FACTOR = 4;
    private static final int CONCURRENCY_LEVEL = 16;
    private static final int INITIAL_CAPACITY = 1000;
    private static final float LOAD_FACTOR = 0.75f;
    private static final double HUNDRED_PERCENT = 100.0;
    @Probe(name="invocations.pending", level=ProbeLevel.MANDATORY)
    private final ConcurrentMap<Long, Invocation> invocations;
    private final ILogger logger;
    private final CallIdSequence callIdSequence;
    private volatile boolean alive = true;

    public InvocationRegistry(ILogger logger, CallIdSequence callIdSequence) {
        this.logger = logger;
        this.callIdSequence = callIdSequence;
        int coreSize = RuntimeAvailableProcessors.get();
        boolean reallyMultiCore = coreSize >= 8;
        int concurrencyLevel = reallyMultiCore ? coreSize * 4 : 16;
        this.invocations = new ConcurrentHashMap<Long, Invocation>(1000, 0.75f, concurrencyLevel);
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "operation");
    }

    @Probe(name="invocations.usedPercentage")
    private double invocationsUsedPercentage() {
        int maxConcurrentInvocations = this.callIdSequence.getMaxConcurrentInvocations();
        if (maxConcurrentInvocations == Integer.MAX_VALUE) {
            return 0.0;
        }
        return 100.0 * (double)this.invocations.size() / (double)maxConcurrentInvocations;
    }

    @Probe(name="invocations.lastCallId")
    long getLastCallId() {
        return this.callIdSequence.getLastCallId();
    }

    public boolean register(Invocation invocation) {
        long callId;
        boolean force = invocation.op.isUrgent() || invocation.isRetryCandidate();
        try {
            callId = force ? this.callIdSequence.forceNext() : this.callIdSequence.next();
        }
        catch (HazelcastOverloadException e) {
            throw new HazelcastOverloadException("Failed to start invocation due to overload: " + invocation, e);
        }
        try {
            OperationAccessor.setCallId(invocation.op, callId);
        }
        catch (IllegalStateException e) {
            this.callIdSequence.complete();
            throw e;
        }
        this.invocations.put(callId, invocation);
        if (!this.alive) {
            invocation.notifyError(new HazelcastInstanceNotActiveException());
            return false;
        }
        return true;
    }

    public boolean deregister(Invocation invocation) {
        if (!OperationAccessor.deactivate(invocation.op)) {
            return false;
        }
        this.invocations.remove(invocation.op.getCallId());
        this.callIdSequence.complete();
        return true;
    }

    public int size() {
        return this.invocations.size();
    }

    @Override
    public Iterator<Invocation> iterator() {
        return this.invocations.values().iterator();
    }

    public Set<Map.Entry<Long, Invocation>> entrySet() {
        return this.invocations.entrySet();
    }

    public Invocation get(long callId) {
        return (Invocation)this.invocations.get(callId);
    }

    public void reset(Throwable cause) {
        for (Invocation invocation : this) {
            try {
                invocation.notifyError(new MemberLeftException(cause));
            }
            catch (Throwable e) {
                this.logger.warning(invocation + " could not be notified with reset message -> " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        this.alive = false;
        for (Invocation invocation : this) {
            try {
                invocation.notifyError(new HazelcastInstanceNotActiveException());
            }
            catch (Throwable e) {
                this.logger.warning(invocation + " could not be notified with shutdown message -> " + e.getMessage(), e);
            }
        }
    }
}

