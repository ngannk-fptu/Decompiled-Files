/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.IcmpFailureDetectorConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.RestServerEndpointConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.spi.annotation.Beta;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Beta
public class AdvancedNetworkConfig {
    private boolean enabled;
    private final Map<EndpointQualifier, EndpointConfig> endpointConfigs = new ConcurrentHashMap<EndpointQualifier, EndpointConfig>();
    private JoinConfig join = new JoinConfig();
    private IcmpFailureDetectorConfig icmpFailureDetectorConfig;
    private MemberAddressProviderConfig memberAddressProviderConfig = new MemberAddressProviderConfig();

    public AdvancedNetworkConfig() {
        this.endpointConfigs.put(EndpointQualifier.MEMBER, new ServerSocketEndpointConfig().setProtocolType(ProtocolType.MEMBER));
    }

    public MemberAddressProviderConfig getMemberAddressProviderConfig() {
        return this.memberAddressProviderConfig;
    }

    public AdvancedNetworkConfig setMemberAddressProviderConfig(MemberAddressProviderConfig memberAddressProviderConfig) {
        this.memberAddressProviderConfig = memberAddressProviderConfig;
        return this;
    }

    public AdvancedNetworkConfig addWanEndpointConfig(EndpointConfig endpointConfig) {
        endpointConfig.setProtocolType(ProtocolType.WAN);
        this.endpointConfigs.put(endpointConfig.getQualifier(), endpointConfig);
        return this;
    }

    public AdvancedNetworkConfig setMemberEndpointConfig(ServerSocketEndpointConfig serverSocketEndpointConfig) {
        serverSocketEndpointConfig.setProtocolType(ProtocolType.MEMBER);
        this.endpointConfigs.put(EndpointQualifier.MEMBER, serverSocketEndpointConfig);
        return this;
    }

    public AdvancedNetworkConfig setClientEndpointConfig(ServerSocketEndpointConfig serverSocketEndpointConfig) {
        serverSocketEndpointConfig.setProtocolType(ProtocolType.CLIENT);
        this.endpointConfigs.put(EndpointQualifier.CLIENT, serverSocketEndpointConfig);
        return this;
    }

    public AdvancedNetworkConfig setRestEndpointConfig(RestServerEndpointConfig restServerEndpointConfig) {
        restServerEndpointConfig.setProtocolType(ProtocolType.REST);
        this.endpointConfigs.put(EndpointQualifier.REST, restServerEndpointConfig);
        return this;
    }

    public AdvancedNetworkConfig setMemcacheEndpointConfig(ServerSocketEndpointConfig memcacheEndpointConfig) {
        memcacheEndpointConfig.setProtocolType(ProtocolType.MEMCACHE);
        this.endpointConfigs.put(EndpointQualifier.MEMCACHE, memcacheEndpointConfig);
        return this;
    }

    public Map<EndpointQualifier, EndpointConfig> getEndpointConfigs() {
        return this.endpointConfigs;
    }

