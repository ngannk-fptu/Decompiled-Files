/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.sampler.CountingSampler;

public abstract class Sampler {
    public static final Sampler ALWAYS_SAMPLE = new Sampler(){

        @Override
        public boolean isSampled(long traceId) {
            return true;
        }

        public String toString() {
            return "AlwaysSample";
        }
    };
    public static final Sampler NEVER_SAMPLE = new Sampler(){

        @Override
        public boolean isSampled(long traceId) {
            return false;
        }

        public String toString() {
            return "NeverSample";
        }
    };

    public abstract boolean isSampled(long var1);

    public static Sampler create(float probability) {
        return CountingSampler.create(probability);
    }
}

