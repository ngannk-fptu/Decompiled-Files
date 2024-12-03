/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;

public interface CCMModeCipher
extends AEADBlockCipher {
    public int processPacket(byte[] var1, int var2, int var3, byte[] var4, int var5) throws InvalidCipherTextException;

    public byte[] processPacket(byte[] var1, int var2, int var3) throws InvalidCipherTextException;
}

