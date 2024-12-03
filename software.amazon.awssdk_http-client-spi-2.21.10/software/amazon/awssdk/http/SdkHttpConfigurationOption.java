/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Key
 */
package software.amazon.awssdk.http;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SystemPropertyTlsKeyManagersProvider;
import software.amazon.awssdk.http.TlsKeyManagersProvider;
import software.amazon.awssdk.http.TlsTrustManagersProvider;
import software.amazon.awssdk.utils.AttributeMap;

@SdkProtectedApi
public final class SdkHttpConfigurationOption<T>
extends AttributeMap.Key<T> {
    public static final SdkHttpConfigurationOption<Duration> READ_TIMEOUT = new SdkHttpConfigurationOption<Duration>("ReadTimeout", Duration.class);
    public static final SdkHttpConfigurationOption<Duration> WRITE_TIMEOUT = new SdkHttpConfigurationOption<Duration>("WriteTimeout", Duration.class);
    public static final SdkHttpConfigurationOption<Duration> CONNECTION_TIMEOUT = new SdkHttpConfigurationOption<Duration>("ConnectionTimeout", Duration.class);
    public static final SdkHttpConfigurationOption<Duration> CONNECTION_ACQUIRE_TIMEOUT = new SdkHttpConfigurationOption<Duration>("ConnectionAcquireTimeout", Duration.class);
    public static final SdkHttpConfigurationOption<Duration> CONNECTION_MAX_IDLE_TIMEOUT = new SdkHttpConfigurationOption<Duration>("ConnectionMaxIdleTimeout", Duration.class);
    public static final SdkHttpConfigurationOption<Duration> CONNECTION_TIME_TO_LIVE = new SdkHttpConfigurationOption<Duration>("ConnectionTimeToLive", Duration.class);
    public static final SdkHttpConfigurationOption<Integer> MAX_CONNECTIONS = new SdkHttpConfigurationOption<Integer>("MaxConnections", Integer.class);
    public static final SdkHttpConfigurationOption<Protocol> PROTOCOL = new SdkHttpConfigurationOption<Protocol>("Protocol", Protocol.class);
    public static final SdkHttpConfigurationOption<Integer> MAX_PENDING_CONNECTION_ACQUIRES = new SdkHttpConfigurationOption<Integer>("MaxConnectionAcquires", Integer.class);
    public static final SdkHttpConfigurationOption<Boolean> REAP_IDLE_CONNECTIONS = new SdkHttpConfigurationOption<Boolean>("ReapIdleConnections", Boolean.class);
    public static final SdkHttpConfigurationOption<Boolean> TCP_KEEPALIVE = new SdkHttpConfigurationOption<Boolean>("TcpKeepalive", Boolean.class);
    public static final SdkHttpConfigurationOption<TlsKeyManagersProvider> TLS_KEY_MANAGERS_PROVIDER = new SdkHttpConfigurationOption<TlsKeyManagersProvider>("TlsKeyManagersProvider", TlsKeyManagersProvider.class);
    public static final SdkHttpConfigurationOption<Boolean> TRUST_ALL_CERTIFICATES = new SdkHttpConfigurationOption<Boolean>("TrustAllCertificates", Boolean.class);
    public static final SdkHttpConfigurationOption<TlsTrustManagersProvider> TLS_TRUST_MANAGERS_PROVIDER = new SdkHttpConfigurationOption<TlsTrustManagersProvider>("TlsTrustManagersProvider", TlsTrustManagersProvider.class);
    public static final SdkHttpConfigurationOption<Duration> TLS_NEGOTIATION_TIMEOUT = new SdkHttpConfigurationOption<Duration>("TlsNegotiationTimeout", Duration.class);
    private static final Duration DEFAULT_SOCKET_READ_TIMEOUT = Duration.ofSeconds(30L);
    private static final Duration DEFAULT_SOCKET_WRITE_TIMEOUT = Duration.ofSeconds(30L);
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(2L);
    private static final Duration DEFAULT_CONNECTION_ACQUIRE_TIMEOUT = Duration.ofSeconds(10L);
    private static final Duration DEFAULT_CONNECTION_MAX_IDLE_TIMEOUT = Duration.ofSeconds(60L);
    private static final Duration DEFAULT_CONNECTION_TIME_TO_LIVE = Duration.ZERO;
    private static final Duration DEFAULT_TLS_NEGOTIATION_TIMEOUT = Duration.ofSeconds(5L);
    private static final Boolean DEFAULT_REAP_IDLE_CONNECTIONS = Boolean.TRUE;
    private static final int DEFAULT_MAX_CONNECTIONS = 50;
    private static final int DEFAULT_MAX_CONNECTION_ACQUIRES = 10000;
    private static final Boolean DEFAULT_TCP_KEEPALIVE = Boolean.FALSE;
    private static final Boolean DEFAULT_TRUST_ALL_CERTIFICATES = Boolean.FALSE;
    private static final Protocol DEFAULT_PROTOCOL = Protocol.HTTP1_1;
    private static final TlsTrustManagersProvider DEFAULT_TLS_TRUST_MANAGERS_PROVIDER = null;
    private static final TlsKeyManagersProvider DEFAULT_TLS_KEY_MANAGERS_PROVIDER = SystemPropertyTlsKeyManagersProvider.create();
    public static final AttributeMap GLOBAL_HTTP_DEFAULTS = AttributeMap.builder().put(READ_TIMEOUT, (Object)DEFAULT_SOCKET_READ_TIMEOUT).put(WRITE_TIMEOUT, (Object)DEFAULT_SOCKET_WRITE_TIMEOUT).put(CONNECTION_TIMEOUT, (Object)DEFAULT_CONNECTION_TIMEOUT).put(CONNECTION_ACQUIRE_TIMEOUT, (Object)DEFAULT_CONNECTION_ACQUIRE_TIMEOUT).put(CONNECTION_MAX_IDLE_TIMEOUT, (Object)DEFAULT_CONNECTION_MAX_IDLE_TIMEOUT).put(CONNECTION_TIME_TO_LIVE, (Object)DEFAULT_CONNECTION_TIME_TO_LIVE).put(MAX_CONNECTIONS, (Object)50).put(MAX_PENDING_CONNECTION_ACQUIRES, (Object)10000).put(PROTOCOL, (Object)DEFAULT_PROTOCOL).put(TRUST_ALL_CERTIFICATES, (Object)DEFAULT_TRUST_ALL_CERTIFICATES).put(REAP_IDLE_CONNECTIONS, (Object)DEFAULT_REAP_IDLE_CONNECTIONS).put(TCP_KEEPALIVE, (Object)DEFAULT_TCP_KEEPALIVE).put(TLS_KEY_MANAGERS_PROVIDER, (Object)DEFAULT_TLS_KEY_MANAGERS_PROVIDER).put(TLS_TRUST_MANAGERS_PROVIDER, (Object)DEFAULT_TLS_TRUST_MANAGERS_PROVIDER).put(TLS_NEGOTIATION_TIMEOUT, (Object)DEFAULT_TLS_NEGOTIATION_TIMEOUT).build();
    private final String name;

    private SdkHttpConfigurationOption(String name, Class<T> clzz) {
        super(clzz);
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}

