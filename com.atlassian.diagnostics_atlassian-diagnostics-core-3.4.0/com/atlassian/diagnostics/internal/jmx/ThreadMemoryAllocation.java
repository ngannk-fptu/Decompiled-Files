/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.jmx;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ThreadMemoryAllocation {
    private final String threadName;
    private final long memoryAllocationInBytes;
    private final StackTraceElement[] stackTrace;

    public ThreadMemoryAllocation(@Nonnull String threadName, long memoryAllocationInBytes, @Nonnull StackTraceElement[] stackTrace) {
        this.threadName = threadName;
        this.memoryAllocationInBytes = memoryAllocationInBytes;
        this.stackTrace = stackTrace;
    }

    @Nonnull
    public String getThreadName() {
        return this.threadName;
    }

    public long getMemoryAllocationInBytes() {
        return this.memoryAllocationInBytes;
    }

    @Nonnull
    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ThreadMemoryAllocation that = (ThreadMemoryAllocation)o;
        return this.memoryAllocationInBytes == that.memoryAllocationInBytes && Objects.equals(this.threadName, that.threadName) && Arrays.equals(this.stackTrace, that.stackTrace);
    }

    public int hashCode() {
        int result = Objects.hash(this.threadName, this.memoryAllocationInBytes);
        result = 31 * result + Arrays.hashCode(this.stackTrace);
        return result;
    }
}

