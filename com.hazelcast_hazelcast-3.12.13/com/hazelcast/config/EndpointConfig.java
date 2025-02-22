/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import java.util.Collection;
import java.util.HashSet;

public class EndpointConfig
implements NamedConfig {
    public static final int DEFAULT_SOCKET_CONNECT_TIMEOUT_SECONDS = 0;
    public static final int DEFAULT_SOCKET_SEND_BUFFER_SIZE_KB = 128;
    public static final int DEFAULT_SOCKET_RECEIVE_BUFFER_SIZE_KB = 128;
    public static final int DEFAULT_SOCKET_LINGER_SECONDS = 0;
    protected String name;
    protected ProtocolType protocolType;
    protected InterfacesConfig interfaces = new InterfacesConfig();
    protected SocketInterceptorConfig socketInterceptorConfig;
    protected SSLConfig sslConfig;
    protected SymmetricEncryptionConfig symmetricEncryptionConfig;
    private Collection<String> outboundPortDefinitions;
    private Collection<Integer> outboundPorts;
    private boolean socketBufferDirect;
    private boolean socketTcpNoDelay = true;
    private boolean socketKeepAlive = true;
    private int socketConnectTimeoutSeconds = 0;
    private int socketSendBufferSizeKb = 128;
    private int socketRcvBufferSizeKb = 128;
    private int socketLingerSeconds = 0;

    public ProtocolType getProtocolType() {
        return this.protocolType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EndpointConfig setName(String name) {
        this.name = name;
        return this;
    }

    public SymmetricEncryptionConfig getSymmetricEncryptionConfig() {
        return this.symmetricEncryptionConfig;
    }

    public EndpointConfig setSymmetricEncryptionConfig(SymmetricEncryptionConfig symmetricEncryptionConfig) {
        this.symmetricEncryptionConfig = symmetricEncryptionConfig;
        return this;
    }

    public EndpointQualifier getQualifier() {
        return EndpointQualifier.resolve(this.protocolType, this.name);
    }

    public Collection<String> getOutboundPortDefinitions() {
        return this.outboundPortDefinitions;
    }

    public EndpointConfig setOutboundPortDefinitions(Collection<String> outboundPortDefs) {
        this.outboundPortDefinitions = outboundPortDefs;
        return this;
    }

    public EndpointConfig addOutboundPortDefinition(String portDef) {
        if (this.outboundPortDefinitions == null) {
            this.outboundPortDefinitions = new HashSet<String>();
        }
        this.outboundPortDefinitions.add(portDef);
        return this;
    }

    public Collection<Integer> getOutboundPorts() {
        return this.outboundPorts;
    }

    public EndpointConfig setOutboundPorts(Collection<Integer> outboundPorts) {
        this.outboundPorts = outboundPorts;
        return this;
    }

    public EndpointConfig addOutboundPort(int port) {
        if (this.outboundPorts == null) {
            this.outboundPorts = new HashSet<Integer>();
        }
        this.outboundPorts.add(port);
        return this;
    }

    public InterfacesConfig getInterfaces() {
        return this.interfaces;
    }

    public EndpointConfig setInterfaces(InterfacesConfig interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    public boolean isSocketBufferDirect() {
        return this.socketBufferDirect;
    }

    public EndpointConfig setSocketBufferDirect(boolean socketBufferDirect) {
        this.socketBufferDirect = socketBufferDirect;
        return this;
    }

    public boolean isSocketTcpNoDelay() {
        return this.socketTcpNoDelay;
    }

    public boolean isSocketKeepAlive() {
        return this.socketKeepAlive;
    }

    public EndpointConfig setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
        return this;
    }

    public EndpointConfig setSocketTcpNoDelay(boolean socketTcpNoDelay) {
        this.socketTcpNoDelay = socketTcpNoDelay;
        return this;
    }

    public int getSocketSendBufferSizeKb() {
        return this.socketSendBufferSizeKb;
    }

    public EndpointConfig setSocketSendBufferSizeKb(int socketSendBufferSizeKb) {
        this.socketSendBufferSizeKb = socketSendBufferSizeKb;
        return this;
    }

    public int getSocketRcvBufferSizeKb() {
        return this.socketRcvBufferSizeKb;
    }

    public EndpointConfig setSocketRcvBufferSizeKb(int socketRcvBufferSizeKb) {
        this.socketRcvBufferSizeKb = socketRcvBufferSizeKb;
        return this;
    }

    public int getSocketLingerSeconds() {
        return this.socketLingerSeconds;
    }

    public EndpointConfig setSocketLingerSeconds(int socketLingerSeconds) {
        this.socketLingerSeconds = socketLingerSeconds;
        return this;
    }

    public int getSocketConnectTimeoutSeconds() {
        return this.socketConnectTimeoutSeconds;
    }

    public EndpointConfig setSocketConnectTimeoutSeconds(int socketConnectTimeoutSeconds) {
        this.socketConnectTimeoutSeconds = socketConnectTimeoutSeconds;
        return this;
    }

    public SocketInterceptorConfig getSocketInterceptorConfig() {
        return this.socketInterceptorConfig;
    }

    public EndpointConfig setSocketInterceptorConfig(SocketInterceptorConfig socketInterceptorConfig) {
        this.socketInterceptorConfig = socketInterceptorConfig;
        return this;
    }

    public SSLConfig getSSLConfig() {
        return this.sslConfig;
    }

    public EndpointConfig setSSLConfig(SSLConfig sslConfig) {
        this.sslConfig = sslConfig;
        return this;
    }

    EndpointConfig setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
        return this;
    }
}

