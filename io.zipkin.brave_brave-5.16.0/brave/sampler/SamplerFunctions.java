/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.internal.Nullable;
import brave.sampler.SamplerFunction;

public final class SamplerFunctions {
    public static <T> SamplerFunction<T> nullSafe(SamplerFunction<T> delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }
        if (delegate instanceof Constants || delegate instanceof NullSafe) {
            return delegate;
        }
        return new NullSafe<T>(delegate);
    }

    public static <T> SamplerFunction<T> deferDecision() {
        return Constants.DEFER_DECISION;
    }

    public static <T> SamplerFunction<T> neverSample() {
        return Constants.NEVER_SAMPLE;
    }

    static enum Constants implements SamplerFunction<Object>
    {
        DEFER_DECISION{

            @Override
            @Nullable
            public Boolean trySample(Object request) {
                return null;
            }

            public String toString() {
                return "DeferDecision";
            }
        }
        ,
        NEVER_SAMPLE{

            @Override
            @Nullable
            public Boolean trySample(Object request) {
                return false;
            }

            public String toString() {
                return "NeverSample";
            }
        };

    }

    static final class NullSafe<T>
    implements SamplerFunction<T> {
        final SamplerFunction<T> delegate;

        NullSafe(SamplerFunction<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Boolean trySample(T arg) {
            if (arg == null) {
                return null;
            }
            return this.delegate.trySample(arg);
        }

        public String toString() {
            return "NullSafe(" + this.delegate + ")";
        }
    }
}

