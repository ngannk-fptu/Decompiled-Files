/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class HMacDSAKCalculator
implements DSAKCalculator {
    private static final BigInteger ZERO = BigInteger.valueOf(0L);
    private final HMac hMac;
    private final byte[] K;
    private final byte[] V;
    private BigInteger n;

    public HMacDSAKCalculator(Digest digest) {
        this.hMac = new HMac(digest);
        this.V = new byte[this.hMac.getMacSize()];
        this.K = new byte[this.hMac.getMacSize()];
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

    @Override
    public void init(BigInteger n, SecureRandom random) {
        throw new IllegalStateException("Operation not supported");
    }

    @Override
    public void init(BigInteger n, BigInteger d, byte[] message) {
        this.n = n;
        Arrays.fill(this.V, (byte)1);
        Arrays.fill(this.K, (byte)0);
        int size = BigIntegers.getUnsignedByteLength(n);
        byte[] x = new byte[size];
        byte[] dVal = BigIntegers.asUnsignedByteArray(d);
        System.arraycopy(dVal, 0, x, x.length - dVal.length, dVal.length);
        byte[] m = new byte[size];
        BigInteger mInt = this.bitsToInt(message);
        if (mInt.compareTo(n) >= 0) {
            mInt = mInt.subtract(n);
        }
        byte[] mVal = BigIntegers.asUnsignedByteArray(mInt);
        System.arraycopy(mVal, 0, m, m.length - mVal.length, mVal.length);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte)0);
        this.hMac.update(x, 0, x.length);
        this.hMac.update(m, 0, m.length);
        this.initAdditionalInput0(this.hMac);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte)1);
        this.hMac.update(x, 0, x.length);
        this.hMac.update(m, 0, m.length);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
    }

    @Override
    public BigInteger nextK() {
        byte[] t = new byte[BigIntegers.getUnsignedByteLength(this.n)];
        while (true) {
            int len;
            for (int tOff = 0; tOff < t.length; tOff += len) {
                this.hMac.update(this.V, 0, this.V.length);
                this.hMac.doFinal(this.V, 0);
                len = Math.min(t.length - tOff, this.V.length);
                System.arraycopy(this.V, 0, t, tOff, len);
            }
            BigInteger k = this.bitsToInt(t);
            if (k.compareTo(ZERO) > 0 && k.compareTo(this.n) < 0) {
                return k;
            }
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.update((byte)0);
            this.hMac.doFinal(this.K, 0);
            this.hMac.init(new KeyParameter(this.K));
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.doFinal(this.V, 0);
        }
    }

    protected void initAdditionalInput0(HMac hmac0) {
    }

    private BigInteger bitsToInt(byte[] t) {
        BigInteger v = new BigInteger(1, t);
        if (t.length * 8 > this.n.bitLength()) {
            v = v.shiftRight(t.length * 8 - this.n.bitLength());
        }
        return v;
    }
}

