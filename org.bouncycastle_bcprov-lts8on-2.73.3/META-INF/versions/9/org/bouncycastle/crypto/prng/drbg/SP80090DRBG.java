/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng.drbg;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface SP80090DRBG {
    public int getBlockSize();

    public int generate(byte[] var1, byte[] var2, boolean var3);

    public void reseed(byte[] var1);
}

