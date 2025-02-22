/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.util.StringUtil;
import java.util.Collection;

public class ServerSocketEndpointConfig
extends EndpointConfig {
    public static final int DEFAULT_PORT = 5701;
    public static final int PORT_AUTO_INCREMENT = 100;
    private static final int PORT_MAX = 65535;
    private int port = 5701;
    private int portCount = 100;
    private boolean portAutoIncrement = true;
    private boolean reuseAddress;
    private String publicAddress;

    public ServerSocketEndpointConfig() {
        String os = StringUtil.lowerCaseInternal(System.getProperty("os.name"));
        this.reuseAddress = !os.contains("win");
    }

    public String getPublicAddress() {
        return this.publicAddress;
    }

    public ServerSocketEndpointConfig setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public ServerSocketEndpointConfig setPort(int port) {
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

    public ServerSocketEndpointConfig setPortAutoIncrement(boolean portAutoIncrement) {
        this.portAutoIncrement = portAutoIncrement;
        return this;
    }

    public boolean isReuseAddress() {
        return this.reuseAddress;
    }

    public ServerSocketEndpointConfig setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
        return this;
    }

    @Override
    ServerSocketEndpointConfig setProtocolType(ProtocolType protocolType) {
        super.setProtocolType(protocolType);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setName(String name) {
        super.setName(name);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setOutboundPortDefinitions(Collection<String> outboundPortDefs) {
        super.setOutboundPortDefinitions(outboundPortDefs);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setOutboundPorts(Collection<Integer> outboundPorts) {
        super.setOutboundPorts(outboundPorts);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setInterfaces(InterfacesConfig interfaces) {
        super.setInterfaces(interfaces);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSocketBufferDirect(boolean socketBufferDirect) {
        super.setSocketBufferDirect(socketBufferDirect);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSocketKeepAlive(boolean socketKeepAlive) {
        super.setSocketKeepAlive(socketKeepAlive);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSocketTcpNoDelay(boolean socketTcpNoDelay) {
        super.setSocketTcpNoDelay(socketTcpNoDelay);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSocketSendBufferSizeKb(int socketSendBufferSizeKb) {
        super.setSocketSendBufferSizeKb(socketSendBufferSizeKb);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSocketRcvBufferSizeKb(int socketRcvBufferSizeKb) {
        super.setSocketRcvBufferSizeKb(socketRcvBufferSizeKb);
        return this;
    }

    @Override
    public EndpointConfig setSocketLingerSeconds(int socketLingerSeconds) {
        super.setSocketLingerSeconds(socketLingerSeconds);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSocketConnectTimeoutSeconds(int socketConnectTimeoutSeconds) {
        super.setSocketConnectTimeoutSeconds(socketConnectTimeoutSeconds);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSocketInterceptorConfig(SocketInterceptorConfig socketInterceptorConfig) {
        super.setSocketInterceptorConfig(socketInterceptorConfig);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSSLConfig(SSLConfig sslConfig) {
        super.setSSLConfig(sslConfig);
        return this;
    }

    @Override
    public ServerSocketEndpointConfig setSymmetricEncryptionConfig(SymmetricEncryptionConfig symmetricEncryptionConfig) {
        super.setSymmetricEncryptionConfig(symmetricEncryptionConfig);
        return this;
    }

    public String toString() {
        return "EndpointConfig{protocolType=" + (Object)((Object)this.protocolType) + ", name=" + this.name + ", port=" + this.port + ", portCount=" + this.portCount + ", portAutoIncrement=" + this.portAutoIncrement + ", interfaces=" + this.interfaces + ", sslConfig=" + this.sslConfig + ", socketInterceptorConfig=" + this.socketInterceptorConfig + '}';
    }
}

