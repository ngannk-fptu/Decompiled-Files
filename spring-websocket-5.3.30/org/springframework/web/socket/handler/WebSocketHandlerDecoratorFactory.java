/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.handler;

import org.springframework.web.socket.WebSocketHandler;

public interface WebSocketHandlerDecoratorFactory {
    public WebSocketHandler decorate(WebSocketHandler var1);
}

