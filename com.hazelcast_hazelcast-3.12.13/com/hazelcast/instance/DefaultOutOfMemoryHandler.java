/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OutOfMemoryHandler;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.OutOfMemoryHandlerHelper;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.MemoryInfoAccessor;
import com.hazelcast.util.RuntimeMemoryInfoAccessor;

@PrivateApi
public class DefaultOutOfMemoryHandler
extends OutOfMemoryHandler {
    public static final String FREE_MAX_PERCENTAGE_PROP = "hazelcast.oome.handler.free_max.percentage";
    static final String GC_OVERHEAD_LIMIT_EXCEEDED = "GC overhead limit exceeded";
    private static final int HUNDRED_PERCENT = 100;
    private static final int FIFTY_PERCENT = 50;
    private static final long MAX_TOTAL_DELTA = MemoryUnit.MEGABYTES.toBytes(1L);
    private static final double FREE_MAX_RATIO;
    private final double freeVersusMaxRatio;
    private final MemoryInfoAccessor memoryInfoAccessor;

    public DefaultOutOfMemoryHandler() {
        this(FREE_MAX_RATIO);
    }

    public DefaultOutOfMemoryHandler(double freeVersusMaxRatio) {
        this(freeVersusMaxRatio, new RuntimeMemoryInfoAccessor());
    }

    public DefaultOutOfMemoryHandler(double freeVersusMaxRatio, MemoryInfoAccessor memoryInfoAccessor) {
        this.freeVersusMaxRatio = freeVersusMaxRatio;
        this.memoryInfoAccessor = memoryInfoAccessor;
    }

    @Override
    public void onOutOfMemory(OutOfMemoryError oome, HazelcastInstance[] hazelcastInstances) {
        for (HazelcastInstance instance : hazelcastInstances) {
            if (!(instance instanceof HazelcastInstanceImpl)) continue;
            OutOfMemoryHandlerHelper.tryCloseConnections(instance);
            OutOfMemoryHandlerHelper.tryShutdown(instance);
        }
        try {
            oome.printStackTrace(System.err);
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
        }
    }

    @Override
    public boolean shouldHandle(OutOfMemoryError oome) {
        try {
            if (GC_OVERHEAD_LIMIT_EXCEEDED.equals(oome.getMessage())) {
                return true;
            }
            long maxMemory = this.memoryInfoAccessor.getMaxMemory();
            long totalMemory = this.memoryInfoAccessor.getTotalMemory();
            if (totalMemory < maxMemory - MAX_TOTAL_DELTA) {
                return false;
            }
            long freeMemory = this.memoryInfoAccessor.getFreeMemory();
            if ((double)freeMemory > (double)maxMemory * this.freeVersusMaxRatio) {
                return false;
            }
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
        }
        return true;
    }

    static {
        int percentage = Integer.parseInt(System.getProperty(FREE_MAX_PERCENTAGE_PROP, "10"));
        if (percentage < 1 || percentage > 50) {
            throw new IllegalArgumentException("'hazelcast.oome.handler.free_max.percentage' should be in [1, 50] range! Current: " + percentage);
        }
        FREE_MAX_RATIO = (double)percentage / 100.0;
    }
}

