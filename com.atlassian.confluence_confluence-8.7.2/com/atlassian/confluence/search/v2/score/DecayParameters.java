/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.search.v2.score;

import com.google.common.base.Preconditions;

public final class DecayParameters {
    private final String origin;
    private final double scale;
    private final double decay;
    private final double offset;
    private final String unit;

    private DecayParameters(Builder builder) {
        this.origin = builder.origin;
        this.scale = builder.scale;
        this.decay = builder.decay;
        this.offset = builder.offset;
        this.unit = builder.unit;
    }

    @Deprecated
    public double getOrigin() {
        return Double.parseDouble(this.origin);
    }

    public String getOriginAsString() {
        return this.origin;
    }

    public double getScale() {
        return this.scale;
    }

    public double getDecay() {
        return this.decay;
    }

    public double getOffset() {
        return this.offset;
    }

    public String getUnit() {
        return this.unit;
    }

    public static Builder builder(double origin, double scale) {
        return new Builder(String.valueOf(origin), scale);
    }

    public static Builder builder(String origin, double scale) {
        return new Builder(origin, scale);
    }

    public static final class Builder {
        private final String origin;
        private final double scale;
        private double decay;
        private double offset;
        private String unit;

        private Builder(String origin, double scale) {
            this.origin = origin;
            this.scale = scale;
            this.decay = 0.5;
            this.offset = 0.0;
        }

        public Builder decay(double decay) {
            Preconditions.checkArgument((decay > 0.0 && decay < 1.0 ? 1 : 0) != 0);
            this.decay = decay;
            return this;
        }

        public Builder offset(double offset) {
            this.offset = offset;
            return this;
        }

        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public DecayParameters build() {
            return new DecayParameters(this);
        }
    }
}

