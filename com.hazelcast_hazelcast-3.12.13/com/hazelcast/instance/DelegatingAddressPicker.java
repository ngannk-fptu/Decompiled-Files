/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ServerSocketHelper;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.MemberAddressProvider;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class DelegatingAddressPicker
implements AddressPicker {
    private final Map<EndpointQualifier, InetSocketAddress> bindAddresses = new ConcurrentHashMap<EndpointQualifier, InetSocketAddress>();
    private final Map<EndpointQualifier, InetSocketAddress> publicAddresses = new ConcurrentHashMap<EndpointQualifier, InetSocketAddress>();
    private final Map<EndpointQualifier, ServerSocketChannel> serverSocketChannels = new ConcurrentHashMap<EndpointQualifier, ServerSocketChannel>();
    private final MemberAddressProvider memberAddressProvider;
    private final Config config;
    private final ILogger logger;
    private final boolean usesAdvancedNetworkConfig;

    DelegatingAddressPicker(MemberAddressProvider memberAddressProvider, Config config, ILogger logger) {
        this.logger = logger;
        this.config = config;
        this.memberAddressProvider = memberAddressProvider;
        this.usesAdvancedNetworkConfig = config.getAdvancedNetworkConfig().isEnabled();
    }

    @Override
    public void pickAddress() throws Exception {
        try {
            if (this.usesAdvancedNetworkConfig) {
                this.pickAddressFromEndpointConfig();
            } else {
                this.pickAddressFromNetworkConfig();
            }
        }
        catch (Exception e) {
            this.logger.severe(e);
            throw e;
        }
    }

    private void validatePublicAddress(InetSocketAddress inetSocketAddress) {
        InetAddress address = inetSocketAddress.getAddress();
        if (address == null) {
            throw new ConfigurationException("Cannot resolve address '" + inetSocketAddress + "'");
        }
        if (address.isAnyLocalAddress()) {
            throw new ConfigurationException("Member address provider has to return a specific public address to broadcast to other members.");
        }
    }

    private void pickAddressFromNetworkConfig() {
        NetworkConfig networkConfig = this.config.getNetworkConfig();
        InetSocketAddress bindAddress = this.memberAddressProvider.getBindAddress();
        InetSocketAddress publicAddress = this.memberAddressProvider.getPublicAddress();
        this.validatePublicAddress(publicAddress);
        ServerSocketChannel serverSocketChannel = ServerSocketHelper.createServerSocketChannel(this.logger, null, bindAddress.getAddress(), bindAddress.getPort() == 0 ? networkConfig.getPort() : bindAddress.getPort(), networkConfig.getPortCount(), networkConfig.isPortAutoIncrement(), networkConfig.isReuseAddress(), false);
        int port = serverSocketChannel.socket().getLocalPort();
        if (port != bindAddress.getPort()) {
            bindAddress = new InetSocketAddress(bindAddress.getAddress(), port);
        }
        this.logger.info("Using bind address: " + bindAddress);
        if (publicAddress.getPort() == 0) {
            publicAddress = new InetSocketAddress(publicAddress.getAddress(), port);
        }
        this.logger.info("Using public address: " + publicAddress);
        this.bindAddresses.put(EndpointQualifier.MEMBER, bindAddress);
        this.publicAddresses.put(EndpointQualifier.MEMBER, publicAddress);
        this.serverSocketChannels.put(EndpointQualifier.MEMBER, serverSocketChannel);
    }

    private void pickAddressFromEndpointConfig() {
        for (EndpointConfig ec : this.config.getAdvancedNetworkConfig().getEndpointConfigs().values()) {
            if (!(ec instanceof ServerSocketEndpointConfig)) continue;
            ServerSocketEndpointConfig endpointConfig = (ServerSocketEndpointConfig)ec;
            EndpointQualifier qualifier = endpointConfig.getQualifier();
            InetSocketAddress bindAddress = this.memberAddressProvider.getBindAddress(qualifier);
            InetSocketAddress publicAddress = this.memberAddressProvider.getPublicAddress(qualifier);
            this.validatePublicAddress(publicAddress);
            if (!this.bindAddresses.values().contains(bindAddress)) {
                ServerSocketChannel serverSocketChannel = ServerSocketHelper.createServerSocketChannel(this.logger, ec, bindAddress.getAddress(), bindAddress.getPort() == 0 ? endpointConfig.getPort() : bindAddress.getPort(), endpointConfig.getPortCount(), endpointConfig.isPortAutoIncrement(), endpointConfig.isReuseAddress(), false);
                this.serverSocketChannels.put(qualifier, serverSocketChannel);
                int port = serverSocketChannel.socket().getLocalPort();
                if (port != bindAddress.getPort()) {
                    bindAddress = new InetSocketAddress(bindAddress.getAddress(), port);
                }
                if (publicAddress.getPort() == 0) {
                    publicAddress = new InetSocketAddress(publicAddress.getAddress(), port);
                }
            }
            this.logger.info("Using bind address: " + bindAddress + ", public address: " + publicAddress + " for qualifier " + qualifier);
            this.bindAddresses.put(qualifier, bindAddress);
            this.publicAddresses.put(qualifier, publicAddress);
        }
    }

    @Override
    public Address getBindAddress() {
        return this.getBindAddress(EndpointQualifier.MEMBER);
    }

    @Override
    public Address getBindAddress(EndpointQualifier qualifier) {
        return this.usesAdvancedNetworkConfig ? new Address(this.bindAddresses.get(qualifier)) : new Address(this.bindAddresses.get(EndpointQualifier.MEMBER));
    }

    @Override
    public Address getPublicAddress() {
        return this.getPublicAddress(EndpointQualifier.MEMBER);
    }

    @Override
    public Address getPublicAddress(EndpointQualifier qualifier) {
        return this.usesAdvancedNetworkConfig ? new Address(this.publicAddresses.get(qualifier)) : new Address(this.publicAddresses.get(EndpointQualifier.MEMBER));
    }

    @Override
    public ServerSocketChannel getServerSocketChannel() {
        return this.getServerSocketChannel(EndpointQualifier.MEMBER);
    }

    @Override
    public ServerSocketChannel getServerSocketChannel(EndpointQualifier qualifier) {
        return this.usesAdvancedNetworkConfig ? this.serverSocketChannels.get(qualifier) : this.serverSocketChannels.get(EndpointQualifier.MEMBER);
    }

    @Override
    public Map<EndpointQualifier, ServerSocketChannel> getServerSocketChannels() {
        return this.serverSocketChannels;
    }

    @Override
    public Map<EndpointQualifier, Address> getPublicAddressMap() {
        HashMap<EndpointQualifier, Address> mappings = new HashMap<EndpointQualifier, Address>(this.publicAddresses.size());
        for (Map.Entry<EndpointQualifier, InetSocketAddress> entry : this.publicAddresses.entrySet()) {
            mappings.put(entry.getKey(), new Address(entry.getValue()));
        }
        return mappings;
    }
}

