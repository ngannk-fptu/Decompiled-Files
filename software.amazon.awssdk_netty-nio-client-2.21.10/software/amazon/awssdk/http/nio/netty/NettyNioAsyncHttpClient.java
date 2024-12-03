/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.handler.ssl.SslContext
 *  io.netty.handler.ssl.SslProvider
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.http.HttpMetric
 *  software.amazon.awssdk.http.Protocol
 *  software.amazon.awssdk.http.SdkHttpConfigurationOption
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.TlsKeyManagersProvider
 *  software.amazon.awssdk.http.TlsTrustManagersProvider
 *  software.amazon.awssdk.http.async.AsyncExecuteRequest
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient$Builder
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.AttributeMap$Key
 *  software.amazon.awssdk.utils.Either
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.nio.netty;

import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.TlsKeyManagersProvider;
import software.amazon.awssdk.http.TlsTrustManagersProvider;
import software.amazon.awssdk.http.async.AsyncExecuteRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.Http2Configuration;
import software.amazon.awssdk.http.nio.netty.ProxyConfiguration;
import software.amazon.awssdk.http.nio.netty.SdkEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.AwaitCloseChannelPoolMap;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.NettyRequestExecutor;
import software.amazon.awssdk.http.nio.netty.internal.NonManagedEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.RequestContext;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelOptions;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPoolMap;
import software.amazon.awssdk.http.nio.netty.internal.SharedSdkEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.Either;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class NettyNioAsyncHttpClient
implements SdkAsyncHttpClient {
    private static final String CLIENT_NAME = "NettyNio";
    private static final NettyClientLogger log = NettyClientLogger.getLogger(NettyNioAsyncHttpClient.class);
    private static final long MAX_STREAMS_ALLOWED = 0xFFFFFFFFL;
    private static final int DEFAULT_INITIAL_WINDOW_SIZE = 0x100000;
    private static final AttributeMap NETTY_HTTP_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, (Object)Duration.ofSeconds(5L)).build();
    private final SdkEventLoopGroup sdkEventLoopGroup;
    private final SdkChannelPoolMap<URI, ? extends SdkChannelPool> pools;
    private final NettyConfiguration configuration;

    private NettyNioAsyncHttpClient(DefaultBuilder builder, AttributeMap serviceDefaultsMap) {
        this.configuration = new NettyConfiguration(serviceDefaultsMap);
        Protocol protocol = (Protocol)serviceDefaultsMap.get((AttributeMap.Key)SdkHttpConfigurationOption.PROTOCOL);
        this.sdkEventLoopGroup = this.eventLoopGroup(builder);
        Http2Configuration http2Configuration = builder.http2Configuration;
        long maxStreams = this.resolveMaxHttp2Streams(builder.maxHttp2Streams, http2Configuration);
        int initialWindowSize = this.resolveInitialWindowSize(http2Configuration);
        this.pools = AwaitCloseChannelPoolMap.builder().sdkChannelOptions(builder.sdkChannelOptions).configuration(this.configuration).protocol(protocol).maxStreams(maxStreams).initialWindowSize(initialWindowSize).healthCheckPingPeriod(this.resolveHealthCheckPingPeriod(http2Configuration)).sdkEventLoopGroup(this.sdkEventLoopGroup).sslProvider(this.resolveSslProvider(builder)).proxyConfiguration(builder.proxyConfiguration).useNonBlockingDnsResolver(builder.useNonBlockingDnsResolver).build();
    }

    @SdkTestInternalApi
    NettyNioAsyncHttpClient(SdkEventLoopGroup sdkEventLoopGroup, SdkChannelPoolMap<URI, ? extends SdkChannelPool> pools, NettyConfiguration configuration) {
        this.sdkEventLoopGroup = sdkEventLoopGroup;
        this.pools = pools;
        this.configuration = configuration;
    }

    public CompletableFuture<Void> execute(AsyncExecuteRequest request) {
        RequestContext ctx = this.createRequestContext(request);
        ctx.metricCollector().reportMetric(HttpMetric.HTTP_CLIENT_NAME, (Object)this.clientName());
        return new NettyRequestExecutor(ctx).execute();
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public static SdkAsyncHttpClient create() {
        return new DefaultBuilder().build();
    }

    private RequestContext createRequestContext(AsyncExecuteRequest request) {
        SdkChannelPool pool = this.pools.get(NettyNioAsyncHttpClient.poolKey(request.request()));
        return new RequestContext(pool, this.sdkEventLoopGroup.eventLoopGroup(), request, this.configuration);
    }

    private SdkEventLoopGroup eventLoopGroup(DefaultBuilder builder) {
        Validate.isTrue((builder.eventLoopGroup == null || builder.eventLoopGroupBuilder == null ? 1 : 0) != 0, (String)"The eventLoopGroup and the eventLoopGroupFactory can't both be configured.", (Object[])new Object[0]);
        return Either.fromNullable((Object)builder.eventLoopGroup, (Object)builder.eventLoopGroupBuilder).map(e -> (SdkEventLoopGroup)e.map(this::nonManagedEventLoopGroup, SdkEventLoopGroup.Builder::build)).orElseGet(SharedSdkEventLoopGroup::get);
    }

    private static URI poolKey(SdkHttpRequest sdkRequest) {
        return (URI)FunctionalUtils.invokeSafely(() -> new URI(sdkRequest.protocol(), null, sdkRequest.host(), sdkRequest.port(), null, null, null));
    }

    private SslProvider resolveSslProvider(DefaultBuilder builder) {
        if (builder.sslProvider != null) {
            return builder.sslProvider;
        }
        return SslContext.defaultClientProvider();
    }

    private long resolveMaxHttp2Streams(Integer topLevelValue, Http2Configuration http2Configuration) {
        if (topLevelValue != null) {
            return topLevelValue.intValue();
        }
        if (http2Configuration == null || http2Configuration.maxStreams() == null) {
            return 0xFFFFFFFFL;
        }
        return Math.min(http2Configuration.maxStreams(), 0xFFFFFFFFL);
    }

    private int resolveInitialWindowSize(Http2Configuration http2Configuration) {
        if (http2Configuration == null || http2Configuration.initialWindowSize() == null) {
            return 0x100000;
        }
        return http2Configuration.initialWindowSize();
    }

    private Duration resolveHealthCheckPingPeriod(Http2Configuration http2Configuration) {
        if (http2Configuration != null) {
            return http2Configuration.healthCheckPingPeriod();
        }
        return null;
    }

    private SdkEventLoopGroup nonManagedEventLoopGroup(SdkEventLoopGroup eventLoopGroup) {
        return SdkEventLoopGroup.create(new NonManagedEventLoopGroup(eventLoopGroup.eventLoopGroup()), eventLoopGroup.channelFactory());
    }

    public void close() {
        NettyUtils.runAndLogError(log, "Unable to close channel pools", this.pools::close);
        NettyUtils.runAndLogError(log, "Unable to shutdown event loop", () -> this.closeEventLoopUninterruptibly(this.sdkEventLoopGroup.eventLoopGroup()));
    }

    private void closeEventLoopUninterruptibly(EventLoopGroup eventLoopGroup) throws ExecutionException {
        try {
            eventLoopGroup.shutdownGracefully(2L, 15L, TimeUnit.SECONDS).get(16L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (TimeoutException e) {
            log.error(null, () -> String.format("Shutting down Netty EventLoopGroup did not complete within %s seconds", 16));
        }
    }

    public String clientName() {
        return CLIENT_NAME;
    }

    @SdkTestInternalApi
    NettyConfiguration configuration() {
        return this.configuration;
    }

    private static final class DefaultBuilder
    implements Builder {
        private final AttributeMap.Builder standardOptions = AttributeMap.builder();
        private SdkChannelOptions sdkChannelOptions = new SdkChannelOptions();
        private SdkEventLoopGroup eventLoopGroup;
        private SdkEventLoopGroup.Builder eventLoopGroupBuilder;
        private Integer maxHttp2Streams;
        private Http2Configuration http2Configuration;
        private SslProvider sslProvider;
        private ProxyConfiguration proxyConfiguration;
        private Boolean useNonBlockingDnsResolver;

        private DefaultBuilder() {
        }

        @Override
        public Builder maxConcurrency(Integer maxConcurrency) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.MAX_CONNECTIONS, (Object)maxConcurrency);
            return this;
        }

        public void setMaxConcurrency(Integer maxConnectionsPerEndpoint) {
            this.maxConcurrency(maxConnectionsPerEndpoint);
        }

        @Override
        public Builder maxPendingConnectionAcquires(Integer maxPendingAcquires) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.MAX_PENDING_CONNECTION_ACQUIRES, (Object)maxPendingAcquires);
            return this;
        }

        public void setMaxPendingConnectionAcquires(Integer maxPendingAcquires) {
            this.maxPendingConnectionAcquires(maxPendingAcquires);
        }

        @Override
        public Builder readTimeout(Duration readTimeout) {
            Validate.isNotNegative((Duration)readTimeout, (String)"readTimeout");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.READ_TIMEOUT, (Object)readTimeout);
            return this;
        }

        public void setReadTimeout(Duration readTimeout) {
            this.readTimeout(readTimeout);
        }

        @Override
        public Builder writeTimeout(Duration writeTimeout) {
            Validate.isNotNegative((Duration)writeTimeout, (String)"writeTimeout");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.WRITE_TIMEOUT, (Object)writeTimeout);
            return this;
        }

        public void setWriteTimeout(Duration writeTimeout) {
            this.writeTimeout(writeTimeout);
        }

        @Override
        public Builder connectionTimeout(Duration timeout) {
            Validate.isPositive((Duration)timeout, (String)"connectionTimeout");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT, (Object)timeout);
            return this;
        }

        public void setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout(connectionTimeout);
        }

        @Override
        public Builder connectionAcquisitionTimeout(Duration connectionAcquisitionTimeout) {
            Validate.isPositive((Duration)connectionAcquisitionTimeout, (String)"connectionAcquisitionTimeout");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_ACQUIRE_TIMEOUT, (Object)connectionAcquisitionTimeout);
            return this;
        }

        public void setConnectionAcquisitionTimeout(Duration connectionAcquisitionTimeout) {
            this.connectionAcquisitionTimeout(connectionAcquisitionTimeout);
        }

        @Override
        public Builder connectionTimeToLive(Duration connectionTimeToLive) {
            Validate.isNotNegative((Duration)connectionTimeToLive, (String)"connectionTimeToLive");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIME_TO_LIVE, (Object)connectionTimeToLive);
            return this;
        }

        public void setConnectionTimeToLive(Duration connectionTimeToLive) {
            this.connectionTimeToLive(connectionTimeToLive);
        }

        @Override
        public Builder connectionMaxIdleTime(Duration connectionMaxIdleTime) {
            Validate.isPositive((Duration)connectionMaxIdleTime, (String)"connectionMaxIdleTime");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, (Object)connectionMaxIdleTime);
            return this;
        }

        public void setConnectionMaxIdleTime(Duration connectionMaxIdleTime) {
            this.connectionMaxIdleTime(connectionMaxIdleTime);
        }

        @Override
        public Builder useIdleConnectionReaper(Boolean useIdleConnectionReaper) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.REAP_IDLE_CONNECTIONS, (Object)useIdleConnectionReaper);
            return this;
        }

        public void setUseIdleConnectionReaper(Boolean useIdleConnectionReaper) {
            this.useIdleConnectionReaper(useIdleConnectionReaper);
        }

        @Override
        public Builder tlsNegotiationTimeout(Duration tlsNegotiationTimeout) {
            Validate.isPositive((Duration)tlsNegotiationTimeout, (String)"tlsNegotiationTimeout");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT, (Object)tlsNegotiationTimeout);
            return this;
        }

        public void setTlsNegotiationTimeout(Duration tlsNegotiationTimeout) {
            this.tlsNegotiationTimeout(tlsNegotiationTimeout);
        }

        @Override
        public Builder eventLoopGroup(SdkEventLoopGroup eventLoopGroup) {
            this.eventLoopGroup = eventLoopGroup;
            return this;
        }

        public void setEventLoopGroup(SdkEventLoopGroup eventLoopGroup) {
            this.eventLoopGroup(eventLoopGroup);
        }

        @Override
        public Builder eventLoopGroupBuilder(SdkEventLoopGroup.Builder eventLoopGroupBuilder) {
            this.eventLoopGroupBuilder = eventLoopGroupBuilder;
            return this;
        }

        public void setEventLoopGroupBuilder(SdkEventLoopGroup.Builder eventLoopGroupBuilder) {
            this.eventLoopGroupBuilder(eventLoopGroupBuilder);
        }

        @Override
        public Builder protocol(Protocol protocol) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.PROTOCOL, (Object)protocol);
            return this;
        }

        public void setProtocol(Protocol protocol) {
            this.protocol(protocol);
        }

        @Override
        public Builder tcpKeepAlive(Boolean keepConnectionAlive) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TCP_KEEPALIVE, (Object)keepConnectionAlive);
            return this;
        }

        public void setTcpKeepAlive(Boolean keepConnectionAlive) {
            this.tcpKeepAlive(keepConnectionAlive);
        }

        @Override
        public Builder putChannelOption(ChannelOption channelOption, Object value) {
            this.sdkChannelOptions.putOption(channelOption, value);
            return this;
        }

        @Override
        public Builder maxHttp2Streams(Integer maxHttp2Streams) {
            this.maxHttp2Streams = maxHttp2Streams;
            return this;
        }

        public void setMaxHttp2Streams(Integer maxHttp2Streams) {
            this.maxHttp2Streams(maxHttp2Streams);
        }

        @Override
        public Builder sslProvider(SslProvider sslProvider) {
            this.sslProvider = sslProvider;
            return this;
        }

        public void setSslProvider(SslProvider sslProvider) {
            this.sslProvider(sslProvider);
        }

        @Override
        public Builder proxyConfiguration(ProxyConfiguration proxyConfiguration) {
            this.proxyConfiguration = proxyConfiguration;
            return this;
        }

        public void setProxyConfiguration(ProxyConfiguration proxyConfiguration) {
            this.proxyConfiguration(proxyConfiguration);
        }

        @Override
        public Builder tlsKeyManagersProvider(TlsKeyManagersProvider tlsKeyManagersProvider) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_KEY_MANAGERS_PROVIDER, (Object)tlsKeyManagersProvider);
            return this;
        }

        public void setTlsKeyManagersProvider(TlsKeyManagersProvider tlsKeyManagersProvider) {
            this.tlsKeyManagersProvider(tlsKeyManagersProvider);
        }

        @Override
        public Builder tlsTrustManagersProvider(TlsTrustManagersProvider tlsTrustManagersProvider) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_TRUST_MANAGERS_PROVIDER, (Object)tlsTrustManagersProvider);
            return this;
        }

        public void setTlsTrustManagersProvider(TlsTrustManagersProvider tlsTrustManagersProvider) {
            this.tlsTrustManagersProvider(tlsTrustManagersProvider);
        }

        @Override
        public Builder http2Configuration(Http2Configuration http2Configuration) {
            this.http2Configuration = http2Configuration;
            return this;
        }

        @Override
        public Builder http2Configuration(Consumer<Http2Configuration.Builder> http2ConfigurationBuilderConsumer) {
            Http2Configuration.Builder builder = Http2Configuration.builder();
            http2ConfigurationBuilderConsumer.accept(builder);
            return this.http2Configuration((Http2Configuration)builder.build());
        }

        public void setHttp2Configuration(Http2Configuration http2Configuration) {
            this.http2Configuration(http2Configuration);
        }

        @Override
        public Builder useNonBlockingDnsResolver(Boolean useNonBlockingDnsResolver) {
            this.useNonBlockingDnsResolver = useNonBlockingDnsResolver;
            return this;
        }

        public void setUseNonBlockingDnsResolver(Boolean useNonBlockingDnsResolver) {
            this.useNonBlockingDnsResolver(useNonBlockingDnsResolver);
        }

        public SdkAsyncHttpClient buildWithDefaults(AttributeMap serviceDefaults) {
            if (this.standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT) == null) {
                this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT, this.standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT));
            }
            return new NettyNioAsyncHttpClient(this, this.standardOptions.build().merge(serviceDefaults).merge(NETTY_HTTP_DEFAULTS).merge(SdkHttpConfigurationOption.GLOBAL_HTTP_DEFAULTS));
        }
    }

    public static interface Builder
    extends SdkAsyncHttpClient.Builder<Builder> {
        public Builder maxConcurrency(Integer var1);

        public Builder maxPendingConnectionAcquires(Integer var1);

        public Builder readTimeout(Duration var1);

        public Builder writeTimeout(Duration var1);

        public Builder connectionTimeout(Duration var1);

        public Builder connectionAcquisitionTimeout(Duration var1);

        public Builder connectionTimeToLive(Duration var1);

        public Builder connectionMaxIdleTime(Duration var1);

        public Builder tlsNegotiationTimeout(Duration var1);

        public Builder useIdleConnectionReaper(Boolean var1);

        public Builder eventLoopGroup(SdkEventLoopGroup var1);

        public Builder eventLoopGroupBuilder(SdkEventLoopGroup.Builder var1);

        public Builder protocol(Protocol var1);

        public Builder tcpKeepAlive(Boolean var1);

        public Builder putChannelOption(ChannelOption var1, Object var2);

        public Builder maxHttp2Streams(Integer var1);

        public Builder sslProvider(SslProvider var1);

        public Builder proxyConfiguration(ProxyConfiguration var1);

        public Builder tlsKeyManagersProvider(TlsKeyManagersProvider var1);

        public Builder tlsTrustManagersProvider(TlsTrustManagersProvider var1);

        public Builder http2Configuration(Http2Configuration var1);

        public Builder http2Configuration(Consumer<Http2Configuration.Builder> var1);

        public Builder useNonBlockingDnsResolver(Boolean var1);
    }
}

