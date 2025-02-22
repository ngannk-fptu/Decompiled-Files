/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class BaseKDFBytesGenerator
implements DigestDerivationFunction {
    private int counterStart;
    private Digest digest;
    private byte[] shared;
    private byte[] iv;

    protected BaseKDFBytesGenerator(int counterStart, Digest digest) {
        this.counterStart = counterStart;
        this.digest = digest;
    }

    @Override
    public void init(DerivationParameters param) {
        if (param instanceof KDFParameters) {
            KDFParameters p = (KDFParameters)param;
            this.shared = p.getSharedSecret();
            this.iv = p.getIV();
        } else if (param instanceof ISO18033KDFParameters) {
            ISO18033KDFParameters p = (ISO18033KDFParameters)param;
            this.shared = p.getSeed();
            this.iv = null;
        } else {
            throw new IllegalArgumentException("KDF parameters required for generator");
        }
    }

    @Override
    public Digest getDigest() {
        return this.digest;
    }

    @Override
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        if (out.length - len < outOff) {
            throw new OutputLengthException("output buffer too small");
        }
        long oBytes = len;
        int outLen = this.digest.getDigestSize();
        if (oBytes > 0x1FFFFFFFFL) {
            throw new IllegalArgumentException("Output length too large");
        }
        int cThreshold = (int)((oBytes + (long)outLen - 1L) / (long)outLen);
        byte[] dig = new byte[this.digest.getDigestSize()];
        byte[] C = new byte[4];
        Pack.intToBigEndian(this.counterStart, C, 0);
        int counterBase = this.counterStart & 0xFFFFFF00;
        for (int i = 0; i < cThreshold; ++i) {
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update(C, 0, C.length);
            if (this.iv != null) {
                this.digest.update(this.iv, 0, this.iv.length);
            }
            this.digest.doFinal(dig, 0);
            if (len > outLen) {
                System.arraycopy(dig, 0, out, outOff, outLen);
                outOff += outLen;
                len -= outLen;
            } else {
                System.arraycopy(dig, 0, out, outOff, len);
            }
            C[3] = (byte)(C[3] + 1);
            if (C[3] != 0) continue;
            Pack.intToBigEndian(counterBase += 256, C, 0);
        }
        this.digest.reset();
        return (int)oBytes;
    }
}

