/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.MemcacheProtocolConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.config.RestServerEndpointConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.MemberSocketInterceptor;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.util.ThreadUtil;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@PrivateApi
public class NodeIOService
implements IOService {
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final RestApiConfig restApiConfig;
    private final MemcacheProtocolConfig memcacheProtocolConfig;

    public NodeIOService(Node node, NodeEngineImpl nodeEngine) {
        this.node = node;
        this.nodeEngine = nodeEngine;
        this.restApiConfig = NodeIOService.initRestApiConfig(node.getProperties(), node.getConfig());
        this.memcacheProtocolConfig = NodeIOService.initMemcacheProtocolConfig(node.getProperties(), node.getConfig());
    }

    private static RestApiConfig initRestApiConfig(HazelcastProperties properties, Config config) {
        boolean isRestConfigPresent;
        boolean isAdvancedNetwork = config.getAdvancedNetworkConfig().isEnabled();
        RestApiConfig restApiConfig = config.getNetworkConfig().getRestApiConfig();
        boolean bl = isAdvancedNetwork ? config.getAdvancedNetworkConfig().getEndpointConfigs().get(EndpointQualifier.REST) != null : (isRestConfigPresent = restApiConfig != null);
        if (isRestConfigPresent) {
            ConfigValidator.ensurePropertyNotConfigured(properties, GroupProperty.REST_ENABLED);
            ConfigValidator.ensurePropertyNotConfigured(properties, GroupProperty.HTTP_HEALTHCHECK_ENABLED);
        }
        if (isRestConfigPresent && isAdvancedNetwork) {
            restApiConfig = new RestApiConfig();
            restApiConfig.setEnabled(true);
            RestServerEndpointConfig restServerEndpointConfig = config.getAdvancedNetworkConfig().getRestEndpointConfig();
            restApiConfig.setEnabledGroups(restServerEndpointConfig.getEnabledGroups());
        } else if (!isRestConfigPresent) {
            restApiConfig = new RestApiConfig();
            if (ConfigValidator.checkAndLogPropertyDeprecated(properties, GroupProperty.REST_ENABLED)) {
                restApiConfig.setEnabled(true);
                restApiConfig.enableAllGroups();
            }
            if (ConfigValidator.checkAndLogPropertyDeprecated(properties, GroupProperty.HTTP_HEALTHCHECK_ENABLED)) {
                restApiConfig.setEnabled(true);
                restApiConfig.enableGroups(RestEndpointGroup.HEALTH_CHECK);
            }
        }
        return restApiConfig;
    }

    private static MemcacheProtocolConfig initMemcacheProtocolConfig(HazelcastProperties properties, Config config) {
        boolean isMemcacheConfigPresent;
        boolean isAdvancedNetwork = config.getAdvancedNetworkConfig().isEnabled();
        MemcacheProtocolConfig memcacheProtocolConfig = config.getNetworkConfig().getMemcacheProtocolConfig();
        boolean bl = isAdvancedNetwork ? config.getAdvancedNetworkConfig().getEndpointConfigs().get(EndpointQualifier.MEMCACHE) != null : (isMemcacheConfigPresent = memcacheProtocolConfig != null);
        if (isMemcacheConfigPresent) {
            ConfigValidator.ensurePropertyNotConfigured(properties, GroupProperty.MEMCACHE_ENABLED);
        }
        if (isMemcacheConfigPresent && isAdvancedNetwork) {
            memcacheProtocolConfig = new MemcacheProtocolConfig();
            memcacheProtocolConfig.setEnabled(true);
        } else if (!isMemcacheConfigPresent) {
            memcacheProtocolConfig = new MemcacheProtocolConfig();
            if (ConfigValidator.checkAndLogPropertyDeprecated(properties, GroupProperty.MEMCACHE_ENABLED)) {
                memcacheProtocolConfig.setEnabled(true);
            }
        }
        return memcacheProtocolConfig;
    }

    @Override
    public HazelcastProperties properties() {
        return this.node.getProperties();
    }

    @Override
    public String getHazelcastName() {
        return this.node.hazelcastInstance.getName();
    }

    @Override
    public LoggingService getLoggingService() {
        return this.nodeEngine.getLoggingService();
    }

    @Override
    public boolean isActive() {
        return this.node.getState() != NodeState.SHUT_DOWN;
    }

    @Override
    public Address getThisAddress() {
        return this.node.getThisAddress();
    }

    @Override
    public Map<EndpointQualifier, Address> getThisAddresses() {
        return this.nodeEngine.getLocalMember().getAddressMap();
    }

    @Override
    public void onFatalError(Exception e) {
        String hzName = this.nodeEngine.getHazelcastInstance().getName();
        Thread thread = new Thread(ThreadUtil.createThreadName(hzName, "io.error.shutdown")){

            @Override
            public void run() {
                NodeIOService.this.node.shutdown(false);
            }
        };
        thread.start();
    }

    public SocketInterceptorConfig getSocketInterceptorConfig(EndpointQualifier endpointQualifier) {
        AdvancedNetworkConfig advancedNetworkConfig = this.node.getConfig().getAdvancedNetworkConfig();
        if (advancedNetworkConfig.isEnabled()) {
            EndpointConfig config = advancedNetworkConfig.getEndpointConfigs().get(endpointQualifier);
            return config != null ? config.getSocketInterceptorConfig() : null;
        }
        return this.node.getConfig().getNetworkConfig().getSocketInterceptorConfig();
    }

    @Override
    public SymmetricEncryptionConfig getSymmetricEncryptionConfig(EndpointQualifier endpointQualifier) {
        AdvancedNetworkConfig advancedNetworkConfig = this.node.getConfig().getAdvancedNetworkConfig();
        if (advancedNetworkConfig.isEnabled()) {
            EndpointConfig config = advancedNetworkConfig.getEndpointConfigs().get(endpointQualifier);
            return config != null ? config.getSymmetricEncryptionConfig() : null;
        }
        return this.node.getConfig().getNetworkConfig().getSymmetricEncryptionConfig();
    }

    @Override
    public SSLConfig getSSLConfig(EndpointQualifier endpointQualifier) {
        AdvancedNetworkConfig advancedNetworkConfig = this.node.getConfig().getAdvancedNetworkConfig();
        if (advancedNetworkConfig.isEnabled()) {
            EndpointConfig config = advancedNetworkConfig.getEndpointConfigs().get(endpointQualifier);
            return config != null ? config.getSSLConfig() : null;
        }
        return this.node.getConfig().getNetworkConfig().getSSLConfig();
    }

    @Override
    public ClientEngine getClientEngine() {
        return this.node.clientEngine;
    }

    @Override
    public TextCommandService getTextCommandService() {
        return this.node.getTextCommandService();
    }

    @Override
    public void removeEndpoint(final Address endPoint) {
        this.nodeEngine.getExecutionService().execute("hz:io", new Runnable(){

            @Override
            public void run() {
                ((NodeIOService)NodeIOService.this).node.clusterService.suspectAddressIfNotConnected(endPoint);
            }
        });
    }

    @Override
    public void onDisconnect(Address endpoint, Throwable cause) {
        if (cause == null) {
            return;
        }
        if (this.node.clusterService.getMember(endpoint) != null) {
            this.nodeEngine.getExecutionService().execute("hz:io", new ReconnectionTask(endpoint));
        }
    }

    @Override
    public void onSuccessfulConnection(Address address) {
        if (!this.node.getClusterService().isJoined()) {
            this.node.getJoiner().unblacklist(address);
        }
    }

    @Override
    public void onFailedConnection(Address address) {
        ClusterServiceImpl clusterService = this.node.clusterService;
        if (!clusterService.isJoined()) {
            this.node.getJoiner().blacklist(address, false);
        } else if (clusterService.getMember(address) != null) {
            this.nodeEngine.getExecutionService().schedule("hz:io", new ReconnectionTask(address), this.getConnectionMonitorInterval(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void shouldConnectTo(Address address) {
        if (this.node.getThisAddress().equals(address)) {
            throw new RuntimeException("Connecting to self! " + address);
        }
    }

    @Override
    public boolean isSocketBind() {
        return this.node.getProperties().getBoolean(GroupProperty.SOCKET_CLIENT_BIND);
    }

    @Override
    public boolean isSocketBindAny() {
        return this.node.getProperties().getBoolean(GroupProperty.SOCKET_CLIENT_BIND_ANY);
    }

    @Override
    public void interceptSocket(EndpointQualifier endpointQualifier, Socket socket, boolean onAccept) throws IOException {
        socket.getChannel().configureBlocking(true);
        if (!this.isSocketInterceptorEnabled(endpointQualifier)) {
            return;
        }
        MemberSocketInterceptor memberSocketInterceptor = this.getSocketInterceptor(endpointQualifier);
        if (memberSocketInterceptor == null) {
            return;
        }
        if (onAccept) {
            memberSocketInterceptor.onAccept(socket);
        } else {
            memberSocketInterceptor.onConnect(socket);
        }
    }

    @Override
    public boolean isSocketInterceptorEnabled(EndpointQualifier endpointQualifier) {
        SocketInterceptorConfig socketInterceptorConfig = this.getSocketInterceptorConfig(endpointQualifier);
        return socketInterceptorConfig != null && socketInterceptorConfig.isEnabled();
    }

    @Override
    public int getSocketConnectTimeoutSeconds(EndpointQualifier endpointQualifier) {
        AdvancedNetworkConfig advancedNetworkConfig = this.node.getConfig().getAdvancedNetworkConfig();
        if (advancedNetworkConfig.isEnabled()) {
            EndpointConfig config = advancedNetworkConfig.getEndpointConfigs().get(endpointQualifier);
            return config != null ? config.getSocketConnectTimeoutSeconds() : 0;
        }
        return this.node.getProperties().getSeconds(GroupProperty.SOCKET_CONNECT_TIMEOUT_SECONDS);
    }

    @Override
    public long getConnectionMonitorInterval() {
        return this.node.getProperties().getMillis(GroupProperty.CONNECTION_MONITOR_INTERVAL);
    }

    @Override
    public int getConnectionMonitorMaxFaults() {
        return this.node.getProperties().getInteger(GroupProperty.CONNECTION_MONITOR_MAX_FAULTS);
    }

    @Override
    public void executeAsync(Runnable runnable) {
        this.nodeEngine.getExecutionService().execute("hz:io", runnable);
    }

    @Override
    public EventService getEventService() {
        return this.nodeEngine.getEventService();
    }

    @Override
    public InternalSerializationService getSerializationService() {
        return this.node.getSerializationService();
    }

    @Override
    public MemberSocketInterceptor getSocketInterceptor(EndpointQualifier endpointQualifier) {
        return this.node.getNodeExtension().getSocketInterceptor(endpointQualifier);
    }

    @Override
    public InboundHandler[] createInboundHandlers(EndpointQualifier qualifier, TcpIpConnection connection) {
        return this.node.getNodeExtension().createInboundHandlers(qualifier, connection, this);
    }

    @Override
    public OutboundHandler[] createOutboundHandlers(EndpointQualifier qualifier, TcpIpConnection connection) {
        return this.node.getNodeExtension().createOutboundHandlers(qualifier, connection, this);
    }

    @Override
    public Collection<Integer> getOutboundPorts(EndpointQualifier endpointQualifier) {
        AdvancedNetworkConfig advancedNetworkConfig = this.node.getConfig().getAdvancedNetworkConfig();
        if (advancedNetworkConfig.isEnabled()) {
            EndpointConfig endpointConfig = advancedNetworkConfig.getEndpointConfigs().get(endpointQualifier);
            List<Integer> outboundPorts = endpointConfig != null ? endpointConfig.getOutboundPorts() : Collections.emptyList();
            List<String> outboundPortDefinitions = endpointConfig != null ? endpointConfig.getOutboundPortDefinitions() : Collections.emptyList();
            return AddressUtil.getOutboundPorts(outboundPorts, outboundPortDefinitions);
        }
        NetworkConfig networkConfig = this.node.getConfig().getNetworkConfig();
        Collection<Integer> outboundPorts = networkConfig.getOutboundPorts();
        Collection<String> outboundPortDefinitions = networkConfig.getOutboundPortDefinitions();
        return AddressUtil.getOutboundPorts(outboundPorts, outboundPortDefinitions);
    }

    @Override
    public RestApiConfig getRestApiConfig() {
        return this.restApiConfig;
    }

    @Override
    public MemcacheProtocolConfig getMemcacheProtocolConfig() {
        return this.memcacheProtocolConfig;
    }

    private class ReconnectionTask
    implements Runnable {
        private final Address endpoint;

        ReconnectionTask(Address endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public void run() {
            ClusterServiceImpl clusterService = ((NodeIOService)NodeIOService.this).node.clusterService;
            if (clusterService.getMember(this.endpoint) != null) {
                NodeIOService.this.node.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(this.endpoint);
            }
        }
    }
}

