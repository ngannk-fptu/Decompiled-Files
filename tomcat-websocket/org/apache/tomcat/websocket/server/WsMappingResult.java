/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.server.ServerEndpointConfig
 */
package org.apache.tomcat.websocket.server;

import java.util.Map;
import javax.websocket.server.ServerEndpointConfig;

class WsMappingResult {
    private final ServerEndpointConfig config;
    private final Map<String, String> pathParams;

    WsMappingResult(ServerEndpointConfig config, Map<String, String> pathParams) {
        this.config = config;
        this.pathParams = pathParams;
    }

    ServerEndpointConfig getConfig() {
        return this.config;
    }

    Map<String, String> getPathParams() {
        return this.pathParams;
    }
}

