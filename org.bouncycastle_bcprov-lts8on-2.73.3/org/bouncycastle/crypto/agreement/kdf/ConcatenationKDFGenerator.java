/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KDFParameters;

public class ConcatenationKDFGenerator
implements DerivationFunction {
    private Digest digest;
    private byte[] shared;
    private byte[] otherInfo;
    private int hLen;

    public ConcatenationKDFGenerator(Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }

    @Override
    public void init(DerivationParameters param) {
        if (!(param instanceof KDFParameters)) {
            throw new IllegalArgumentException("KDF parameters required for generator");
        }
        KDFParameters p = (KDFParameters)param;
        this.shared = p.getSharedSecret();
        this.otherInfo = p.getIV();
    }

    public Digest getDigest() {
        return this.digest;
    }

    private void ItoOSP(int i, byte[] sp) {
        sp[0] = (byte)(i >>> 24);
        sp[1] = (byte)(i >>> 16);
        sp[2] = (byte)(i >>> 8);
        sp[3] = (byte)(i >>> 0);
    }

    @Override
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        if (len <= 0) {
            throw new IllegalArgumentException("len must be > 0");
        }
        if (out.length - len < outOff) {
            throw new OutputLengthException("output buffer too small");
        }
        byte[] hashBuf = new byte[this.hLen];
        byte[] C = new byte[4];
        int counter = 1;
        int outputLen = 0;
        this.digest.reset();
        if (len > this.hLen) {
            do {
                this.ItoOSP(counter, C);
                this.digest.update(C, 0, C.length);
                this.digest.update(this.shared, 0, this.shared.length);
                this.digest.update(this.otherInfo, 0, this.otherInfo.length);
                this.digest.doFinal(hashBuf, 0);
                System.arraycopy(hashBuf, 0, out, outOff + outputLen, this.hLen);
                outputLen += this.hLen;
            } while (counter++ < len / this.hLen);
        }
        if (outputLen < len) {
            this.ItoOSP(counter, C);
            this.digest.update(C, 0, C.length);
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update(this.otherInfo, 0, this.otherInfo.length);
            this.digest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, out, outOff + outputLen, len - outputLen);
        }
        return len;
    }
}

