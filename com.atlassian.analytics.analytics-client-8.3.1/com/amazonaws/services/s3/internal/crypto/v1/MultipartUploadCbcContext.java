/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.v1;

import com.amazonaws.services.s3.internal.crypto.v1.ContentCryptoMaterial;
import com.amazonaws.services.s3.internal.crypto.v1.MultipartUploadCryptoContext;

final class MultipartUploadCbcContext
extends MultipartUploadCryptoContext {
    private byte[] nextIV;

    MultipartUploadCbcContext(String bucketName, String key, ContentCryptoMaterial cekMaterial) {
        super(bucketName, key, cekMaterial);
    }

    public void setNextInitializationVector(byte[] nextIV) {
        this.nextIV = nextIV;
    }

    public byte[] getNextInitializationVector() {
        return this.nextIV;
    }
}

