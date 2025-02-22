/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class Fingerprint {
    private static char[] encodingTable = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private final byte[] fingerprint;

    public Fingerprint(byte[] source) {
        this(source, 160);
    }

    public Fingerprint(byte[] source, int bitLength) {
        this.fingerprint = Fingerprint.calculateFingerprint(source, bitLength);
    }

    public byte[] getFingerprint() {
        return Arrays.clone(this.fingerprint);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i != this.fingerprint.length; ++i) {
            if (i > 0) {
                sb.append(":");
            }
            sb.append(encodingTable[this.fingerprint[i] >>> 4 & 0xF]);
            sb.append(encodingTable[this.fingerprint[i] & 0xF]);
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Fingerprint) {
            return Arrays.areEqual(((Fingerprint)o).fingerprint, this.fingerprint);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.fingerprint);
    }

    public static byte[] calculateFingerprint(byte[] input) {
        return Fingerprint.calculateFingerprint(input, 160);
    }

    public static byte[] calculateFingerprint(byte[] input, int bitLength) {
        if (bitLength % 8 != 0) {
            throw new IllegalArgumentException("bitLength must be a multiple of 8");
        }
        SHAKEDigest digest = new SHAKEDigest(256);
        digest.update(input, 0, input.length);
        byte[] rv = new byte[bitLength / 8];
        digest.doFinal(rv, 0, bitLength / 8);
        return rv;
    }
}

