/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

public interface ProxyManager<K extends Serializable> {
    default public Bucket getProxy(K key, BucketConfiguration configuration) {
        return this.getProxy(key, () -> configuration);
    }

    public Bucket getProxy(K var1, Supplier<BucketConfiguration> var2);

    public Optional<Bucket> getProxy(K var1);

    public Optional<BucketConfiguration> getProxyConfiguration(K var1);
}

