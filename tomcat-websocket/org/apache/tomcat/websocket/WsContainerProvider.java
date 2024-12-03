/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.spi.ServiceProvider
 *  javax.websocket.ContainerProvider
 *  javax.websocket.WebSocketContainer
 */
package org.apache.tomcat.websocket;

import aQute.bnd.annotation.spi.ServiceProvider;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import org.apache.tomcat.websocket.WsWebSocketContainer;

@ServiceProvider(value=ContainerProvider.class)
public class WsContainerProvider
extends ContainerProvider {
    protected WebSocketContainer getContainer() {
        return new WsWebSocketContainer();
    }
}

