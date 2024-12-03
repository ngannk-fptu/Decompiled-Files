/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.NativeServiceProvider;
import org.bouncycastle.crypto.modes.CBCModeCipher;
import org.bouncycastle.crypto.modes.CFBModeCipher;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.GCMModeCipher;

public interface NativeBlockCipherProvider
extends NativeServiceProvider {
    public GCMModeCipher createGCM();

    public CBCModeCipher createCBC();

    public CFBModeCipher createCFB(int var1);

    public CTRModeCipher createCTR();
}

