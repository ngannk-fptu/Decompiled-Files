/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.spi.ServiceProvider
 *  javax.websocket.Extension
 *  javax.websocket.HandshakeResponse
 *  javax.websocket.server.HandshakeRequest
 *  javax.websocket.server.ServerEndpointConfig
 *  javax.websocket.server.ServerEndpointConfig$Configurator
 */
package org.apache.tomcat.websocket.server;

import aQute.bnd.annotation.spi.ServiceProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

@ServiceProvider(value=ServerEndpointConfig.Configurator.class)
public class DefaultServerEndpointConfigurator
extends ServerEndpointConfig.Configurator {
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        try {
            return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (InstantiationException e) {
            throw e;
        }
        catch (ReflectiveOperationException e) {
            InstantiationException ie = new InstantiationException();
            ie.initCause(e);
            throw ie;
        }
    }

    public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
        for (String request : requested) {
            if (!supported.contains(request)) continue;
            return request;
        }
        return "";
    }

    public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
        HashSet<String> installedNames = new HashSet<String>();
        for (Extension e : installed) {
            installedNames.add(e.getName());
        }
        ArrayList<Extension> result = new ArrayList<Extension>();
        for (Extension request : requested) {
            if (!installedNames.contains(request.getName())) continue;
            result.add(request);
        }
        return result;
    }

    public boolean checkOrigin(String originHeaderValue) {
        return true;
    }

    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
    }
}

