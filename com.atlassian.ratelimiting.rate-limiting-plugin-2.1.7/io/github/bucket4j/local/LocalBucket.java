/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.local;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;

public interface LocalBucket
extends Bucket {
    public BucketConfiguration getConfiguration();
}

