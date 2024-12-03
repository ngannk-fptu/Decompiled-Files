/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.hyperloglog.impl;

import com.hazelcast.cardinality.impl.CardinalityEstimatorDataSerializerHook;
import com.hazelcast.cardinality.impl.hyperloglog.impl.DenseHyperLogLogEncoder;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogEncoder;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogEncoding;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;
import java.util.Arrays;

public class SparseHyperLogLogEncoder
implements HyperLogLogEncoder,
Versioned {
    private static final int P_PRIME = 25;
    private static final int P_PRIME_MASK = 0x1FFFFFF;
    private static final long P_PRIME_FENCE_MASK = 0x4000000000L;
    private static final int DEFAULT_TEMP_CAPACITY = 200;
    private int p;
    private int pMask;
    private int pFenseMask;
    private long pDiffMask;
    private VariableLengthDiffArray register;
    private int[] temp;
    private int mPrime;
    private int tempIdx;

    public SparseHyperLogLogEncoder() {
    }

    SparseHyperLogLogEncoder(int p) {
        this.init(p, new VariableLengthDiffArray());
    }

    public void init(int p, VariableLengthDiffArray register) {
        this.p = p;
        this.pMask = (1 << p) - 1;
        this.pFenseMask = 1 << 64 - p - 1;
        this.pDiffMask = 0x1FFFFFF ^ this.pMask;
        this.mPrime = 0x2000000;
        this.temp = new int[200];
        this.register = register;
    }

    @Override
    public boolean add(long hash) {
        boolean isTempAtCapacity;
        int encoded = this.encodeHash(hash);
        this.temp[this.tempIdx++] = encoded;
        boolean bl = isTempAtCapacity = this.tempIdx == 200;
        if (isTempAtCapacity) {
            this.mergeAndResetTmp();
        }
        return true;
    }

    @Override
    public long estimate() {
        this.mergeAndResetTmp();
        return this.linearCounting(this.mPrime, this.mPrime - this.register.total);
    }

    @Override
    public HyperLogLogEncoder merge(HyperLogLogEncoder encoder) {
        HyperLogLogEncoder dense = this.asDense();
        return dense.merge(encoder);
    }

    @Override
    public int getFactoryId() {
        return CardinalityEstimatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        this.mergeAndResetTmp();
        out.writeInt(this.p);
        out.writeInt(this.register.total);
        out.writeInt(this.register.mark);
        out.writeInt(this.register.prev);
        out.writeByteArray(this.register.elements);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int p = in.readInt();
        int total = in.readInt();
        int mark = in.readInt();
        int prev = in.readInt();
        byte[] bytes = in.readByteArray();
        this.init(p, new VariableLengthDiffArray(bytes, total, mark, prev));
    }

    @Override
    public HyperLogLogEncoding getEncodingType() {
        return HyperLogLogEncoding.SPARSE;
    }

    @Override
    public int getMemoryFootprint() {
        return this.register.mark + 800;
    }

    HyperLogLogEncoder asDense() {
        this.mergeAndResetTmp();
        byte[] dense = new byte[1 << this.p];
        for (int hash : this.register.explode()) {
            int index = this.decodeHashPIndex(hash);
            dense[index] = (byte)Math.max(dense[index], this.decodeHashRunOfZeros(hash));
        }
        return new DenseHyperLogLogEncoder(this.p, dense);
    }

    private int encodeHash(long hash) {
        if ((hash & this.pDiffMask) == 0L) {
            int newHash = (int)(hash & 0x1FFFFFFL) << 7;
            return newHash | Long.numberOfTrailingZeros(hash >>> 25 | 0x4000000000L) + 1 << 1 | 1;
        }
        return (int)(hash & 0x1FFFFFFL) << 1;
    }

    private int decodeHashPPrimeIndex(int hash) {
        if (!this.hasRunOfZerosEncoded(hash)) {
            return hash >> 1 & 0x1FFFFFF & this.mPrime - 1;
        }
        return hash >> 7 & 0x1FFFFFF & this.mPrime - 1;
    }

    private int decodeHashPIndex(long hash) {
        if (!this.hasRunOfZerosEncoded(hash)) {
            return (int)(hash >>> 1) & this.pMask;
        }
        return (int)(hash >>> 7) & this.pMask;
    }

    private byte decodeHashRunOfZeros(int hash) {
        int stripedZeroFlag = hash >>> 1;
        if (!this.hasRunOfZerosEncoded(hash)) {
            return (byte)(Long.numberOfTrailingZeros(stripedZeroFlag >>> this.p | this.pFenseMask) + 1);
        }
        int pW = stripedZeroFlag & 0x3F;
        return (byte)(pW + (25 - this.p));
    }

    private boolean hasRunOfZerosEncoded(long hash) {
        return (hash & 1L) == 1L;
    }

    private long linearCounting(int total, int empty) {
        return (long)((double)total * Math.log((double)total / (double)empty));
    }

    private void mergeAndResetTmp() {
        if (this.tempIdx == 0) {
            return;
        }
        int[] old = this.register.explode();
        int[] all = Arrays.copyOf(old, old.length + this.tempIdx);
        System.arraycopy(this.temp, 0, all, old.length, this.tempIdx);
        Arrays.sort(all);
        this.register.clear();
        int previousHash = all[0];
        for (int i = 1; i < all.length; ++i) {
            boolean conflictingIndex;
            int hash = all[i];
            boolean bl = conflictingIndex = this.decodeHashPPrimeIndex(hash) == this.decodeHashPPrimeIndex(previousHash);
            if (!conflictingIndex) {
                this.register.add(previousHash);
            }
            previousHash = hash;
        }
        this.register.add(previousHash);
        Arrays.fill(this.temp, 0);
        this.tempIdx = 0;
    }

    private static class VariableLengthDiffArray {
        private static final int INITIAL_CAPACITY = 32;
        private byte[] elements = new byte[32];
        private int prev;
        private int total;
        private int mark;

        VariableLengthDiffArray() {
        }

        VariableLengthDiffArray(byte[] elements, int total, int mark, int prev) {
            this.elements = elements;
            this.total = total;
            this.mark = mark;
            this.prev = prev;
        }

        void add(int value) {
            this.append(value - this.prev);
            this.prev = value;
        }

        void clear() {
            Arrays.fill(this.elements, (byte)0);
            this.mark = 0;
            this.total = 0;
            this.prev = 0;
        }

        int[] explode() {
            int[] exploded = new int[this.total];
            int counter = 0;
            int last = 0;
            for (int i = 0; i < this.mark; ++i) {
                byte element;
                int noOfBytes = 0;
                do {
                    element = this.elements[i++];
                    int n = counter;
                    exploded[n] = exploded[n] | (element & 0x7F) << 7 * noOfBytes++;
                } while (this.needsMoreBytes(element));
                int n = counter;
                exploded[n] = exploded[n] + last;
                last = exploded[counter];
                --i;
                ++counter;
            }
            return exploded;
        }

        private void append(int diff) {
            while (diff > 127) {
                this.ensureCapacity();
                this.elements[this.mark++] = (byte)(diff & 0x7F | 0x80);
                diff >>>= 7;
            }
            this.ensureCapacity();
            this.elements[this.mark++] = (byte)(diff & 0x7F);
            ++this.total;
        }

        private void ensureCapacity() {
            if (this.elements.length == this.mark) {
                int newCapacity = this.elements.length << 1;
                this.elements = Arrays.copyOf(this.elements, newCapacity);
            }
        }

        private boolean needsMoreBytes(byte val) {
            return (val & 0x80) != 0;
        }
    }
}

