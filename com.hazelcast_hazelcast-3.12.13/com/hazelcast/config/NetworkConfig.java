/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.IcmpFailureDetectorConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.config.MemcacheProtocolConfig;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.security.jsm.HazelcastRuntimePermission;
import com.hazelcast.util.StringUtil;
import java.util.Collection;
import java.util.HashSet;

public class NetworkConfig {
    public static final int DEFAULT_PORT = 5701;
    private static final int PORT_MAX = 65535;
    private static final int PORT_AUTO_INCREMENT = 100;
    private int port = 5701;
    private int portCount = 100;
    private boolean portAutoIncrement = true;
    private boolean reuseAddress;
    private String publicAddress;
    private Collection<String> outboundPortDefinitions;
    private Collection<Integer> outboundPorts;
    private InterfacesConfig interfaces = new InterfacesConfig();
    private JoinConfig join = new JoinConfig();
    private SymmetricEncryptionConfig symmetricEncryptionConfig;
    private SocketInterceptorConfig socketInterceptorConfig;
    private SSLConfig sslConfig;
    private MemberAddressProviderConfig memberAddressProviderConfig = new MemberAddressProviderConfig();
    private IcmpFailureDetectorConfig icmpFailureDetectorConfig;
    private RestApiConfig restApiConfig;
    private MemcacheProtocolConfig memcacheProtocolConfig;

    public NetworkConfig() {
        String os = StringUtil.lowerCaseInternal(System.getProperty("os.name"));
        this.reuseAddress = !os.contains("win");
    }

    public int getPort() {
        return this.port;
    }

    public NetworkConfig setPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port out of range: " + port + ". Allowed range [0,65535]");
        }
        this.port = port;
        return this;
    }

    public int getPortCount() {
        return this.portCount;
    }

    public void setPortCount(int portCount) {
        if (portCount < 1) {
            throw new IllegalArgumentException("port count can't be smaller than 0");
        }
        this.portCount = portCount;
    }

    public boolean isPortAutoIncrement() {
        return this.portAutoIncrement;
    }

    public NetworkConfig setPortAutoIncrement(boolean portAutoIncrement) {
        this.portAutoIncrement = portAutoIncrement;
        return this;
    }

    public boolean isReuseAddress() {
        return this.reuseAddress;
    }

    public NetworkConfig setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
        return this;
    }

    public Collection<String> getOutboundPortDefinitions() {
        return this.outboundPortDefinitions;
    }

    public NetworkConfig setOutboundPortDefinitions(Collection<String> outboundPortDefs) {
        this.outboundPortDefinitions = outboundPortDefs;
        return this;
    }

    public NetworkConfig addOutboundPortDefinition(String portDef) {
        if (this.outboundPortDefinitions == null) {
            this.outboundPortDefinitions = new HashSet<String>();
        }
        this.outboundPortDefinitions.add(portDef);
        return this;
    }

    public Collection<Integer> getOutboundPorts() {
        return this.outboundPorts;
    }

    public NetworkConfig setOutboundPorts(Collection<Integer> outboundPorts) {
        this.outboundPorts = outboundPorts;
        return this;
    }

    public NetworkConfig addOutboundPort(int port) {
        if (this.outboundPorts == null) {
            this.outboundPorts = new HashSet<Integer>();
        }
        this.outboundPorts.add(port);
        return this;
    }

    public InterfacesConfig getInterfaces() {
        return this.interfaces;
    }

    public NetworkConfig setInterfaces(InterfacesConfig interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    public JoinConfig getJoin() {
        return this.join;
    }

    public NetworkConfig setJoin(JoinConfig join) {
        this.join = join;
        return this;
    }

    public String getPublicAddress() {
        return this.publicAddress;
    }

    public NetworkConfig setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
        return this;
    }

    public SocketInterceptorConfig getSocketInterceptorConfig() {
        return this.socketInterceptorConfig;
    }

    public NetworkConfig setSocketInterceptorConfig(SocketInterceptorConfig socketInterceptorConfig) {
        this.socketInterceptorConfig = socketInterceptorConfig;
        return this;
    }

    public SymmetricEncryptionConfig getSymmetricEncryptionConfig() {
        return this.symmetricEncryptionConfig;
    }

    public NetworkConfig setSymmetricEncryptionConfig(SymmetricEncryptionConfig symmetricEncryptionConfig) {
        this.symmetricEncryptionConfig = symmetricEncryptionConfig;
        return this;
    }

    public SSLConfig getSSLConfig() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new HazelcastRuntimePermission("com.hazelcast.config.NetworkConfig.getSSLConfig"));
        }
        return this.sslConfig;
    }

    public NetworkConfig setSSLConfig(SSLConfig sslConfig) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new HazelcastRuntimePermission("com.hazelcast.config.NetworkConfig.setSSLConfig"));
        }
        this.sslConfig = sslConfig;
        return this;
    }

    public MemberAddressProviderConfig getMemberAddressProviderConfig() {
        return this.memberAddressProviderConfig;
    }

    public NetworkConfig setMemberAddressProviderConfig(MemberAddressProviderConfig memberAddressProviderConfig) {
        this.memberAddressProviderConfig = memberAddressProviderConfig;
        return this;
    }

    public NetworkConfig setIcmpFailureDetectorConfig(IcmpFailureDetectorConfig icmpFailureDetectorConfig) {
        this.icmpFailureDetectorConfig = icmpFailureDetectorConfig;
        return this;
    }

    public IcmpFailureDetectorConfig getIcmpFailureDetectorConfig() {
        return this.icmpFailureDetectorConfig;
    }

    public RestApiConfig getRestApiConfig() {
        return this.restApiConfig;
    }

    public NetworkConfig setRestApiConfig(RestApiConfig restApiConfig) {
        this.restApiConfig = restApiConfig;
        return this;
    }

    public MemcacheProtocolConfig getMemcacheProtocolConfig() {
        return this.memcacheProtocolConfig;
    }

    public NetworkConfig setMemcacheProtocolConfig(MemcacheProtocolConfig memcacheProtocolConfig) {
        this.memcacheProtocolConfig = memcacheProtocolConfig;
        return this;
    }

    public String toString() {
        return "NetworkConfig{publicAddress='" + this.publicAddress + '\'' + ", port=" + this.port + ", portCount=" + this.portCount + ", portAutoIncrement=" + this.portAutoIncrement + ", join=" + this.join + ", interfaces=" + this.interfaces + ", sslConfig=" + this.sslConfig + ", socketInterceptorConfig=" + this.socketInterceptorConfig + ", symmetricEncryptionConfig=" + this.symmetricEncryptionConfig + ", icmpFailureDetectorConfig=" + this.icmpFailureDetectorConfig + ", restApiConfig=" + this.restApiConfig + ", memcacheProtocolConfig=" + this.memcacheProtocolConfig + '}';
    }
}

