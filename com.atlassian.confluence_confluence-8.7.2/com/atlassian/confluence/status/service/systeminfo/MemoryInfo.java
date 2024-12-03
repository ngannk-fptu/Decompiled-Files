/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation
 *  com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationFactory
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationFactory;

public class MemoryInfo {
    private static final long MEGABYTE = 0x100000L;
    private final Bytes maxHeap;
    private final Bytes allocatedHeap;
    private final Bytes freeAllocatedHeap;
    private final Bytes maxPermGen;
    private final Bytes usedPermGen;
    private final Bytes maxNonHeap;
    private final Bytes usedNonHeap;
    private final Bytes xms;
    private final Bytes xmx;

    public MemoryInfo() {
        Runtime rt = Runtime.getRuntime();
        RuntimeInformation extraRuntimeInformation = RuntimeInformationFactory.getRuntimeInformation();
        this.freeAllocatedHeap = new Bytes(rt.freeMemory());
        this.allocatedHeap = new Bytes(rt.totalMemory());
        this.maxHeap = new Bytes(rt.maxMemory());
        this.maxPermGen = new Bytes(extraRuntimeInformation.getTotalPermGenMemory());
        this.usedPermGen = new Bytes(extraRuntimeInformation.getTotalPermGenMemoryUsed());
        this.maxNonHeap = new Bytes(extraRuntimeInformation.getTotalNonHeapMemory());
        this.usedNonHeap = new Bytes(extraRuntimeInformation.getTotalNonHeapMemoryUsed());
        this.xmx = new Bytes(extraRuntimeInformation.getXmx());
        this.xms = new Bytes(extraRuntimeInformation.getXms());
    }

    public Bytes getAvailableHeap() {
        return this.maxHeap.minus(this.allocatedHeap).plus(this.freeAllocatedHeap);
    }

    public Bytes getFreeAllocatedHeap() {
        return this.freeAllocatedHeap;
    }

    public Bytes getMaxHeap() {
        return this.maxHeap;
    }

    public Bytes getAllocatedHeap() {
        return this.allocatedHeap;
    }

    public Bytes getUsedHeap() {
        return this.getAllocatedHeap().minus(this.getFreeAllocatedHeap());
    }

    @Deprecated
    public Bytes getMaxPermGen() {
        return this.maxPermGen;
    }

    @Deprecated
    public Bytes getUsedPermGen() {
        return this.usedPermGen;
    }

    @Deprecated
    public Bytes getAvailablePermGen() {
        return this.maxPermGen.minus(this.usedPermGen);
    }

    public Bytes getMaxNonHeap() {
        return this.maxNonHeap;
    }

    public Bytes getUsedNonHeap() {
        return this.usedNonHeap;
    }

    public Bytes getAvailableNonHeap() {
        if (this.maxNonHeap.value != -1L) {
            return this.maxNonHeap.minus(this.usedNonHeap);
        }
        return new Bytes(-1L);
    }

    public Bytes getXmx() {
        return this.xmx;
    }

    public Bytes getXms() {
        return this.xms;
    }

    public static class Bytes {
        private final long value;

        public Bytes(long value) {
            this.value = value;
        }

        public long bytes() {
            return this.value;
        }

        public long megabytes() {
            return this.value / 0x100000L;
        }

        public Bytes plus(Bytes b) {
            return new Bytes(this.value + b.value);
        }

        public Bytes minus(Bytes b) {
            return new Bytes(this.value - b.value);
        }

        public String toString() {
            return Long.toString(this.value);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Bytes bytes = (Bytes)o;
            return this.value == bytes.value;
        }

        public int hashCode() {
            return (int)(this.value ^ this.value >>> 32);
        }
    }
}

