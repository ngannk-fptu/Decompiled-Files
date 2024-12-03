/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.DefaultAddressPicker;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Map;

class AdvancedNetworkAddressPicker
implements AddressPicker {
    private final AdvancedNetworkConfig advancedNetworkConfig;
    private final Map<EndpointQualifier, AddressPicker> pickers = new HashMap<EndpointQualifier, AddressPicker>();

    AdvancedNetworkAddressPicker(Config config, ILogger logger) {
        this.advancedNetworkConfig = config.getAdvancedNetworkConfig();
        for (EndpointConfig endpointConfig : this.advancedNetworkConfig.getEndpointConfigs().values()) {
            if (!(endpointConfig instanceof ServerSocketEndpointConfig)) continue;
            ServerSocketEndpointConfig serverSocketEndpointConfig = (ServerSocketEndpointConfig)endpointConfig;
            EndpointQualifier endpointQualifier = serverSocketEndpointConfig.getQualifier();
            TcpIpConfig tcpIpConfig = this.advancedNetworkConfig.getJoin().getTcpIpConfig();
            InterfacesConfig interfacesConfig = serverSocketEndpointConfig.getInterfaces();
            String publicAddressConfig = serverSocketEndpointConfig.getPublicAddress();
            boolean isReuseAddress = serverSocketEndpointConfig.isReuseAddress();
            boolean isPortAutoIncrement = serverSocketEndpointConfig.isPortAutoIncrement();
            int port = serverSocketEndpointConfig.getPort();
            int portCount = serverSocketEndpointConfig.getPortCount();
            DefaultAddressPicker picker = new DefaultAddressPicker(config, endpointQualifier, interfacesConfig, tcpIpConfig, isReuseAddress, isPortAutoIncrement, port, portCount, publicAddressConfig, logger);
            this.pickers.put(endpointConfig.getQualifier(), picker);
        }
    }

    @Override
    public void pickAddress() throws Exception {
        for (AddressPicker picker : this.pickers.values()) {
            picker.pickAddress();
        }
    }

    @Override
    public Address getBindAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Address getBindAddress(EndpointQualifier qualifier) {
        return this.pickers.get(qualifier).getBindAddress(qualifier);
    }

    @Override
    public Address getPublicAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Address getPublicAddress(EndpointQualifier qualifier) {
        return this.pickers.get(qualifier).getPublicAddress(qualifier);
    }

    @Override
    public Map<EndpointQualifier, Address> getPublicAddressMap() {
        HashMap<EndpointQualifier, Address> pubAddressMap = new HashMap<EndpointQualifier, Address>(this.pickers.size());
        for (Map.Entry<EndpointQualifier, AddressPicker> entry : this.pickers.entrySet()) {
            pubAddressMap.put(entry.getKey(), entry.getValue().getPublicAddress(entry.getKey()));
        }
        return pubAddressMap;
    }

    @Override
    public ServerSocketChannel getServerSocketChannel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServerSocketChannel getServerSocketChannel(EndpointQualifier qualifier) {
        return this.pickers.get(qualifier).getServerSocketChannel(qualifier);
    }

    @Override
    public Map<EndpointQualifier, ServerSocketChannel> getServerSocketChannels() {
        HashMap<EndpointQualifier, ServerSocketChannel> channels = new HashMap<EndpointQualifier, ServerSocketChannel>(this.pickers.size());
        for (Map.Entry<EndpointQualifier, AddressPicker> entry : this.pickers.entrySet()) {
            channels.put(entry.getKey(), entry.getValue().getServerSocketChannel(entry.getKey()));
        }
        return channels;
    }
}

