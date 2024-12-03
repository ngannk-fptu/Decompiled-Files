/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.Bits;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.JVMUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

@SuppressFBWarnings(value={"EI_EXPOSE_REP"})
public class HeapData
implements Data {
    public static final int PARTITION_HASH_OFFSET = 0;
    public static final int TYPE_OFFSET = 4;
    public static final int DATA_OFFSET = 8;
    public static final int HEAP_DATA_OVERHEAD = 8;
    private static final int ARRAY_HEADER_SIZE_IN_BYTES = 16;
    protected byte[] payload;

    public HeapData() {
    }

    public HeapData(byte[] payload) {
        if (payload != null && payload.length > 0 && payload.length < 8) {
            throw new IllegalArgumentException("Data should be either empty or should contain more than 8 bytes! -> " + Arrays.toString(payload));
        }
        this.payload = payload;
    }

    @Override
    public int dataSize() {
        return Math.max(this.totalSize() - 8, 0);
    }

    @Override
    public int totalSize() {
        return this.payload != null ? this.payload.length : 0;
    }

    @Override
    public void copyTo(byte[] dest, int destPos) {
        if (this.totalSize() > 0) {
            System.arraycopy(this.payload, 0, dest, destPos, this.payload.length);
        }
    }

    @Override
    public int getPartitionHash() {
        if (this.hasPartitionHash()) {
            return Bits.readIntB(this.payload, 0);
        }
        return this.hashCode();
    }

    @Override
    public boolean hasPartitionHash() {
        return this.payload != null && this.payload.length >= 8 && Bits.readIntB(this.payload, 0) != 0;
    }

    @Override
    public byte[] toByteArray() {
        return this.payload;
    }

    @Override
    public int getType() {
        if (this.totalSize() == 0) {
            return 0;
        }
        return Bits.readIntB(this.payload, 4);
    }

    @Override
    public int getHeapCost() {
        return JVMUtil.REFERENCE_COST_IN_BYTES + (this.payload != null ? 16 + this.payload.length : 0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Data)) {
            return false;
        }
        Data data = (Data)o;
        if (this.getType() != data.getType()) {
            return false;
        }
        int dataSize = this.dataSize();
        if (dataSize != data.dataSize()) {
            return false;
        }
        return dataSize == 0 || HeapData.equals(this.payload, data.toByteArray());
    }

    private static boolean equals(byte[] data1, byte[] data2) {
        if (data1 == data2) {
            return true;
        }
        if (data1 == null || data2 == null) {
            return false;
        }
        int length = data1.length;
        if (data2.length != length) {
            return false;
        }
        for (int i = length - 1; i >= 8; --i) {
            if (data1[i] == data2[i]) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return HashUtil.MurmurHash3_x86_32(this.payload, 8, this.dataSize());
    }

    @Override
    public long hash64() {
        return HashUtil.MurmurHash3_x64_64(this.payload, 8, this.dataSize());
    }

    @Override
    public boolean isPortable() {
        return -1 == this.getType();
    }

    @Override
    public boolean isJson() {
        return -130 == this.getType();
    }

    public String toString() {
        return "HeapData{type=" + this.getType() + ", hashCode=" + this.hashCode() + ", partitionHash=" + this.getPartitionHash() + ", totalSize=" + this.totalSize() + ", dataSize=" + this.dataSize() + ", heapCost=" + this.getHeapCost() + '}';
    }
}

