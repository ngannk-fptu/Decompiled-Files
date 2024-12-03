/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface BlockCipher {
    public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException;

    public String getAlgorithmName();

    public int getBlockSize();

    public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException;

    public void reset();
}

