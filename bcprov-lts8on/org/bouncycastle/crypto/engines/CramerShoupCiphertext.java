/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class CramerShoupCiphertext {
    BigInteger u1;
    BigInteger u2;
    BigInteger e;
    BigInteger v;

    public CramerShoupCiphertext() {
    }

    public CramerShoupCiphertext(BigInteger u1, BigInteger u2, BigInteger e, BigInteger v) {
        this.u1 = u1;
        this.u2 = u2;
        this.e = e;
        this.v = v;
    }

    public CramerShoupCiphertext(byte[] c) {
        int off = 0;
        int s = Pack.bigEndianToInt(c, off);
        byte[] tmp = Arrays.copyOfRange(c, off += 4, off + s);
        off += s;
        this.u1 = new BigInteger(tmp);
        s = Pack.bigEndianToInt(c, off);
        tmp = Arrays.copyOfRange(c, off += 4, off + s);
        off += s;
        this.u2 = new BigInteger(tmp);
        s = Pack.bigEndianToInt(c, off);
        tmp = Arrays.copyOfRange(c, off += 4, off + s);
        off += s;
        this.e = new BigInteger(tmp);
        s = Pack.bigEndianToInt(c, off);
        tmp = Arrays.copyOfRange(c, off += 4, off + s);
        off += s;
        this.v = new BigInteger(tmp);
    }

    public BigInteger getU1() {
        return this.u1;
    }

    public void setU1(BigInteger u1) {
        this.u1 = u1;
    }

    public BigInteger getU2() {
        return this.u2;
    }

    public void setU2(BigInteger u2) {
        this.u2 = u2;
    }

    public BigInteger getE() {
        return this.e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getV() {
        return this.v;
    }

    public void setV(BigInteger v) {
        this.v = v;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("u1: " + this.u1.toString());
        result.append("\nu2: " + this.u2.toString());
        result.append("\ne: " + this.e.toString());
        result.append("\nv: " + this.v.toString());
        return result.toString();
    }

    public byte[] toByteArray() {
        byte[] u1Bytes = this.u1.toByteArray();
        int u1Length = u1Bytes.length;
        byte[] u2Bytes = this.u2.toByteArray();
        int u2Length = u2Bytes.length;
        byte[] eBytes = this.e.toByteArray();
        int eLength = eBytes.length;
        byte[] vBytes = this.v.toByteArray();
        int vLength = vBytes.length;
        int off = 0;
        byte[] result = new byte[u1Length + u2Length + eLength + vLength + 16];
        Pack.intToBigEndian(u1Length, result, off);
        System.arraycopy(u1Bytes, 0, result, off += 4, u1Length);
        Pack.intToBigEndian(u2Length, result, off += u1Length);
        System.arraycopy(u2Bytes, 0, result, off += 4, u2Length);
        Pack.intToBigEndian(eLength, result, off += u2Length);
        System.arraycopy(eBytes, 0, result, off += 4, eLength);
        Pack.intToBigEndian(vLength, result, off += eLength);
        System.arraycopy(vBytes, 0, result, off += 4, vLength);
        off += vLength;
        return result;
    }
}

