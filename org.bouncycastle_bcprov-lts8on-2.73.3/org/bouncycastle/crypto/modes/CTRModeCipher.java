/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.SkippingStreamCipher;

public interface CTRModeCipher
extends MultiBlockCipher,
SkippingStreamCipher {
    public BlockCipher getUnderlyingCipher();
}

