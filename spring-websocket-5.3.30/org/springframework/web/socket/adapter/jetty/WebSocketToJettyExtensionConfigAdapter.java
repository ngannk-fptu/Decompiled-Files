/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.websocket.api.extensions.ExtensionConfig
 */
package org.springframework.web.socket.adapter.jetty;

import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.springframework.web.socket.WebSocketExtension;

public class WebSocketToJettyExtensionConfigAdapter
extends ExtensionConfig {
    public WebSocketToJettyExtensionConfigAdapter(WebSocketExtension extension) {
        super(extension.getName());
        extension.getParameters().forEach((x$0, x$1) -> super.setParameter(x$0, x$1));
    }
}

