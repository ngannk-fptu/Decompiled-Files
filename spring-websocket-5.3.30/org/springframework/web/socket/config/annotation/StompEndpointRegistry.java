/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.socket.config.annotation;

import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebMvcStompEndpointRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.util.UrlPathHelper;

public interface StompEndpointRegistry {
    public StompWebSocketEndpointRegistration addEndpoint(String ... var1);

    public void setOrder(int var1);

    public void setUrlPathHelper(UrlPathHelper var1);

    public WebMvcStompEndpointRegistry setErrorHandler(StompSubProtocolErrorHandler var1);
}

