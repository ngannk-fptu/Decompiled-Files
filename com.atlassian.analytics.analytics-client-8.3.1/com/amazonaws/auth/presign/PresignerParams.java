/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.presign;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.SdkClock;
import com.amazonaws.internal.auth.SignerProvider;
import java.net.URI;

@Immutable
@SdkProtectedApi
public class PresignerParams {
    private final URI endpoint;
    private final AWSCredentialsProvider credentialsProvider;
    private final SignerProvider signerProvider;
    private final SdkClock clock;

    public PresignerParams(URI endpoint, AWSCredentialsProvider credentialsProvider, SignerProvider signerProvider, SdkClock clock) {
        this.endpoint = endpoint;
        this.credentialsProvider = credentialsProvider;
        this.signerProvider = signerProvider;
        this.clock = clock;
    }

    public static Builder builder() {
        return new Builder();
    }

    public URI endpoint() {
        return this.endpoint;
    }

    public AWSCredentialsProvider credentialsProvider() {
        return this.credentialsProvider;
    }

    public SignerProvider signerProvider() {
        return this.signerProvider;
    }

    public SdkClock clock() {
        return this.clock;
    }

    public static class Builder {
        private URI endpoint;
        private AWSCredentialsProvider credentialsProvider;
        private SignerProvider signerProvider;
        private SdkClock clock;

        private Builder() {
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder credentialsProvider(AWSCredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        public Builder signerProvider(SignerProvider signerProvider) {
            this.signerProvider = signerProvider;
            return this;
        }

        @SdkTestInternalApi
        public Builder clock(SdkClock clock) {
            this.clock = clock;
            return this;
        }

        public PresignerParams build() {
            return new PresignerParams(this.endpoint, this.credentialsProvider, this.signerProvider, this.resolveClock());
        }

        private SdkClock resolveClock() {
            return this.clock == null ? SdkClock.STANDARD : this.clock;
        }
    }
}

