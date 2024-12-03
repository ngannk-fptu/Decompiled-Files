/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto;

import com.amazonaws.services.s3.internal.crypto.ContentCryptoScheme;

class AesCtr
extends ContentCryptoScheme {
    AesCtr() {
    }

    @Override
    public String getKeyGeneratorAlgorithm() {
        return AES_GCM.getKeyGeneratorAlgorithm();
    }

    @Override
    public String getCipherAlgorithm() {
        return "AES/CTR/NoPadding";
    }

    @Override
    public int getKeyLengthInBits() {
        return AES_GCM.getKeyLengthInBits();
    }

    @Override
    public int getBlockSizeInBytes() {
        return AES_GCM.getBlockSizeInBytes();
    }

    @Override
    public int getIVLengthInBytes() {
        return 16;
    }

    @Override
    long getMaxPlaintextSize() {
        return -1L;
    }

    @Override
    public byte[] adjustIV(byte[] iv, long byteOffset) {
        if (iv.length != 12) {
            throw new UnsupportedOperationException();
        }
        int blockSize = this.getBlockSizeInBytes();
        long blockOffset = byteOffset / (long)blockSize;
        if (blockOffset * (long)blockSize != byteOffset) {
            throw new IllegalArgumentException("Expecting byteOffset to be multiple of 16, but got blockOffset=" + blockOffset + ", blockSize=" + blockSize + ", byteOffset=" + byteOffset);
        }
        byte[] J0 = this.computeJ0(iv);
        return AesCtr.incrementBlocks(J0, blockOffset);
    }

    private byte[] computeJ0(byte[] nonce) {
        int blockSize = this.getBlockSizeInBytes();
        byte[] J0 = new byte[blockSize];
        System.arraycopy(nonce, 0, J0, 0, nonce.length);
        J0[blockSize - 1] = 1;
        return AesCtr.incrementBlocks(J0, 1L);
    }
}

