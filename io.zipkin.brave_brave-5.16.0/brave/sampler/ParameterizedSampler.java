/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.internal.Nullable;
import brave.propagation.SamplingFlags;
import brave.sampler.CountingSampler;
import brave.sampler.Matcher;
import brave.sampler.Sampler;
import brave.sampler.SamplerFunction;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParameterizedSampler<P>
implements SamplerFunction<P> {
    final R<P>[] rules;

    public static <P> Builder<P> newBuilder() {
        return new Builder();
    }

    ParameterizedSampler(Builder<P> builder) {
        this.rules = new R[builder.rules.size()];
        int i = 0;
        for (Map.Entry rule : builder.rules.entrySet()) {
            this.rules[i++] = new R(rule.getKey(), rule.getValue());
        }
    }

    @Override
    @Nullable
    public Boolean trySample(P parameters) {
        if (parameters == null) {
            return null;
        }
        for (R<P> rule : this.rules) {
            if (!rule.matcher.matches(parameters)) continue;
            return rule.sampler.isSampled(0L);
        }
        return null;
    }

    @Deprecated
    public SamplingFlags sample(@Nullable P parameters) {
        return SamplingFlags.Builder.build(this.trySample(parameters));
    }

    @Deprecated
    public static <P> ParameterizedSampler<P> create(List<? extends Rule<P>> rules) {
        if (rules == null) {
            throw new NullPointerException("rules == null");
        }
        Builder<P> builder = ParameterizedSampler.newBuilder();
        for (Rule<P> rule : rules) {
            builder.putRule(rule.matcher, rule.sampler);
        }
        return builder.build();
    }

    @Deprecated
    public static abstract class Rule<P>
    extends R<P>
    implements Matcher<P> {
        protected Rule(float probability) {
            super(null, CountingSampler.create(probability));
        }

        @Override
        public abstract boolean matches(P var1);
    }

    static class R<P> {
        final Matcher<P> matcher;
        final Sampler sampler;

        R(Matcher<P> matcher, Sampler sampler) {
            this.matcher = matcher;
            this.sampler = sampler;
        }
    }

    public static final class Builder<P> {
        final Map<Matcher<P>, Sampler> rules = new LinkedHashMap<Matcher<P>, Sampler>();

        public Builder<P> putAllRules(ParameterizedSampler<P> sampler) {
            if (sampler == null) {
                throw new NullPointerException("sampler == null");
            }
            for (R rule : sampler.rules) {
                this.putRule(rule.matcher, rule.sampler);
            }
            return this;
        }

        public Builder<P> putRule(Matcher<P> matcher, Sampler sampler) {
            if (matcher == null) {
                throw new NullPointerException("matcher == null");
            }
            if (sampler == null) {
                throw new NullPointerException("sampler == null");
            }
            this.rules.put(matcher, sampler);
            return this;
        }

        public ParameterizedSampler<P> build() {
            return new ParameterizedSampler(this);
        }

        Builder() {
        }
    }
}

