/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;

public interface BufferedBlockCipher {
    public BlockCipher getUnderlyingCipher();

    public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException;

    public int getBlockSize();

    public int getUpdateOutputSize(int var1);

    public int getOutputSize(int var1);

    public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException;

    public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException, IllegalStateException;

    public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException, InvalidCipherTextException;

    public void reset();
}

