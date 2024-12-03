/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.internal.Nullable;
import brave.propagation.SamplingFlags;
import brave.sampler.CountingSampler;
import brave.sampler.RateLimitingSampler;
import brave.sampler.Sampler;
import brave.sampler.SamplerFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class DeclarativeSampler<M>
implements SamplerFunction<M> {
    final ConcurrentMap<M, Sampler> methodToSamplers = new ConcurrentHashMap<M, Sampler>();
    static final Sampler NULL_SENTINEL = new Sampler(){

        @Override
        public boolean isSampled(long traceId) {
            throw new AssertionError();
        }
    };

    public static <M> DeclarativeSampler<M> createWithProbability(ProbabilityOfMethod<M> probabilityOfMethod) {
        if (probabilityOfMethod == null) {
            throw new NullPointerException("probabilityOfMethod == null");
        }
        return new DeclarativeCountingSampler<M>(probabilityOfMethod);
    }

    public static <M> DeclarativeSampler<M> createWithRate(RateOfMethod<M> rateOfMethod) {
        if (rateOfMethod == null) {
            throw new NullPointerException("rateOfMethod == null");
        }
        return new DeclarativeRateLimitingSampler<M>(rateOfMethod);
    }

    @Override
    @Nullable
    public Boolean trySample(@Nullable M method) {
        if (method == null) {
            return null;
        }
        Sampler sampler = (Sampler)this.methodToSamplers.get(method);
        if (sampler == NULL_SENTINEL) {
            return null;
        }
        if (sampler != null) {
            return sampler.isSampled(0L);
        }
        sampler = this.samplerOfMethod(method);
        if (sampler == null) {
            this.methodToSamplers.put(method, NULL_SENTINEL);
            return null;
        }
        Sampler previousSampler = this.methodToSamplers.putIfAbsent(method, sampler);
        if (previousSampler != null) {
            sampler = previousSampler;
        }
        return sampler.isSampled(0L);
    }

    @Nullable
    abstract Sampler samplerOfMethod(M var1);

    DeclarativeSampler() {
    }

    public static <M> DeclarativeSampler<M> create(RateForMethod<M> rateForMethod) {
        return DeclarativeSampler.createWithProbability(rateForMethod);
    }

    @Deprecated
    public Sampler toSampler(M method) {
        return this.toSampler(method, Sampler.NEVER_SAMPLE);
    }

    @Deprecated
    public Sampler toSampler(final M method, final Sampler fallback) {
        if (fallback == null) {
            throw new NullPointerException("fallback == null");
        }
        if (method == null) {
            return fallback;
        }
        return new Sampler(){

            @Override
            public boolean isSampled(long traceId) {
                Boolean decision = DeclarativeSampler.this.trySample(method);
                if (decision == null) {
                    return fallback.isSampled(traceId);
                }
                return decision;
            }
        };
    }

    @Deprecated
    public SamplingFlags sample(@Nullable M method) {
        if (method == null) {
            return SamplingFlags.EMPTY;
        }
        return SamplingFlags.Builder.build(this.trySample(method));
    }

    public static interface RateForMethod<M>
    extends ProbabilityOfMethod<M> {
    }

    static final class DeclarativeRateLimitingSampler<M>
    extends DeclarativeSampler<M> {
        final RateOfMethod<M> rateOfMethod;

        DeclarativeRateLimitingSampler(RateOfMethod<M> rateOfMethod) {
            this.rateOfMethod = rateOfMethod;
        }

        @Override
        Sampler samplerOfMethod(M method) {
            Integer rate = this.rateOfMethod.get(method);
            if (rate == null) {
                return null;
            }
            return RateLimitingSampler.create(rate);
        }

        public String toString() {
            return "DeclarativeRateLimitingSampler{" + this.rateOfMethod + "}";
        }
    }

    static final class DeclarativeCountingSampler<M>
    extends DeclarativeSampler<M> {
        final ProbabilityOfMethod<M> probabilityOfMethod;

        DeclarativeCountingSampler(ProbabilityOfMethod<M> probabilityOfMethod) {
            this.probabilityOfMethod = probabilityOfMethod;
        }

        @Override
        Sampler samplerOfMethod(M method) {
            Float probability = this.probabilityOfMethod.get(method);
            if (probability == null) {
                return null;
            }
            return CountingSampler.create(probability.floatValue());
        }

        public String toString() {
            return "DeclarativeCountingSampler{" + this.probabilityOfMethod + "}";
        }
    }

    public static interface RateOfMethod<M> {
        @Nullable
        public Integer get(M var1);
    }

    public static interface ProbabilityOfMethod<M> {
        @Nullable
        public Float get(M var1);
    }
}

