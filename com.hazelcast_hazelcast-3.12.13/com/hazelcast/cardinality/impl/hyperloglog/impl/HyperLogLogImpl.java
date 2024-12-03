/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.hyperloglog.impl;

import com.hazelcast.cardinality.impl.CardinalityEstimatorDataSerializerHook;
import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogEncoder;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogEncoding;
import com.hazelcast.cardinality.impl.hyperloglog.impl.SparseHyperLogLogEncoder;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public class HyperLogLogImpl
implements HyperLogLog,
Versioned {
    private static final int LOWER_P_BOUND = 4;
    private static final int UPPER_P_BOUND = 16;
    private static final int DEFAULT_P = 14;
    private int m;
    private HyperLogLogEncoder encoder;
    private Long cachedEstimate;

    public HyperLogLogImpl() {
        this(14);
    }

    public HyperLogLogImpl(int p) {
        if (p < 4 || p > 16) {
            throw new IllegalArgumentException("Precision (p) outside valid range [4..16].");
        }
        this.m = 1 << p;
        this.encoder = new SparseHyperLogLogEncoder(p);
    }

    @Override
    public long estimate() {
        if (this.cachedEstimate == null) {
            this.cachedEstimate = this.encoder.estimate();
        }
        return this.cachedEstimate;
    }

    @Override
    public void add(long hash) {
        this.convertToDenseIfNeeded();
        boolean changed = this.encoder.add(hash);
        if (changed) {
            this.cachedEstimate = null;
        }
    }

    @Override
    public void addAll(long[] hashes) {
        for (long hash : hashes) {
            this.add(hash);
        }
    }

    @Override
    public void merge(HyperLogLog other) {
        if (!(other instanceof HyperLogLogImpl)) {
            throw new IllegalStateException("Can't merge " + other + " into " + this);
        }
        this.encoder = this.encoder.merge(((HyperLogLogImpl)other).encoder);
        this.cachedEstimate = null;
    }

    @Override
    public int getFactoryId() {
        return CardinalityEstimatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.encoder);
        out.writeInt(this.m);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.encoder = (HyperLogLogEncoder)in.readObject();
        this.m = in.readInt();
    }

    private void convertToDenseIfNeeded() {
        boolean shouldConvertToDense;
        boolean bl = shouldConvertToDense = HyperLogLogEncoding.SPARSE.equals((Object)this.encoder.getEncodingType()) && this.encoder.getMemoryFootprint() >= this.m;
        if (shouldConvertToDense) {
            this.encoder = ((SparseHyperLogLogEncoder)this.encoder).asDense();
        }
    }
}

