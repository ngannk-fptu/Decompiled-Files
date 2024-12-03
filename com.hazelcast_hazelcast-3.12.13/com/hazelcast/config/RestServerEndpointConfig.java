/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class RestServerEndpointConfig
extends ServerSocketEndpointConfig {
    private final Set<RestEndpointGroup> enabledGroups = Collections.synchronizedSet(EnumSet.noneOf(RestEndpointGroup.class));

    public RestServerEndpointConfig() {
        for (RestEndpointGroup eg : RestEndpointGroup.values()) {
            if (!eg.isEnabledByDefault()) continue;
            this.enabledGroups.add(eg);
        }
    }

    @Override
    public final ProtocolType getProtocolType() {
        return ProtocolType.REST;
    }

    @Override
    public EndpointQualifier getQualifier() {
        return EndpointQualifier.REST;
    }

    public RestServerEndpointConfig enableAllGroups() {
        return this.enableGroups(RestEndpointGroup.values());
    }

    public RestServerEndpointConfig enableGroups(RestEndpointGroup ... endpointGroups) {
        if (endpointGroups != null) {
            this.enabledGroups.addAll(Arrays.asList(endpointGroups));
        }
        return this;
    }

    public RestServerEndpointConfig disableAllGroups() {
        this.enabledGroups.clear();
        return this;
    }

    public RestServerEndpointConfig disableGroups(RestEndpointGroup ... endpointGroups) {
        if (endpointGroups != null) {
            this.enabledGroups.removeAll(Arrays.asList(endpointGroups));
        }
        return this;
    }

    public boolean isEnabledAndNotEmpty() {
        return !this.enabledGroups.isEmpty();
    }

    public Set<RestEndpointGroup> getEnabledGroups() {
        return new HashSet<RestEndpointGroup>(this.enabledGroups);
    }

    public boolean isGroupEnabled(RestEndpointGroup group) {
        return this.enabledGroups.contains((Object)group);
    }

    public void setEnabledGroups(Collection<RestEndpointGroup> groups) {
        this.enabledGroups.clear();
        if (groups != null) {
            this.enabledGroups.addAll(groups);
        }
    }

    @Override
    public RestServerEndpointConfig setPublicAddress(String publicAddress) {
        super.setPublicAddress(publicAddress);
        return this;
    }

    @Override
    public RestServerEndpointConfig setPort(int port) {
        super.setPort(port);
        return this;
    }

    @Override
    public RestServerEndpointConfig setPortAutoIncrement(boolean portAutoIncrement) {
        super.setPortAutoIncrement(portAutoIncrement);
        return this;
    }

    @Override
    public RestServerEndpointConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress(reuseAddress);
        return this;
    }

    @Override
    public RestServerEndpointConfig setName(String name) {
        super.setName(name);
        return this;
    }

    @Override
    public RestServerEndpointConfig setOutboundPortDefinitions(Collection<String> outboundPortDefs) {
        super.setOutboundPortDefinitions((Collection)outboundPortDefs);
        return this;
    }

    @Override
    public RestServerEndpointConfig setOutboundPorts(Collection<Integer> outboundPorts) {
        super.setOutboundPorts((Collection)outboundPorts);
        return this;
    }

    @Override
    public RestServerEndpointConfig setInterfaces(InterfacesConfig interfaces) {
        super.setInterfaces(interfaces);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketBufferDirect(boolean socketBufferDirect) {
        super.setSocketBufferDirect(socketBufferDirect);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketKeepAlive(boolean socketKeepAlive) {
        super.setSocketKeepAlive(socketKeepAlive);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketTcpNoDelay(boolean socketTcpNoDelay) {
        super.setSocketTcpNoDelay(socketTcpNoDelay);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketSendBufferSizeKb(int socketSendBufferSizeKb) {
        super.setSocketSendBufferSizeKb(socketSendBufferSizeKb);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketRcvBufferSizeKb(int socketRcvBufferSizeKb) {
        super.setSocketRcvBufferSizeKb(socketRcvBufferSizeKb);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketLingerSeconds(int socketLingerSeconds) {
        super.setSocketLingerSeconds(socketLingerSeconds);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketConnectTimeoutSeconds(int socketConnectTimeoutSeconds) {
        super.setSocketConnectTimeoutSeconds(socketConnectTimeoutSeconds);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSocketInterceptorConfig(SocketInterceptorConfig socketInterceptorConfig) {
        super.setSocketInterceptorConfig(socketInterceptorConfig);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSSLConfig(SSLConfig sslConfig) {
        super.setSSLConfig(sslConfig);
        return this;
    }

    @Override
    public RestServerEndpointConfig setSymmetricEncryptionConfig(SymmetricEncryptionConfig symmetricEncryptionConfig) {
        super.setSymmetricEncryptionConfig(symmetricEncryptionConfig);
        return this;
    }

    @Override
    public RestServerEndpointConfig addOutboundPortDefinition(String portDef) {
        super.addOutboundPortDefinition(portDef);
        return this;
    }

    @Override
    public RestServerEndpointConfig addOutboundPort(int port) {
        super.addOutboundPort(port);
        return this;
    }

    @Override
    public String toString() {
        return "RestServerEndpointConfig{enabledGroups=" + this.enabledGroups + "}";
    }
}

