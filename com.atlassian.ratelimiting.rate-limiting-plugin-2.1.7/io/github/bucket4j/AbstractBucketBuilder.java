/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConfigurationBuilder;

public class AbstractBucketBuilder<T extends AbstractBucketBuilder> {
    private final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

    protected AbstractBucketBuilder() {
    }

    public T addLimit(Bandwidth bandwidth) {
        this.configurationBuilder.addLimit(bandwidth);
        return (T)this;
    }

    protected BucketConfiguration buildConfiguration() {
        return this.configurationBuilder.build();
    }
}

