/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ParallelHash
implements Xof,
Digest {
    private static final byte[] N_PARALLEL_HASH = Strings.toByteArray("ParallelHash");
    private final CSHAKEDigest cshake;
    private final CSHAKEDigest compressor;
    private final int bitLength;
    private final int outputLength;
    private final int B;
    private final byte[] buffer;
    private final byte[] compressorBuffer;
    private boolean firstOutput;
    private int nCount;
    private int bufOff;
    private final CryptoServicePurpose purpose;

    public ParallelHash(int bitLength, byte[] S, int B) {
        this(bitLength, S, B, bitLength * 2, CryptoServicePurpose.ANY);
    }

    public ParallelHash(int bitLength, byte[] S, int B, int outputSize) {
        this(bitLength, S, B, outputSize, CryptoServicePurpose.ANY);
    }

    public ParallelHash(int bitLength, byte[] S, int B, int outputSize, CryptoServicePurpose purpose) {
        this.cshake = new CSHAKEDigest(bitLength, N_PARALLEL_HASH, S);
        this.compressor = new CSHAKEDigest(bitLength, new byte[0], new byte[0]);
        this.bitLength = bitLength;
        this.B = B;
        this.outputLength = (outputSize + 7) / 8;
        this.buffer = new byte[B];
        this.compressorBuffer = new byte[bitLength * 2 / 8];
        this.purpose = purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, bitLength, purpose));
        this.reset();
    }

    public ParallelHash(ParallelHash source) {
        this.cshake = new CSHAKEDigest(source.cshake);
        this.compressor = new CSHAKEDigest(source.compressor);
        this.bitLength = source.bitLength;
        this.B = source.B;
        this.outputLength = source.outputLength;
        this.buffer = Arrays.clone(source.buffer);
        this.compressorBuffer = Arrays.clone(source.compressorBuffer);
        this.purpose = source.purpose;
        this.firstOutput = source.firstOutput;
        this.nCount = source.nCount;
        this.bufOff = source.bufOff;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.bitLength, this.purpose));
    }

    @Override
    public String getAlgorithmName() {
        return "ParallelHash" + this.cshake.getAlgorithmName().substring(6);
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
        this.buffer[this.bufOff++] = in;
        if (this.bufOff == this.buffer.length) {
            this.compress();
        }
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        len = Math.max(0, len);
        int i = 0;
        if (this.bufOff != 0) {
            while (i < len && this.bufOff != this.buffer.length) {
                this.buffer[this.bufOff++] = in[inOff + i++];
            }
            if (this.bufOff == this.buffer.length) {
                this.compress();
            }
        }
        if (i < len) {
            while (len - i >= this.B) {
                this.compress(in, inOff + i, this.B);
                i += this.B;
            }
        }
        while (i < len) {
            this.update(in[inOff + i++]);
        }
    }

    private void compress() {
        this.compress(this.buffer, 0, this.bufOff);
        this.bufOff = 0;
    }

    private void compress(byte[] buf, int offSet, int len) {
        this.compressor.update(buf, offSet, len);
        this.compressor.doFinal(this.compressorBuffer, 0, this.compressorBuffer.length);
        this.cshake.update(this.compressorBuffer, 0, this.compressorBuffer.length);
        ++this.nCount;
    }

    private void wrapUp(int outputSize) {
        if (this.bufOff != 0) {
            this.compress();
        }
        byte[] nOut = XofUtils.rightEncode(this.nCount);
        byte[] encOut = XofUtils.rightEncode(outputSize * 8);
        this.cshake.update(nOut, 0, nOut.length);
        this.cshake.update(encOut, 0, encOut.length);
        this.firstOutput = false;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.firstOutput) {
            this.wrapUp(this.outputLength);
        }
        int rv = this.cshake.doFinal(out, outOff, this.getDigestSize());
        this.reset();
        return rv;
    }

    @Override
    public int doFinal(byte[] out, int outOff, int outLen) {
        if (this.firstOutput) {
            this.wrapUp(this.outputLength);
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
        Arrays.clear(this.buffer);
        byte[] hdr = XofUtils.leftEncode(this.B);
        this.cshake.update(hdr, 0, hdr.length);
        this.nCount = 0;
        this.bufOff = 0;
        this.firstOutput = true;
    }
}

