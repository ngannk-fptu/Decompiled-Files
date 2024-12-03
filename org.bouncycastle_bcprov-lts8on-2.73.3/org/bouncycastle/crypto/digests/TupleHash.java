/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.util.Strings;

public class TupleHash
implements Xof,
Digest {
    private static final byte[] N_TUPLE_HASH = Strings.toByteArray("TupleHash");
    private final CSHAKEDigest cshake;
    private final int bitLength;
    private final int outputLength;
    private boolean firstOutput;

    public TupleHash(int bitLength, byte[] S) {
        this(bitLength, S, bitLength * 2);
    }

    public TupleHash(int bitLength, byte[] S, int outputSize) {
        this.cshake = new CSHAKEDigest(bitLength, N_TUPLE_HASH, S);
        this.bitLength = bitLength;
        this.outputLength = (outputSize + 7) / 8;
        this.reset();
    }

    public TupleHash(TupleHash original) {
        this.cshake = new CSHAKEDigest(original.cshake);
        this.bitLength = this.cshake.fixedOutputLength;
        this.outputLength = this.bitLength * 2 / 8;
        this.firstOutput = original.firstOutput;
    }

    @Override
    public String getAlgorithmName() {
        return "TupleHash" + this.cshake.getAlgorithmName().substring(6);
    }

    @Override
    public int getByteLength() {
        return this.cshake.getByteLength();
    }

    @Override
    public int getDigestSize() {
        return this.outputLength;
    }

    @Override
    public void update(byte in) throws IllegalStateException {
        byte[] bytes = XofUtils.encode(in);
        this.cshake.update(bytes, 0, bytes.length);
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        byte[] bytes = XofUtils.encode(in, inOff, len);
        this.cshake.update(bytes, 0, bytes.length);
    }

    private void wrapUp(int outputSize) {
        byte[] encOut = XofUtils.rightEncode((long)outputSize * 8L);
        this.cshake.update(encOut, 0, encOut.length);
        this.firstOutput = false;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.firstOutput) {
            this.wrapUp(this.getDigestSize());
        }
        int rv = this.cshake.doFinal(out, outOff, this.getDigestSize());
        this.reset();
        return rv;
    }

    @Override
    public int doFinal(byte[] out, int outOff, int outLen) {
        if (this.firstOutput) {
            this.wrapUp(this.getDigestSize());
        }
        int rv = this.cshake.doFinal(out, outOff, outLen);
        this.reset();
        return rv;
    }

    @Override
    public int doOutput(byte[] out, int outOff, int outLen) {
        if (this.firstOutput) {
            this.wrapUp(0);
        }
        return this.cshake.doOutput(out, outOff, outLen);
    }

    @Override
    public void reset() {
        this.cshake.reset();
        this.firstOutput = true;
    }
}

