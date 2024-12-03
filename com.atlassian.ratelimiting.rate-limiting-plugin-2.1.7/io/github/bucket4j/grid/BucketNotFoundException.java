/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import java.io.Serializable;

public class BucketNotFoundException
extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    private final Serializable bucketId;

    public BucketNotFoundException(Serializable bucketId) {
        super(BucketNotFoundException.createErrorMessage(bucketId));
        this.bucketId = bucketId;
    }

    private static String createErrorMessage(Serializable bucketId) {
        return "Bucket with key [" + bucketId + "] does not exist";
    }

    public Object getBucketId() {
        return this.bucketId;
    }
}

