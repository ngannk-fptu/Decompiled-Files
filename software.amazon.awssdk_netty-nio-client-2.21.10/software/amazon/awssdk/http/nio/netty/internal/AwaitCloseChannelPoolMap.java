/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.pool.ChannelPool
 *  io.netty.channel.pool.ChannelPoolHandler
 *  io.netty.handler.ssl.SslContext
 *  io.netty.handler.ssl.SslProvider
 *  io.netty.util.concurrent.EventExecutor
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.http.Protocol
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.EventExecutor;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.ProxyConfiguration;
import software.amazon.awssdk.http.nio.netty.SdkEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.BetterSimpleChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.BootstrapProvider;
import software.amazon.awssdk.http.nio.netty.internal.CancellableAcquireChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.ChannelPipelineInitializer;
import software.amazon.awssdk.http.nio.netty.internal.HandlerRemovingChannelPoolListener;
import software.amazon.awssdk.http.nio.netty.internal.HealthCheckedChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.HonorCloseOnReleaseChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.Http1TunnelConnectionPool;
import software.amazon.awssdk.http.nio.netty.internal.InUseTrackingChannelPoolListener;
import software.amazon.awssdk.http.nio.netty.internal.ListenerInvokingChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.ReleaseOnceChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelOptions;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPoolMap;
import software.amazon.awssdk.http.nio.netty.internal.SimpleChannelPoolAwareChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.SslContextProvider;
import software.amazon.awssdk.http.nio.netty.internal.http2.HttpOrHttp2ChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@SdkInternalApi
public final class AwaitCloseChannelPoolMap
extends SdkChannelPoolMap<URI, SimpleChannelPoolAwareChannelPool> {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(AwaitCloseChannelPoolMap.class);
    private static final ChannelPoolHandler NOOP_HANDLER = new ChannelPoolHandler(){

        public void channelReleased(Channel ch) throws Exception {
        }

        public void channelAcquired(Channel ch) throws Exception {
        }

        public void channelCreated(Channel ch) throws Exception {
        }
    };
    private static final Function<Builder, BootstrapProvider> DEFAULT_BOOTSTRAP_PROVIDER = b -> new BootstrapProvider(((Builder)b).sdkEventLoopGroup, ((Builder)b).configuration, ((Builder)b).sdkChannelOptions);
    private final Map<URI, Boolean> shouldProxyForHostCache = new ConcurrentHashMap<URI, Boolean>();
    private final NettyConfiguration configuration;
    private final Protocol protocol;
    private final long maxStreams;
    private final Duration healthCheckPingPeriod;
    private final int initialWindowSize;
    private final SslProvider sslProvider;
    private final ProxyConfiguration proxyConfiguration;
    private final BootstrapProvider bootstrapProvider;
    private final SslContextProvider sslContextProvider;
    private final Boolean useNonBlockingDnsResolver;

    private AwaitCloseChannelPoolMap(Builder builder, Function<Builder, BootstrapProvider> createBootStrapProvider) {
        this.configuration = builder.configuration;
        this.protocol = builder.protocol;
        this.maxStreams = builder.maxStreams;
        this.healthCheckPingPeriod = builder.healthCheckPingPeriod;
        this.initialWindowSize = builder.initialWindowSize;
        this.sslProvider = builder.sslProvider;
        this.proxyConfiguration = builder.proxyConfiguration;
        this.bootstrapProvider = createBootStrapProvider.apply(builder);
        this.sslContextProvider = new SslContextProvider(this.configuration, this.protocol, this.sslProvider);
        this.useNonBlockingDnsResolver = builder.useNonBlockingDnsResolver;
    }

    private AwaitCloseChannelPoolMap(Builder builder) {
        this(builder, DEFAULT_BOOTSTRAP_PROVIDER);
    }

    @SdkTestInternalApi
    AwaitCloseChannelPoolMap(Builder builder, Map<URI, Boolean> shouldProxyForHostCache, BootstrapProvider bootstrapProvider) {
        this(builder, bootstrapProvider == null ? DEFAULT_BOOTSTRAP_PROVIDER : b -> bootstrapProvider);
        if (shouldProxyForHostCache != null) {
            this.shouldProxyForHostCache.putAll(shouldProxyForHostCache);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected SimpleChannelPoolAwareChannelPool newPool(URI key) {
        Object baseChannelPool;
        BetterSimpleChannelPool tcpChannelPool;
        SslContext sslContext = this.needSslContext(key) ? this.sslContextProvider.sslContext() : null;
        Bootstrap bootstrap = this.createBootstrap(key);
        AtomicReference<ChannelPool> channelPoolRef = new AtomicReference<ChannelPool>();
        ChannelPipelineInitializer pipelineInitializer = new ChannelPipelineInitializer(this.protocol, sslContext, this.sslProvider, this.maxStreams, this.initialWindowSize, this.healthCheckPingPeriod, channelPoolRef, this.configuration, key);
        if (this.shouldUseProxyForHost(key)) {
            tcpChannelPool = new BetterSimpleChannelPool(bootstrap, NOOP_HANDLER);
            baseChannelPool = new Http1TunnelConnectionPool(bootstrap.config().group().next(), (ChannelPool)tcpChannelPool, sslContext, this.proxyAddress(key), this.proxyConfiguration.username(), this.proxyConfiguration.password(), key, (ChannelPoolHandler)pipelineInitializer, this.configuration);
        } else {
            baseChannelPool = tcpChannelPool = new BetterSimpleChannelPool(bootstrap, (ChannelPoolHandler)pipelineInitializer);
        }
        SdkChannelPool wrappedPool = this.wrapBaseChannelPool(bootstrap, (ChannelPool)baseChannelPool);
        channelPoolRef.set(wrappedPool);
        return new SimpleChannelPoolAwareChannelPool(wrappedPool, tcpChannelPool);
    }

    @Override
    public void close() {
        log.trace(null, () -> "Closing channel pools");
        Collection channelPools = this.pools().values();
        super.close();
        try {
            CompletableFuture.allOf((CompletableFuture[])channelPools.stream().map(pool -> pool.underlyingSimpleChannelPool().closeFuture()).toArray(CompletableFuture[]::new)).get(5L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private Bootstrap createBootstrap(URI poolKey) {
        String host = this.bootstrapHost(poolKey);
        int port = this.bootstrapPort(poolKey);
        return this.bootstrapProvider.createBootstrap(host, port, this.useNonBlockingDnsResolver);
    }

    private boolean shouldUseProxyForHost(URI remoteAddr) {
        if (this.proxyConfiguration == null) {
            return false;
        }
        return this.shouldProxyForHostCache.computeIfAbsent(remoteAddr, uri -> this.proxyConfiguration.nonProxyHosts().stream().noneMatch(h -> uri.getHost().matches((String)h)));
    }

    private String bootstrapHost(URI remoteHost) {
        if (this.shouldUseProxyForHost(remoteHost)) {
            return this.proxyConfiguration.host();
        }
        return remoteHost.getHost();
    }

    private int bootstrapPort(URI remoteHost) {
        if (this.shouldUseProxyForHost(remoteHost)) {
            return this.proxyConfiguration.port();
        }
        return remoteHost.getPort();
    }

    private URI proxyAddress(URI remoteHost) {
        if (!this.shouldUseProxyForHost(remoteHost)) {
            return null;
        }
        String scheme = this.proxyConfiguration.scheme();
        if (scheme == null) {
            scheme = "http";
        }
        try {
            return new URI(scheme, null, this.proxyConfiguration.host(), this.proxyConfiguration.port(), null, null, null);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Unable to construct proxy URI", e);
        }
    }

    private SdkChannelPool wrapBaseChannelPool(Bootstrap bootstrap, ChannelPool channelPool) {
        channelPool = new HonorCloseOnReleaseChannelPool(channelPool);
        SdkChannelPool sdkChannelPool = new HttpOrHttp2ChannelPool(channelPool, bootstrap.config().group(), this.configuration.maxConnections(), this.configuration);
        sdkChannelPool = new ListenerInvokingChannelPool(bootstrap.config().group(), sdkChannelPool, Arrays.asList(InUseTrackingChannelPoolListener.create(), HandlerRemovingChannelPoolListener.create()));
        sdkChannelPool = new ReleaseOnceChannelPool(sdkChannelPool);
        sdkChannelPool = new HealthCheckedChannelPool(bootstrap.config().group(), this.configuration, sdkChannelPool);
        sdkChannelPool = new CancellableAcquireChannelPool((EventExecutor)bootstrap.config().group().next(), sdkChannelPool);
        return sdkChannelPool;
    }

    private boolean needSslContext(URI targetAddress) {
        URI proxyAddress = this.proxyAddress(targetAddress);
        boolean needContext = targetAddress.getScheme().equalsIgnoreCase("https") || proxyAddress != null && proxyAddress.getScheme().equalsIgnoreCase("https");
        return needContext;
    }

    public static class Builder {
        private SdkChannelOptions sdkChannelOptions;
        private SdkEventLoopGroup sdkEventLoopGroup;
        private NettyConfiguration configuration;
        private Protocol protocol;
        private long maxStreams;
        private int initialWindowSize;
        private Duration healthCheckPingPeriod;
        private SslProvider sslProvider;
        private ProxyConfiguration proxyConfiguration;
        private Boolean useNonBlockingDnsResolver;

        private Builder() {
        }

        public Builder sdkChannelOptions(SdkChannelOptions sdkChannelOptions) {
            this.sdkChannelOptions = sdkChannelOptions;
            return this;
        }

        public Builder sdkEventLoopGroup(SdkEventLoopGroup sdkEventLoopGroup) {
            this.sdkEventLoopGroup = sdkEventLoopGroup;
            return this;
        }

        public Builder configuration(NettyConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder maxStreams(long maxStreams) {
            this.maxStreams = maxStreams;
            return this;
        }

        public Builder initialWindowSize(int initialWindowSize) {
            this.initialWindowSize = initialWindowSize;
            return this;
        }

        public Builder healthCheckPingPeriod(Duration healthCheckPingPeriod) {
            this.healthCheckPingPeriod = healthCheckPingPeriod;
            return this;
        }

        public Builder sslProvider(SslProvider sslProvider) {
            this.sslProvider = sslProvider;
            return this;
        }

        public Builder proxyConfiguration(ProxyConfiguration proxyConfiguration) {
            this.proxyConfiguration = proxyConfiguration;
            return this;
        }

        public Builder useNonBlockingDnsResolver(Boolean useNonBlockingDnsResolver) {
            this.useNonBlockingDnsResolver = useNonBlockingDnsResolver;
            return this;
        }

        public AwaitCloseChannelPoolMap build() {
            return new AwaitCloseChannelPoolMap(this);
        }
    }
}

