/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEndpointManager;
import com.hazelcast.core.ClientType;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.Clock;
import com.hazelcast.util.StringUtil;
import java.util.concurrent.TimeUnit;

public class ClientHeartbeatMonitor
implements Runnable {
    private static final int HEART_BEAT_CHECK_INTERVAL_SECONDS = 10;
    private static final int DEFAULT_CLIENT_HEARTBEAT_TIMEOUT_SECONDS = 60;
    private final ClientEndpointManager clientEndpointManager;
    private final long heartbeatTimeoutSeconds;
    private final ExecutionService executionService;
    private final ILogger logger;

    public ClientHeartbeatMonitor(ClientEndpointManager clientEndpointManager, ILogger logger, ExecutionService executionService, HazelcastProperties hazelcastProperties) {
        this.clientEndpointManager = clientEndpointManager;
        this.logger = logger;
        this.executionService = executionService;
        this.heartbeatTimeoutSeconds = this.getHeartbeatTimeout(hazelcastProperties);
    }

    private long getHeartbeatTimeout(HazelcastProperties hazelcastProperties) {
        long configuredTimeout = hazelcastProperties.getSeconds(GroupProperty.CLIENT_HEARTBEAT_TIMEOUT_SECONDS);
        if (configuredTimeout > 0L) {
            return configuredTimeout;
        }
        return 60L;
    }

    public void start() {
        this.executionService.scheduleWithRepetition(this, 10L, 10L, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        this.cleanupEndpointsWithDeadConnections();
        for (ClientEndpoint clientEndpoint : this.clientEndpointManager.getEndpoints()) {
            this.monitor(clientEndpoint);
        }
    }

    private void cleanupEndpointsWithDeadConnections() {
        for (ClientEndpoint endpoint : this.clientEndpointManager.getEndpoints()) {
            if (endpoint.getConnection().isAlive()) continue;
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Cleaning up endpoints with dead connection " + endpoint);
            }
            this.clientEndpointManager.removeEndpoint(endpoint);
        }
    }

    private void monitor(ClientEndpoint clientEndpoint) {
        long currentTimeMillis;
        long timeoutInMillis;
        if (clientEndpoint.isOwnerConnection() && ClientType.CPP.equals((Object)clientEndpoint.getClientType()) && clientEndpoint.getClientVersion() < BuildInfo.calculateVersion("3.10")) {
            return;
        }
        Connection connection = clientEndpoint.getConnection();
        long lastTimePacketReceived = connection.lastReadTimeMillis();
        if (lastTimePacketReceived + (timeoutInMillis = TimeUnit.SECONDS.toMillis(this.heartbeatTimeoutSeconds)) < (currentTimeMillis = Clock.currentTimeMillis())) {
            String message = "Client heartbeat is timed out, closing connection to " + connection + ". Now: " + StringUtil.timeToString(currentTimeMillis) + ". LastTimePacketReceived: " + StringUtil.timeToString(lastTimePacketReceived);
            connection.close(message, null);
        }
    }
}

