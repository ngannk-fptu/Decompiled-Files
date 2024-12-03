/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.v1;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.GuardedBy;
import com.amazonaws.services.s3.internal.crypto.CipherLite;
import com.amazonaws.services.s3.internal.crypto.v1.ContentCryptoMaterial;
import com.amazonaws.services.s3.internal.crypto.v1.MultipartUploadContext;

class MultipartUploadCryptoContext
extends MultipartUploadContext {
    private final ContentCryptoMaterial cekMaterial;
    @GuardedBy(value="this")
    private int partNumber;
    private volatile boolean partUploadInProgress;

    MultipartUploadCryptoContext(String bucketName, String key, ContentCryptoMaterial cekMaterial) {
        super(bucketName, key);
        this.cekMaterial = cekMaterial;
    }

    CipherLite getCipherLite() {
        return this.cekMaterial.getCipherLite();
    }

    ContentCryptoMaterial getContentCryptoMaterial() {
        return this.cekMaterial;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void beginPartUpload(int nextPartNumber) throws SdkClientException {
        if (nextPartNumber < 1) {
            throw new IllegalArgumentException("part number must be at least 1");
        }
        if (this.partUploadInProgress) {
            throw new SdkClientException("Parts are required to be uploaded in series");
        }
        MultipartUploadCryptoContext multipartUploadCryptoContext = this;
        synchronized (multipartUploadCryptoContext) {
            if (nextPartNumber - this.partNumber > 1) {
                throw new SdkClientException("Parts are required to be uploaded in series (partNumber=" + this.partNumber + ", nextPartNumber=" + nextPartNumber + ")");
            }
            this.partNumber = nextPartNumber;
            this.partUploadInProgress = true;
        }
    }

    void endPartUpload() {
        this.partUploadInProgress = false;
    }
}

