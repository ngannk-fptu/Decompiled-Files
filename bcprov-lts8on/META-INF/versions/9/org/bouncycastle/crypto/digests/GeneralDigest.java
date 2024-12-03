/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class GeneralDigest
implements ExtendedDigest,
Memoable {
    private static final int BYTE_LENGTH = 64;
    protected final CryptoServicePurpose purpose;
    private final byte[] xBuf = new byte[4];
    private int xBufOff;
    private long byteCount;

    protected GeneralDigest() {
        this(CryptoServicePurpose.ANY);
    }

    protected GeneralDigest(CryptoServicePurpose purpose) {
        this.purpose = purpose;
        this.xBufOff = 0;
    }

    protected GeneralDigest(GeneralDigest t) {
        this.purpose = t.purpose;
        this.copyIn(t);
    }

    protected GeneralDigest(byte[] encodedState) {
        CryptoServicePurpose[] values = CryptoServicePurpose.values();
        this.purpose = values[encodedState[encodedState.length - 1]];
        System.arraycopy(encodedState, 0, this.xBuf, 0, this.xBuf.length);
        this.xBufOff = Pack.bigEndianToInt(encodedState, 4);
        this.byteCount = Pack.bigEndianToLong(encodedState, 8);
    }

    protected void copyIn(GeneralDigest t) {
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
        this.xBufOff = t.xBufOff;
        this.byteCount = t.byteCount;
    }

    @Override
    public void update(byte in) {
        this.xBuf[this.xBufOff++] = in;
        if (this.xBufOff == this.xBuf.length) {
            this.processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        ++this.byteCount;
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        len = Math.max(0, len);
        int i = 0;
        if (this.xBufOff != 0) {
            while (i < len) {
                this.xBuf[this.xBufOff++] = in[inOff + i++];
                if (this.xBufOff != 4) continue;
                this.processWord(this.xBuf, 0);
                this.xBufOff = 0;
                break;
            }
        }
        int limit = len - 3;
        while (i < limit) {
            this.processWord(in, inOff + i);
            i += 4;
        }
        while (i < len) {
            this.xBuf[this.xBufOff++] = in[inOff + i++];
        }
        this.byteCount += (long)len;
    }

    public void finish() {
        long bitLength = this.byteCount << 3;
        this.update((byte)-128);
        while (this.xBufOff != 0) {
            this.update((byte)0);
        }
        this.processLength(bitLength);
        this.processBlock();
    }

    @Override
    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; ++i) {
            this.xBuf[i] = 0;
        }
    }

    protected void populateState(byte[] state) {
        System.arraycopy(this.xBuf, 0, state, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, state, 4);
        Pack.longToBigEndian(this.byteCount, state, 8);
    }

    @Override
    public int getByteLength() {
        return 64;
    }

    protected abstract void processWord(byte[] var1, int var2);

    protected abstract void processLength(long var1);

    protected abstract void processBlock();

    protected abstract CryptoServiceProperties cryptoServiceProperties();
}

