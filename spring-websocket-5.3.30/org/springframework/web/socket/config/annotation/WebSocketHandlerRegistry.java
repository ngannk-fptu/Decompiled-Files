/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.config.annotation;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;

public interface WebSocketHandlerRegistry {
    public WebSocketHandlerRegistration addHandler(WebSocketHandler var1, String ... var2);
}

