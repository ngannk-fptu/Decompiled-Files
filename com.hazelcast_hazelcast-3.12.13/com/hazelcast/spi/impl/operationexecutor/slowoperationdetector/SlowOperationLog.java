/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor.slowoperationdetector;

import com.hazelcast.internal.diagnostics.OperationDescriptors;
import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.internal.management.dto.SlowOperationInvocationDTO;
import com.hazelcast.spi.Operation;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

final class SlowOperationLog {
    private static final int SHORT_STACKTRACE_LENGTH = 200;
    final AtomicInteger totalInvocations = new AtomicInteger(0);
    final String operation;
    final String stackTrace;
    final String shortStackTrace;
    private final ConcurrentHashMap<Integer, Invocation> invocations = new ConcurrentHashMap();

    SlowOperationLog(String stackTrace, Object task) {
        this.operation = task instanceof Operation ? OperationDescriptors.toOperationDesc((Operation)task) : task.getClass().getName();
        this.stackTrace = stackTrace;
        this.shortStackTrace = stackTrace.length() <= 200 ? stackTrace : stackTrace.substring(0, stackTrace.indexOf(10, 200)) + "\n\t(...)";
    }

    Invocation getOrCreate(Integer operationHashCode, Object operation, long lastDurationNanos, long nowNanos, long nowMillis) {
        Invocation candidate = this.invocations.get(operationHashCode);
        if (candidate != null) {
            return candidate;
        }
        int durationMs = (int)TimeUnit.NANOSECONDS.toMillis(lastDurationNanos);
        long startedAt = nowMillis - (long)durationMs;
        candidate = new Invocation(operation.toString(), startedAt, nowNanos, durationMs);
        this.invocations.put(operationHashCode, candidate);
        return candidate;
    }

    boolean purgeInvocations(long nowNanos, long slowOperationLogLifetimeNanos) {
        for (Map.Entry<Integer, Invocation> invocationEntry : this.invocations.entrySet()) {
            if (nowNanos - invocationEntry.getValue().lastAccessNanos <= slowOperationLogLifetimeNanos) continue;
            this.invocations.remove(invocationEntry.getKey());
        }
        return this.invocations.isEmpty();
    }

    SlowOperationDTO createDTO() {
        ArrayList<SlowOperationInvocationDTO> invocationDTOList = new ArrayList<SlowOperationInvocationDTO>(this.invocations.size());
        for (Map.Entry<Integer, Invocation> invocationEntry : this.invocations.entrySet()) {
            int id = invocationEntry.getKey();
            invocationDTOList.add(invocationEntry.getValue().createDTO(id));
        }
        return new SlowOperationDTO(this.operation, this.stackTrace, this.totalInvocations.get(), invocationDTOList);
    }

    static final class Invocation {
        private final String operationDetails;
        private final long startedAt;
        private long lastAccessNanos;
        private volatile int durationMs;

        private Invocation(String operationDetails, long startedAt, long lastAccessNanos, int durationMs) {
            this.operationDetails = operationDetails;
            this.startedAt = startedAt;
            this.lastAccessNanos = lastAccessNanos;
            this.durationMs = durationMs;
        }

        void update(long lastAccessNanos, int durationMs) {
            this.lastAccessNanos = lastAccessNanos;
            this.durationMs = durationMs;
        }

        SlowOperationInvocationDTO createDTO(int id) {
            return new SlowOperationInvocationDTO(id, this.operationDetails, this.startedAt, this.durationMs);
        }
    }
}

