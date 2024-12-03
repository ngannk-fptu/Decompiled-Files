/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.internal.Nullable
 *  brave.sampler.CountingSampler
 *  brave.sampler.Matcher
 *  brave.sampler.Matchers
 *  brave.sampler.ParameterizedSampler
 *  brave.sampler.ParameterizedSampler$Builder
 *  brave.sampler.RateLimitingSampler
 *  brave.sampler.Sampler
 *  brave.sampler.SamplerFunction
 */
package brave.http;

import brave.http.HttpAdapter;
import brave.http.HttpRequest;
import brave.http.HttpRequestMatchers;
import brave.http.HttpSampler;
import brave.internal.Nullable;
import brave.sampler.CountingSampler;
import brave.sampler.Matcher;
import brave.sampler.Matchers;
import brave.sampler.ParameterizedSampler;
import brave.sampler.RateLimitingSampler;
import brave.sampler.Sampler;
import brave.sampler.SamplerFunction;

public final class HttpRuleSampler
extends HttpSampler
implements SamplerFunction<HttpRequest> {
    final ParameterizedSampler<HttpRequest> delegate;

    public static Builder newBuilder() {
        return new Builder();
    }

    HttpRuleSampler(ParameterizedSampler<HttpRequest> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Boolean trySample(HttpRequest request) {
        return this.delegate.trySample((Object)request);
    }

    @Override
    @Deprecated
    public <Req> Boolean trySample(HttpAdapter<Req, ?> adapter, Req request) {
        if (request == null) {
            return null;
        }
        return this.trySample(new HttpSampler.FromHttpAdapter<Req>(adapter, request));
    }

    public static final class Builder {
        final ParameterizedSampler.Builder<HttpRequest> delegate = ParameterizedSampler.newBuilder();

        @Deprecated
        public Builder addRule(@Nullable String method, String path, float probability) {
            if (path == null) {
                throw new NullPointerException("path == null");
            }
            Sampler sampler = CountingSampler.create((float)probability);
            if (method == null) {
                this.delegate.putRule(HttpRequestMatchers.pathStartsWith(path), RateLimitingSampler.create((int)10));
                return this;
            }
            this.delegate.putRule(Matchers.and((Matcher[])new Matcher[]{HttpRequestMatchers.methodEquals(method), HttpRequestMatchers.pathStartsWith(path)}), sampler);
            return this;
        }

        public Builder putAllRules(HttpRuleSampler sampler) {
            if (sampler == null) {
                throw new NullPointerException("sampler == null");
            }
            this.delegate.putAllRules(sampler.delegate);
            return this;
        }

        public Builder putRule(Matcher<HttpRequest> matcher, Sampler sampler) {
            this.delegate.putRule(matcher, sampler);
            return this;
        }

        public HttpRuleSampler build() {
            return new HttpRuleSampler((ParameterizedSampler<HttpRequest>)this.delegate.build());
        }

        Builder() {
        }
    }
}

