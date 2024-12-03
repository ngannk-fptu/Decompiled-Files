/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.hyperloglog.impl;

import com.hazelcast.cardinality.impl.CardinalityEstimatorDataSerializerHook;
import com.hazelcast.cardinality.impl.hyperloglog.impl.DenseHyperLogLogConstants;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogEncoder;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogEncoding;
import com.hazelcast.cardinality.impl.hyperloglog.impl.SparseHyperLogLogEncoder;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

public class DenseHyperLogLogEncoder
implements HyperLogLogEncoder {
    private int p;
    private byte[] register;
    private transient int numOfEmptyRegs;
    private transient double[] invPowLookup;
    private transient int m;
    private transient long pFenseMask;

    public DenseHyperLogLogEncoder() {
    }

    public DenseHyperLogLogEncoder(int p) {
        this(p, null);
    }

    public DenseHyperLogLogEncoder(int p, byte[] register) {
        this.init(p, register);
    }

    private void init(int p, byte[] register) {
        this.p = p;
        this.numOfEmptyRegs = this.m = 1 << p;
        this.register = register != null ? register : new byte[this.m];
        this.invPowLookup = new double[64 - p + 1];
        this.pFenseMask = 1 << 64 - p - 1;
        this.prePopulateInvPowLookup();
    }

    @Override
    public boolean add(long hash) {
        int index = (int)hash & this.register.length - 1;
        int value = Long.numberOfTrailingZeros(hash >>> this.p | this.pFenseMask) + 1;
        assert (index < this.register.length);
        assert (value <= 255);
        assert (value <= 64 - this.p);
        if (value > this.register[index]) {
            this.register[index] = (byte)value;
            return true;
        }
        return false;
    }

    @Override
    public long estimate() {
        double raw = 1.0 / this.computeE() * this.alpha() * (double)this.m * (double)this.m;
        return this.applyRangeCorrection(raw);
    }

    @Override
    public HyperLogLogEncoder merge(HyperLogLogEncoder encoder) {
        DenseHyperLogLogEncoder otherDense = HyperLogLogEncoding.SPARSE.equals((Object)encoder.getEncodingType()) ? (DenseHyperLogLogEncoder)((SparseHyperLogLogEncoder)encoder).asDense() : (DenseHyperLogLogEncoder)encoder;
        for (int i = 0; i < this.register.length; ++i) {
            this.register[i] = (byte)Math.max(this.register[i], otherDense.register[i]);
        }
        return this;
    }

    @Override
    public int getFactoryId() {
        return CardinalityEstimatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.p);
        out.writeByteArray(this.register);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.init(in.readInt(), null);
        this.register = in.readByteArray();
    }

    @Override
    public int getMemoryFootprint() {
        return this.m;
    }

    @Override
    public HyperLogLogEncoding getEncodingType() {
        return HyperLogLogEncoding.DENSE;
    }

    private double alpha() {
        assert (this.m >= 16);
        if (this.m >= 128) {
            return 0.7213 / (1.0 + 1.079 / (double)this.m);
        }
        if (this.m == 64) {
            return 0.709;
        }
        if (this.m == 32) {
            return 0.697;
        }
        if (this.m == 16) {
            return 0.673;
        }
        return -1.0;
    }

    private long applyRangeCorrection(double e) {
        double ePrime = e <= (double)(this.m * 5) ? e - (double)this.estimateBias(e) : e;
        double h = this.numOfEmptyRegs != 0 ? (double)this.linearCounting(this.m, this.numOfEmptyRegs) : ePrime;
        return (long)(this.exceedsThreshold(h) ? ePrime : h);
    }

    private double computeE() {
        double e = 0.0;
        this.numOfEmptyRegs = 0;
        for (byte r : this.register) {
            if (r > 0) {
                e += this.invPow(r);
                continue;
            }
            ++this.numOfEmptyRegs;
        }
        return e + (double)this.numOfEmptyRegs;
    }

    private long estimateBias(double e) {
        int i = 0;
        double[] rawEstimates = DenseHyperLogLogConstants.RAW_ESTIMATE_DATA[this.p - 4];
        double closestToZero = Math.abs(e - rawEstimates[0]);
        TreeMap<Double, Integer> distances = new TreeMap<Double, Integer>();
        for (double est : rawEstimates) {
            double distance = e - est;
            distances.put(distance, i++);
            if (!(Math.abs(distance) < closestToZero)) continue;
            closestToZero = distance;
        }
        int kNN = 6;
        double sum = 0.0;
        Iterator firstX = distances.descendingMap().tailMap(closestToZero).entrySet().iterator();
        Iterator lastX = distances.tailMap(closestToZero).entrySet().iterator();
        int kNNLeft = kNN;
        while (kNNLeft-- > kNN / 2 && firstX.hasNext()) {
            sum += DenseHyperLogLogConstants.BIAS_DATA[this.p - 4][(Integer)firstX.next().getValue()];
        }
        while (kNNLeft-- >= 0 && lastX.hasNext()) {
            sum += DenseHyperLogLogConstants.BIAS_DATA[this.p - 4][(Integer)lastX.next().getValue()];
        }
        return (long)(sum / (double)kNN);
    }

    private boolean exceedsThreshold(double e) {
        return e >= (double)DenseHyperLogLogConstants.THRESHOLD[this.p - 4];
    }

    private double invPow(int index) {
        assert (index <= 64 - this.p);
        return this.invPowLookup[index];
    }

    private long linearCounting(int total, int empty) {
        return (long)((double)total * Math.log((double)total / (double)empty));
    }

    private void prePopulateInvPowLookup() {
        this.invPowLookup[0] = 1.0;
        for (int i = 1; i <= 64 - this.p; ++i) {
            this.invPowLookup[i] = Math.pow(2.0, -i);
        }
    }
}

