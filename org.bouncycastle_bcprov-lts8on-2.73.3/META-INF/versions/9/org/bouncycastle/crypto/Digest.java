/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface Digest {
    public String getAlgorithmName();

    public int getDigestSize();

    public void update(byte var1);

    public void update(byte[] var1, int var2, int var3);

    public int doFinal(byte[] var1, int var2);

    public void reset();
}

