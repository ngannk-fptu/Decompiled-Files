/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.nio.ascii.TextParsers;
import com.hazelcast.nio.ascii.TextProtocolFilter;
import com.hazelcast.nio.tcp.TcpIpConnection;
import java.util.StringTokenizer;

public class RestApiFilter
implements TextProtocolFilter {
    private final RestApiConfig restApiConfig;
    private final TextParsers parsers;

    RestApiFilter(RestApiConfig restApiConfig, TextParsers parsers) {
        this.restApiConfig = restApiConfig;
        this.parsers = parsers;
    }

    @Override
    public void filterConnection(String commandLine, TcpIpConnection connection) {
        RestEndpointGroup restEndpointGroup = this.getEndpointGroup(commandLine);
        if (restEndpointGroup != null) {
            if (!this.restApiConfig.isGroupEnabled(restEndpointGroup)) {
                connection.close("REST endpoint group is not enabled - " + (Object)((Object)restEndpointGroup), null);
            }
        } else if (!commandLine.isEmpty()) {
            connection.close("Unsupported command received on REST API handler.", null);
        }
    }

    private RestEndpointGroup getEndpointGroup(String commandLine) {
        if (commandLine == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(commandLine);
        String operation = this.nextToken(st);
        if (this.parsers.getParser(operation) == null) {
            return null;
        }
        String requestUri = this.nextToken(st);
        return requestUri != null ? this.getHttpApiEndpointGroup(operation, requestUri) : null;
    }

    private RestEndpointGroup getHttpApiEndpointGroup(String operation, String requestUri) {
        if (requestUri.startsWith("/hazelcast/rest/maps/") || requestUri.startsWith("/hazelcast/rest/queues/")) {
            return RestEndpointGroup.DATA;
        }
        if (requestUri.startsWith("/hazelcast/health")) {
            return RestEndpointGroup.HEALTH_CHECK;
        }
        if (requestUri.startsWith("/hazelcast/rest/mancenter/wan/") || requestUri.startsWith("/hazelcast/rest/wan/") || requestUri.startsWith("/hazelcast/rest/mancenter/clearWanQueues")) {
            return RestEndpointGroup.WAN;
        }
        if (requestUri.startsWith("/hazelcast/rest/management/cluster/forceStart") || requestUri.startsWith("/hazelcast/rest/management/cluster/partialStart") || requestUri.startsWith("/hazelcast/rest/management/cluster/hotBackup") || requestUri.startsWith("/hazelcast/rest/management/cluster/hotBackupInterrupt")) {
            return RestEndpointGroup.HOT_RESTART;
        }
        if (requestUri.startsWith("/hazelcast/rest/cluster") || requestUri.startsWith("/hazelcast/rest/management/cluster/state") || requestUri.startsWith("/hazelcast/rest/management/cluster/nodes") || "GET".equals(operation) && requestUri.startsWith("/hazelcast/rest/license") || "GET".equals(operation) && requestUri.startsWith("/hazelcast/rest/management/cluster/version")) {
            return RestEndpointGroup.CLUSTER_READ;
        }
        if (requestUri.startsWith("/hazelcast/")) {
            return RestEndpointGroup.CLUSTER_WRITE;
        }
        return null;
    }

    private String nextToken(StringTokenizer st) {
        return st.hasMoreTokens() ? st.nextToken() : null;
    }
}