    public AdvancedNetworkConfig setEndpointConfigs(Map<EndpointQualifier, EndpointConfig> endpointConfigs) {
        for (Map.Entry<EndpointQualifier, EndpointConfig> entry : endpointConfigs.entrySet()) {
            entry.getValue().setProtocolType(entry.getKey().getType());
        }
        for (ProtocolType protocolType : ProtocolType.values()) {
            int count = AdvancedNetworkConfig.countEndpointConfigs(endpointConfigs, protocolType);
            if (count <= protocolType.getServerSocketCardinality()) continue;
            throw new InvalidConfigurationException("Protocol type " + (Object)((Object)protocolType) + " does not allow more than " + protocolType.getServerSocketCardinality() + " server sockets but " + count + " were defined");
        }
        this.endpointConfigs.clear();
        this.endpointConfigs.putAll(endpointConfigs);
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public AdvancedNetworkConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public JoinConfig getJoin() {
        return this.join;
    }

    public AdvancedNetworkConfig setJoin(JoinConfig join) {
        this.join = join;
        return this;
    }

    public AdvancedNetworkConfig setIcmpFailureDetectorConfig(IcmpFailureDetectorConfig icmpFailureDetectorConfig) {
        this.icmpFailureDetectorConfig = icmpFailureDetectorConfig;
        return this;
    }

    public IcmpFailureDetectorConfig getIcmpFailureDetectorConfig() {
        return this.icmpFailureDetectorConfig;
    }

    public RestServerEndpointConfig getRestEndpointConfig() {
        return (RestServerEndpointConfig)this.endpointConfigs.get(EndpointQualifier.REST);
    }

    public String toString() {
        return "AdvancedNetworkConfig{isEnabled=" + this.enabled + ", join=" + this.join + ", memberAddressProvider=" + this.memberAddressProviderConfig + ", endpointConfigs=" + this.endpointConfigs + ", icmpFailureDetectorConfig=" + this.icmpFailureDetectorConfig + '}';
    }

    private static int countEndpointConfigs(Map<EndpointQualifier, EndpointConfig> endpointConfigs, ProtocolType protocolType) {
        int count = 0;
        for (EndpointQualifier qualifier : endpointConfigs.keySet()) {
            if (qualifier.getType() != protocolType) continue;
            ++count;
        }
        return count;
    }

    public static class MemberNetworkingView
    extends NetworkConfig {
        private final AdvancedNetworkConfig config;

        MemberNetworkingView(AdvancedNetworkConfig config) {
            this.config = config;
        }

        private ServerSocketEndpointConfig getMemberEndpoint() {
            return (ServerSocketEndpointConfig)this.config.getEndpointConfigs().get(EndpointQualifier.MEMBER);
        }

        @Override
        public int getPort() {
            return this.getMemberEndpoint().getPort();
        }

        @Override
        public NetworkConfig setPort(int port) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPortCount() {
            return this.getMemberEndpoint().getPortCount();
        }

        @Override
        public void setPortCount(int portCount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPortAutoIncrement() {
            return this.getMemberEndpoint().isPortAutoIncrement();
        }

        @Override
        public NetworkConfig setPortAutoIncrement(boolean portAutoIncrement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReuseAddress() {
            return this.getMemberEndpoint().isReuseAddress();
        }

        @Override
        public NetworkConfig setReuseAddress(boolean reuseAddress) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getOutboundPortDefinitions() {
            return this.getMemberEndpoint().getOutboundPortDefinitions();
        }

        @Override
        public NetworkConfig setOutboundPortDefinitions(Collection<String> outboundPortDefs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NetworkConfig addOutboundPortDefinition(String portDef) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Integer> getOutboundPorts() {
            return this.getMemberEndpoint().getOutboundPorts();
        }

        @Override
        public NetworkConfig setOutboundPorts(Collection<Integer> outboundPorts) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NetworkConfig addOutboundPort(int port) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InterfacesConfig getInterfaces() {
            return this.getMemberEndpoint().getInterfaces();
        }

        @Override
        public NetworkConfig setInterfaces(InterfacesConfig interfaces) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JoinConfig getJoin() {
            return this.config.getJoin();
        }

        @Override
        public NetworkConfig setJoin(JoinConfig join) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPublicAddress() {
            return this.getMemberEndpoint().getPublicAddress();
        }

        @Override
        public NetworkConfig setPublicAddress(String publicAddress) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SocketInterceptorConfig getSocketInterceptorConfig() {
            return this.getMemberEndpoint().getSocketInterceptorConfig();
        }

        @Override
        public NetworkConfig setSocketInterceptorConfig(SocketInterceptorConfig socketInterceptorConfig) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SymmetricEncryptionConfig getSymmetricEncryptionConfig() {
            return this.getMemberEndpoint().getSymmetricEncryptionConfig();
        }

        @Override
        public NetworkConfig setSymmetricEncryptionConfig(SymmetricEncryptionConfig symmetricEncryptionConfig) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SSLConfig getSSLConfig() {
            return this.getMemberEndpoint().getSSLConfig();
        }

        @Override
        public NetworkConfig setSSLConfig(SSLConfig sslConfig) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MemberAddressProviderConfig getMemberAddressProviderConfig() {
            return this.config.getMemberAddressProviderConfig();
        }

        @Override
        public NetworkConfig setMemberAddressProviderConfig(MemberAddressProviderConfig memberAddressProviderConfig) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NetworkConfig setIcmpFailureDetectorConfig(IcmpFailureDetectorConfig icmpFailureDetectorConfig) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IcmpFailureDetectorConfig getIcmpFailureDetectorConfig() {
            return this.config.getIcmpFailureDetectorConfig();
        }

        @Override
        public String toString() {
            return "NetworkConfig{MemberNetworkingView=true, publicAddress='" + this.getPublicAddress() + '\'' + ", port=" + this.getPort() + ", portCount=" + this.getPortCount() + ", portAutoIncrement=" + this.isPortAutoIncrement() + ", join=" + this.getJoin() + ", interfaces=" + this.getInterfaces() + ", sslConfig=" + this.getSSLConfig() + ", socketInterceptorConfig=" + this.getSocketInterceptorConfig() + ", symmetricEncryptionConfig=" + this.getSymmetricEncryptionConfig() + ", icmpFailureDetectorConfig=" + this.getIcmpFailureDetectorConfig() + ", memcacheProtocolConfig=" + this.getMemcacheProtocolConfig() + '}';
        }
    }
}

