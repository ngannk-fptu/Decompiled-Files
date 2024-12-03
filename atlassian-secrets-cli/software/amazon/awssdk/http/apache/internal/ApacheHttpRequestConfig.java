/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.apache.internal;

import java.net.InetAddress;
import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.apache.ProxyConfiguration;

@SdkInternalApi
public final class ApacheHttpRequestConfig {
    private final Duration socketTimeout;
    private final Duration connectionTimeout;
    private final Duration connectionAcquireTimeout;
    private final InetAddress localAddress;
    private final boolean expectContinueEnabled;
    private final ProxyConfiguration proxyConfiguration;

    private ApacheHttpRequestConfig(Builder builder) {
        this.socketTimeout = builder.socketTimeout;
        this.connectionTimeout = builder.connectionTimeout;
        this.connectionAcquireTimeout = builder.connectionAcquireTimeout;
        this.localAddress = builder.localAddress;
        this.expectContinueEnabled = builder.expectContinueEnabled;
        this.proxyConfiguration = builder.proxyConfiguration;
    }

    public Duration socketTimeout() {
        return this.socketTimeout;
    }

    public Duration connectionTimeout() {
        return this.connectionTimeout;
    }

    public Duration connectionAcquireTimeout() {
        return this.connectionAcquireTimeout;
    }

    public InetAddress localAddress() {
        return this.localAddress;
    }

    public boolean expectContinueEnabled() {
        return this.expectContinueEnabled;
    }

    public ProxyConfiguration proxyConfiguration() {
        return this.proxyConfiguration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Duration socketTimeout;
        private Duration connectionTimeout;
        private Duration connectionAcquireTimeout;
        private InetAddress localAddress;
        private boolean expectContinueEnabled;
        private ProxyConfiguration proxyConfiguration;

        private Builder() {
        }

        public Builder socketTimeout(Duration socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder connectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder connectionAcquireTimeout(Duration connectionAcquireTimeout) {
            this.connectionAcquireTimeout = connectionAcquireTimeout;
            return this;
        }

        public Builder localAddress(InetAddress localAddress) {
            this.localAddress = localAddress;
            return this;
        }

        public Builder expectContinueEnabled(boolean expectContinueEnabled) {
            this.expectContinueEnabled = expectContinueEnabled;
            return this;
        }

        public Builder proxyConfiguration(ProxyConfiguration proxyConfiguration) {
            this.proxyConfiguration = proxyConfiguration;
            return this;
        }

        public ApacheHttpRequestConfig build() {
            return new ApacheHttpRequestConfig(this);
        }
    }
}

