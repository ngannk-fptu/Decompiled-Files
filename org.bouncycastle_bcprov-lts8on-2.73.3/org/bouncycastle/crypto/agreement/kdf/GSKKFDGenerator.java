/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.agreement.kdf.GSKKDFParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GSKKFDGenerator
implements DigestDerivationFunction {
    private final Digest digest;
    private byte[] z;
    private int counter;
    private byte[] r;
    private byte[] buf;

    public GSKKFDGenerator(Digest digest) {
        this.digest = digest;
        this.buf = new byte[digest.getDigestSize()];
    }

    @Override
    public Digest getDigest() {
        return this.digest;
    }

    @Override
    public void init(DerivationParameters param) {
        if (!(param instanceof GSKKDFParameters)) {
            throw new IllegalArgumentException("unkown parameters type");
        }
        this.z = ((GSKKDFParameters)param).getZ();
        this.counter = ((GSKKDFParameters)param).getStartCounter();
        this.r = ((GSKKDFParameters)param).getNonce();
    }

    @Override
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        if (outOff + len > out.length) {
            throw new DataLengthException("output buffer too small");
        }
        this.digest.update(this.z, 0, this.z.length);
        byte[] c = Pack.intToBigEndian(this.counter++);
        this.digest.update(c, 0, c.length);
        if (this.r != null) {
            this.digest.update(this.r, 0, this.r.length);
        }
        this.digest.doFinal(this.buf, 0);
        System.arraycopy(this.buf, 0, out, outOff, len);
        Arrays.clear(this.buf);
        return len;
    }
}

