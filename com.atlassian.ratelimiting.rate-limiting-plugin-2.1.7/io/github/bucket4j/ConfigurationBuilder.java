/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketExceptions;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationBuilder {
    private List<Bandwidth> bandwidths = new ArrayList<Bandwidth>(1);

    protected ConfigurationBuilder() {
    }

    public BucketConfiguration build() {
        return new BucketConfiguration(this.bandwidths);
    }

    @Deprecated
    public BucketConfiguration buildConfiguration() {
        return this.build();
    }

    public ConfigurationBuilder addLimit(Bandwidth bandwidth) {
        if (bandwidth == null) {
            throw BucketExceptions.nullBandwidth();
        }
        this.bandwidths.add(bandwidth);
        return this;
    }

    public String toString() {
        return "ConfigurationBuilder{, bandwidths=" + this.bandwidths + '}';
    }
}

