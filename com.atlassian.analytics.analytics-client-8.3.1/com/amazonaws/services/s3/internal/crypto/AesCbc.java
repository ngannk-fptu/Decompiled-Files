/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto;

import com.amazonaws.services.s3.internal.crypto.ContentCryptoScheme;

class AesCbc
extends ContentCryptoScheme {
    AesCbc() {
    }

    @Override
    public String getKeyGeneratorAlgorithm() {
        return "AES";
    }

    @Override
    public String getCipherAlgorithm() {
        return "AES/CBC/PKCS5Padding";
    }

    @Override
    public int getKeyLengthInBits() {
        return 256;
    }

    @Override
    public int getBlockSizeInBytes() {
        return 16;
    }

    @Override
    public int getIVLengthInBytes() {
        return 16;
    }

    @Override
    long getMaxPlaintextSize() {
        return 0x10000000000000L;
    }
}

