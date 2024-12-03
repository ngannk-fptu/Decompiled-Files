/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.TlsKeyManagersProvider;
import software.amazon.awssdk.http.TlsTrustManagersProvider;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.NumericUtils;

@SdkInternalApi
public final class NettyConfiguration {
    public static final int CHANNEL_POOL_CLOSE_TIMEOUT_SECONDS = 5;
    public static final int EVENTLOOP_SHUTDOWN_QUIET_PERIOD_SECONDS = 2;
    public static final int EVENTLOOP_SHUTDOWN_TIMEOUT_SECONDS = 15;
    public static final int EVENTLOOP_SHUTDOWN_FUTURE_TIMEOUT_SECONDS = 16;
    public static final int HTTP2_CONNECTION_PING_TIMEOUT_SECONDS = 5;
    private final AttributeMap configuration;

    public NettyConfiguration(AttributeMap configuration) {
        this.configuration = configuration;
    }

    public <T> T attribute(AttributeMap.Key<T> key) {
        return this.configuration.get(key);
    }

    public int connectTimeoutMillis() {
        return NumericUtils.saturatedCast(this.configuration.get(SdkHttpConfigurationOption.CONNECTION_TIMEOUT).toMillis());
    }

    public int connectionAcquireTimeoutMillis() {
        return NumericUtils.saturatedCast(this.configuration.get(SdkHttpConfigurationOption.CONNECTION_ACQUIRE_TIMEOUT).toMillis());
    }

    public int maxConnections() {
        return this.configuration.get(SdkHttpConfigurationOption.MAX_CONNECTIONS);
    }

    public int maxPendingConnectionAcquires() {
        return this.configuration.get(SdkHttpConfigurationOption.MAX_PENDING_CONNECTION_ACQUIRES);
    }

    public int readTimeoutMillis() {
        return NumericUtils.saturatedCast(this.configuration.get(SdkHttpConfigurationOption.READ_TIMEOUT).toMillis());
    }

    public int writeTimeoutMillis() {
        return NumericUtils.saturatedCast(this.configuration.get(SdkHttpConfigurationOption.WRITE_TIMEOUT).toMillis());
    }

    public int idleTimeoutMillis() {
        return NumericUtils.saturatedCast(this.configuration.get(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT).toMillis());
    }

    public int connectionTtlMillis() {
        return NumericUtils.saturatedCast(this.configuration.get(SdkHttpConfigurationOption.CONNECTION_TIME_TO_LIVE).toMillis());
    }

    public boolean reapIdleConnections() {
        return this.configuration.get(SdkHttpConfigurationOption.REAP_IDLE_CONNECTIONS);
    }

    public TlsKeyManagersProvider tlsKeyManagersProvider() {
        return this.configuration.get(SdkHttpConfigurationOption.TLS_KEY_MANAGERS_PROVIDER);
    }

    public TlsTrustManagersProvider tlsTrustManagersProvider() {
        return this.configuration.get(SdkHttpConfigurationOption.TLS_TRUST_MANAGERS_PROVIDER);
    }

    public boolean trustAllCertificates() {
        return this.configuration.get(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES);
    }

    public boolean tcpKeepAlive() {
        return this.configuration.get(SdkHttpConfigurationOption.TCP_KEEPALIVE);
    }

    public Duration tlsHandshakeTimeout() {
        return this.configuration.get(SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT);
    }
}

