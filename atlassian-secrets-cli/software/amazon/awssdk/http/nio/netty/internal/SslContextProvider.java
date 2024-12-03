/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SystemPropertyTlsKeyManagersProvider;
import software.amazon.awssdk.http.TlsTrustManagersProvider;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.StaticKeyManagerFactory;
import software.amazon.awssdk.http.nio.netty.internal.StaticTrustManagerFactory;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class SslContextProvider {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(SslContextProvider.class);
    private final Protocol protocol;
    private final SslProvider sslProvider;
    private final TrustManagerFactory trustManagerFactory;
    private final KeyManagerFactory keyManagerFactory;

    public SslContextProvider(NettyConfiguration configuration, Protocol protocol, SslProvider sslProvider) {
        this.protocol = protocol;
        this.sslProvider = sslProvider;
        this.trustManagerFactory = this.getTrustManager(configuration);
        this.keyManagerFactory = this.getKeyManager(configuration);
    }

    public SslContext sslContext() {
        try {
            return SslContextBuilder.forClient().sslProvider(this.sslProvider).ciphers(this.getCiphers(), SupportedCipherSuiteFilter.INSTANCE).trustManager(this.trustManagerFactory).keyManager(this.keyManagerFactory).build();
        }
        catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getCiphers() {
        return this.protocol == Protocol.HTTP2 ? Http2SecurityUtil.CIPHERS : null;
    }

    private TrustManagerFactory getTrustManager(NettyConfiguration configuration) {
        TlsTrustManagersProvider tlsTrustManagersProvider = configuration.tlsTrustManagersProvider();
        Validate.isTrue(tlsTrustManagersProvider == null || !configuration.trustAllCertificates(), "A TlsTrustManagerProvider can't be provided if TrustAllCertificates is also set", new Object[0]);
        if (tlsTrustManagersProvider != null) {
            return StaticTrustManagerFactory.create(tlsTrustManagersProvider.trustManagers());
        }
        if (configuration.trustAllCertificates()) {
            log.warn(null, () -> "SSL Certificate verification is disabled. This is not a safe setting and should only be used for testing.");
            return InsecureTrustManagerFactory.INSTANCE;
        }
        return null;
    }

    private KeyManagerFactory getKeyManager(NettyConfiguration configuration) {
        KeyManager[] keyManagers;
        if (configuration.tlsKeyManagersProvider() != null && (keyManagers = configuration.tlsKeyManagersProvider().keyManagers()) != null) {
            return StaticKeyManagerFactory.create(keyManagers);
        }
        KeyManager[] systemPropertyKeyManagers = SystemPropertyTlsKeyManagersProvider.create().keyManagers();
        return systemPropertyKeyManagers == null ? null : StaticKeyManagerFactory.create(systemPropertyKeyManagers);
    }
}

