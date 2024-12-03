/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketExceptions;
import io.github.bucket4j.IncompatibleConfigurationException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class BucketConfiguration
implements Serializable {
    private static final long serialVersionUID = 42L;
    private final Bandwidth[] bandwidths;

    public BucketConfiguration(List<Bandwidth> bandwidths) {
        Objects.requireNonNull(bandwidths);
        if (bandwidths.isEmpty()) {
            throw BucketExceptions.restrictionsNotSpecified();
        }
        this.bandwidths = new Bandwidth[bandwidths.size()];
        for (int i = 0; i < bandwidths.size(); ++i) {
            this.bandwidths[i] = Objects.requireNonNull(bandwidths.get(i));
        }
    }

    public Bandwidth[] getBandwidths() {
        return this.bandwidths;
    }

    public String toString() {
        return "BucketConfiguration{bandwidths=" + Arrays.toString(this.bandwidths) + '}';
    }

    public void checkCompatibility(BucketConfiguration newConfiguration) {
        if (!this.isCompatible(newConfiguration)) {
            throw new IncompatibleConfigurationException(this, newConfiguration);
        }
    }

    public boolean isCompatible(BucketConfiguration newConfiguration) {
        return this.bandwidths.length == newConfiguration.bandwidths.length;
    }
}

