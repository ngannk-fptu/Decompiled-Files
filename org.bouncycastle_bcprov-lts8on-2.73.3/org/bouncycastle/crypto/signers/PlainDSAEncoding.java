/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class PlainDSAEncoding
implements DSAEncoding {
    public static final PlainDSAEncoding INSTANCE = new PlainDSAEncoding();

    @Override
    public byte[] encode(BigInteger n, BigInteger r, BigInteger s) {
        int valueLength = BigIntegers.getUnsignedByteLength(n);
        byte[] result = new byte[valueLength * 2];
        this.encodeValue(n, r, result, 0, valueLength);
        this.encodeValue(n, s, result, valueLength, valueLength);
        return result;
    }

    @Override
    public BigInteger[] decode(BigInteger n, byte[] encoding) {
        int valueLength = BigIntegers.getUnsignedByteLength(n);
        if (encoding.length != valueLength * 2) {
            throw new IllegalArgumentException("Encoding has incorrect length");
        }
        return new BigInteger[]{this.decodeValue(n, encoding, 0, valueLength), this.decodeValue(n, encoding, valueLength, valueLength)};
    }

    protected BigInteger checkValue(BigInteger n, BigInteger x) {
        if (x.signum() < 0 || x.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        return x;
    }

    protected BigInteger decodeValue(BigInteger n, byte[] buf, int off, int len) {
        byte[] bs = Arrays.copyOfRange(buf, off, off + len);
        return this.checkValue(n, new BigInteger(1, bs));
    }

    private void encodeValue(BigInteger n, BigInteger x, byte[] buf, int off, int len) {
        byte[] bs = this.checkValue(n, x).toByteArray();
        int bsOff = Math.max(0, bs.length - len);
        int bsLen = bs.length - bsOff;
        int pos = len - bsLen;
        Arrays.fill(buf, off, off + pos, (byte)0);
        System.arraycopy(bs, bsOff, buf, off + pos, bsLen);
    }
}

