/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.networking.ChannelInitializer;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.ascii.TextChannelInitializer;
import com.hazelcast.nio.tcp.ClientChannelInitializer;
import com.hazelcast.nio.tcp.MemberChannelInitializer;
import com.hazelcast.nio.tcp.UnifiedChannelInitializer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultChannelInitializerProvider
implements ChannelInitializerProvider {
    protected final IOService ioService;
    private final ChannelInitializer uniChannelInitializer;
    private final Config config;
    private volatile Map<EndpointQualifier, ChannelInitializer> initializerMap;

    public DefaultChannelInitializerProvider(IOService ioService, Config config) {
        this.checkSslConfigAvailability(config);
        this.ioService = ioService;
        this.uniChannelInitializer = new UnifiedChannelInitializer(ioService);
        this.config = config;
    }

    @Override
    public ChannelInitializer provide(EndpointQualifier qualifier) {
        return this.initializerMap.isEmpty() ? this.provideUnifiedChannelInitializer() : this.initializerMap.get(qualifier);
    }

    public void init() {
        AdvancedNetworkConfig advancedNetworkConfig = this.config.getAdvancedNetworkConfig();
        if (!advancedNetworkConfig.isEnabled() || advancedNetworkConfig.getEndpointConfigs().isEmpty()) {
            this.initializerMap = Collections.emptyMap();
            return;
        }
        HashMap<EndpointQualifier, ChannelInitializer> map = new HashMap<EndpointQualifier, ChannelInitializer>();
        block7: for (EndpointConfig endpointConfig : advancedNetworkConfig.getEndpointConfigs().values()) {
            this.checkSslConfigAvailability(endpointConfig.getSSLConfig());
            switch (endpointConfig.getProtocolType()) {
                case MEMBER: {
                    map.put(EndpointQualifier.MEMBER, this.provideMemberChannelInitializer(endpointConfig));
                    continue block7;
                }
                case CLIENT: {
                    map.put(EndpointQualifier.CLIENT, this.provideClientChannelInitializer(endpointConfig));
                    continue block7;
                }
                case REST: {
                    map.put(EndpointQualifier.REST, this.provideTextChannelInitializer(endpointConfig, true));
                    continue block7;
                }
                case MEMCACHE: {
                    map.put(EndpointQualifier.MEMCACHE, this.provideTextChannelInitializer(endpointConfig, false));
                    continue block7;
                }
                case WAN: {
                    map.put(endpointConfig.getQualifier(), this.provideMemberChannelInitializer(endpointConfig));
                    continue block7;
                }
            }
            throw new IllegalStateException("Cannot build channel initializer for protocol type " + (Object)((Object)endpointConfig.getProtocolType()));
        }
        this.initializerMap = map;
    }

    protected ChannelInitializer provideUnifiedChannelInitializer() {
        return this.uniChannelInitializer;
    }

    protected ChannelInitializer provideMemberChannelInitializer(EndpointConfig endpointConfig) {
        return new MemberChannelInitializer(this.ioService, endpointConfig);
    }

    protected ChannelInitializer provideClientChannelInitializer(EndpointConfig endpointConfig) {
        return new ClientChannelInitializer(this.ioService, endpointConfig);
    }

    protected ChannelInitializer provideTextChannelInitializer(EndpointConfig endpointConfig, boolean rest) {
        return new TextChannelInitializer(this.ioService, endpointConfig, rest);
    }

    protected ChannelInitializer provideWanChannelInitializer(EndpointConfig endpointConfig) {
        throw new UnsupportedOperationException("TODO");
    }

    private void checkSslConfigAvailability(Config config) {
        if (config.getAdvancedNetworkConfig().isEnabled()) {
            return;
        }
        SSLConfig sslConfig = config.getNetworkConfig().getSSLConfig();
        this.checkSslConfigAvailability(sslConfig);
    }

    private void checkSslConfigAvailability(SSLConfig sslConfig) {
        if (sslConfig != null && sslConfig.isEnabled() && !BuildInfoProvider.getBuildInfo().isEnterprise()) {
            throw new IllegalStateException("SSL/TLS requires Hazelcast Enterprise Edition");
        }
    }
}

