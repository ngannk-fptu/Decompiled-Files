/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class KMAC
implements Mac,
Xof {
    private static final byte[] padding = new byte[100];
    private final CSHAKEDigest cshake;
    private final int bitLength;
    private final int outputLength;
    private byte[] key;
    private boolean initialised;
    private boolean firstOutput;

    public KMAC(int bitLength, byte[] S) {
        this.cshake = new CSHAKEDigest(bitLength, Strings.toByteArray("KMAC"), S);
        this.bitLength = bitLength;
        this.outputLength = bitLength * 2 / 8;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        KeyParameter kParam = (KeyParameter)params;
        this.key = Arrays.clone(kParam.getKey());
        this.initialised = true;
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return "KMAC" + this.cshake.getAlgorithmName().substring(6);
    }

    @Override
    public int getByteLength() {
        return this.cshake.getByteLength();
    }

    @Override
    public int getMacSize() {
        return this.outputLength;
    }

    @Override
    public int getDigestSize() {
        return this.outputLength;
    }

    @Override
    public void update(byte in) throws IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialized");
        }
        this.cshake.update(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialized");
        }
        this.cshake.update(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.firstOutput) {
            if (!this.initialised) {
                throw new IllegalStateException("KMAC not initialized");
            }
            byte[] encOut = XofUtils.rightEncode(this.getMacSize() * 8);
            this.cshake.update(encOut, 0, encOut.length);
        }
        int rv = this.cshake.doFinal(out, outOff, this.getMacSize());
        this.reset();
        return rv;
    }

    @Override
    public int doFinal(byte[] out, int outOff, int outLen) {
        if (this.firstOutput) {
            if (!this.initialised) {
                throw new IllegalStateException("KMAC not initialized");
            }
            byte[] encOut = XofUtils.rightEncode(outLen * 8);
            this.cshake.update(encOut, 0, encOut.length);
        }
        int rv = this.cshake.doFinal(out, outOff, outLen);
        this.reset();
        return rv;
    }

    @Override
    public int doOutput(byte[] out, int outOff, int outLen) {
        if (this.firstOutput) {
            if (!this.initialised) {
                throw new IllegalStateException("KMAC not initialized");
            }
            byte[] encOut = XofUtils.rightEncode(0L);
            this.cshake.update(encOut, 0, encOut.length);
            this.firstOutput = false;
        }
        return this.cshake.doOutput(out, outOff, outLen);
    }

    @Override
    public void reset() {
        this.cshake.reset();
        if (this.key != null) {
            if (this.bitLength == 128) {
                this.bytePad(this.key, 168);
            } else {
                this.bytePad(this.key, 136);
            }
        }
        this.firstOutput = true;
    }

    private void bytePad(byte[] X, int w) {
        int required;
        byte[] bytes = XofUtils.leftEncode(w);
        this.update(bytes, 0, bytes.length);
        byte[] encX = KMAC.encode(X);
        this.update(encX, 0, encX.length);
        if (required > 0 && required != w) {
            for (required = w - (bytes.length + encX.length) % w; required > padding.length; required -= padding.length) {
                this.update(padding, 0, padding.length);
            }
            this.update(padding, 0, required);
        }
    }

    private static byte[] encode(byte[] X) {
        return Arrays.concatenate(XofUtils.leftEncode(X.length * 8), X);
    }
}

