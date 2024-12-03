/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.util.Integers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class Utils {
    static final Hashtable maxSecurityStrengths = new Hashtable();

    Utils() {
    }

    static int getMaxSecurityStrength(Digest d) {
        return (Integer)maxSecurityStrengths.get(d.getAlgorithmName());
    }

    static int getMaxSecurityStrength(Mac m) {
        String name = m.getAlgorithmName();
        return (Integer)maxSecurityStrengths.get(name.substring(0, name.indexOf("/")));
    }

    static byte[] hash_df(Digest digest, byte[] seedMaterial, int seedLength) {
        byte[] temp = new byte[(seedLength + 7) / 8];
        int len = temp.length / digest.getDigestSize();
        int counter = 1;
        byte[] dig = new byte[digest.getDigestSize()];
        for (int i = 0; i <= len; ++i) {
            digest.update((byte)counter);
            digest.update((byte)(seedLength >> 24));
            digest.update((byte)(seedLength >> 16));
            digest.update((byte)(seedLength >> 8));
            digest.update((byte)seedLength);
            digest.update(seedMaterial, 0, seedMaterial.length);
            digest.doFinal(dig, 0);
            int bytesToCopy = temp.length - i * dig.length > dig.length ? dig.length : temp.length - i * dig.length;
            System.arraycopy(dig, 0, temp, i * dig.length, bytesToCopy);
            ++counter;
        }
        if (seedLength % 8 != 0) {
            int shift = 8 - seedLength % 8;
            int carry = 0;
            for (int i = 0; i != temp.length; ++i) {
                int b = temp[i] & 0xFF;
                temp[i] = (byte)(b >>> shift | carry << 8 - shift);
                carry = b;
            }
        }
        return temp;
    }

    static boolean isTooLarge(byte[] bytes, int maxBytes) {
        return bytes != null && bytes.length > maxBytes;
    }

    static {
        maxSecurityStrengths.put("SHA-1", Integers.valueOf(128));
        maxSecurityStrengths.put("SHA-224", Integers.valueOf(192));
        maxSecurityStrengths.put("SHA-256", Integers.valueOf(256));
        maxSecurityStrengths.put("SHA-384", Integers.valueOf(256));
        maxSecurityStrengths.put("SHA-512", Integers.valueOf(256));
        maxSecurityStrengths.put("SHA-512/224", Integers.valueOf(192));
        maxSecurityStrengths.put("SHA-512/256", Integers.valueOf(256));
    }
}

