/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.AbstractBucketBuilder;

public interface Extension<T extends AbstractBucketBuilder<T>> {
    public T builder();
}

