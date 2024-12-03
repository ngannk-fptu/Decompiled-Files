/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.sampler.Sampler;
import java.util.Random;

public final class BoundarySampler
extends Sampler {
    static final long SALT = new Random().nextLong();
    private final long boundary;

    public static Sampler create(float probability) {
        if (probability == 0.0f) {
            return Sampler.NEVER_SAMPLE;
        }
        if ((double)probability == 1.0) {
            return ALWAYS_SAMPLE;
        }
        if (probability < 1.0E-4f || probability > 1.0f) {
            throw new IllegalArgumentException("probability should be between 0.0001 and 1: was " + probability);
        }
        long boundary = (long)(probability * 10000.0f);
        return new BoundarySampler(boundary);
    }

    BoundarySampler(long boundary) {
        this.boundary = boundary;
    }

    @Override
    public boolean isSampled(long traceId) {
        long t = Math.abs(traceId ^ SALT);
        return t % 10000L <= this.boundary;
    }

    public String toString() {
        return "BoundaryTraceIdSampler(" + this.boundary + ")";
    }
}

