/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Session
 *  javax.websocket.server.ServerEndpointConfig
 */
package org.apache.tomcat.websocket.pojo;

import java.util.Map;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.tomcat.websocket.pojo.PojoEndpointBase;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;

public class PojoEndpointServer
extends PojoEndpointBase {
    public PojoEndpointServer(Map<String, String> pathParameters, Object pojo) {
        super(pathParameters);
        this.setPojo(pojo);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
        ServerEndpointConfig sec = (ServerEndpointConfig)endpointConfig;
        PojoMethodMapping methodMapping = (PojoMethodMapping)sec.getUserProperties().get("org.apache.tomcat.websocket.pojo.PojoEndpoint.methodMapping");
        this.setMethodMapping(methodMapping);
        this.doOnOpen(session, endpointConfig);
    }
}

