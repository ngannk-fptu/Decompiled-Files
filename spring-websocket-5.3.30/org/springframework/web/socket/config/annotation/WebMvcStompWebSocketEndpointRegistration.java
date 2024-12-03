/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.HttpRequestHandler
 */
package org.springframework.web.socket.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;

public class WebMvcStompWebSocketEndpointRegistration
implements StompWebSocketEndpointRegistration {
    private final String[] paths;
    private final WebSocketHandler webSocketHandler;
    private final TaskScheduler sockJsTaskScheduler;
    @Nullable
    private HandshakeHandler handshakeHandler;
    private final List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
    private final List<String> allowedOrigins = new ArrayList<String>();
    private final List<String> allowedOriginPatterns = new ArrayList<String>();
    @Nullable
    private SockJsServiceRegistration registration;

    public WebMvcStompWebSocketEndpointRegistration(String[] paths, WebSocketHandler webSocketHandler, TaskScheduler sockJsTaskScheduler) {
        Assert.notEmpty((Object[])paths, (String)"No paths specified");
        Assert.notNull((Object)webSocketHandler, (String)"WebSocketHandler must not be null");
        this.paths = paths;
        this.webSocketHandler = webSocketHandler;
        this.sockJsTaskScheduler = sockJsTaskScheduler;
    }

    @Override
    public StompWebSocketEndpointRegistration setHandshakeHandler(HandshakeHandler handshakeHandler) {
        this.handshakeHandler = handshakeHandler;
        return this;
    }

    @Override
    public StompWebSocketEndpointRegistration addInterceptors(HandshakeInterceptor ... interceptors) {
        if (!ObjectUtils.isEmpty((Object[])interceptors)) {
            this.interceptors.addAll(Arrays.asList(interceptors));
        }
        return this;
    }

    @Override
    public StompWebSocketEndpointRegistration setAllowedOrigins(String ... allowedOrigins) {
        this.allowedOrigins.clear();
        if (!ObjectUtils.isEmpty((Object[])allowedOrigins)) {
            this.allowedOrigins.addAll(Arrays.asList(allowedOrigins));
        }
        return this;
    }

    @Override
    public StompWebSocketEndpointRegistration setAllowedOriginPatterns(String ... allowedOriginPatterns) {
        this.allowedOriginPatterns.clear();
        if (!ObjectUtils.isEmpty((Object[])allowedOriginPatterns)) {
            this.allowedOriginPatterns.addAll(Arrays.asList(allowedOriginPatterns));
        }
        return this;
    }

    @Override
    public SockJsServiceRegistration withSockJS() {
        this.registration = new SockJsServiceRegistration();
        this.registration.setTaskScheduler(this.sockJsTaskScheduler);
        HandshakeInterceptor[] interceptors = this.getInterceptors();
        if (interceptors.length > 0) {
            this.registration.setInterceptors(interceptors);
        }
        if (this.handshakeHandler != null) {
            WebSocketTransportHandler handler = new WebSocketTransportHandler(this.handshakeHandler);
            this.registration.setTransportHandlerOverrides(handler);
        }
        if (!this.allowedOrigins.isEmpty()) {
            this.registration.setAllowedOrigins(StringUtils.toStringArray(this.allowedOrigins));
        }
        if (!this.allowedOriginPatterns.isEmpty()) {
            this.registration.setAllowedOriginPatterns(StringUtils.toStringArray(this.allowedOriginPatterns));
        }
        return this.registration;
    }

    protected HandshakeInterceptor[] getInterceptors() {
        ArrayList<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>(this.interceptors.size() + 1);
        interceptors.addAll(this.interceptors);
        OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(this.allowedOrigins);
        interceptors.add(interceptor);
        if (!ObjectUtils.isEmpty(this.allowedOriginPatterns)) {
            interceptor.setAllowedOriginPatterns(this.allowedOriginPatterns);
        }
        return interceptors.toArray(new HandshakeInterceptor[0]);
    }

    public final MultiValueMap<HttpRequestHandler, String> getMappings() {
        LinkedMultiValueMap mappings = new LinkedMultiValueMap();
        if (this.registration != null) {
            SockJsService sockJsService = this.registration.getSockJsService();
            for (String path : this.paths) {
                String pattern = path.endsWith("/") ? path + "**" : path + "/**";
                SockJsHttpRequestHandler handler = new SockJsHttpRequestHandler(sockJsService, this.webSocketHandler);
                mappings.add((Object)handler, (Object)pattern);
            }
        } else {
            for (String path : this.paths) {
                WebSocketHttpRequestHandler handler = this.handshakeHandler != null ? new WebSocketHttpRequestHandler(this.webSocketHandler, this.handshakeHandler) : new WebSocketHttpRequestHandler(this.webSocketHandler);
                HandshakeInterceptor[] interceptors = this.getInterceptors();
                if (interceptors.length > 0) {
                    handler.setHandshakeInterceptors(Arrays.asList(interceptors));
                }
                mappings.add((Object)handler, (Object)path);
            }
        }
        return mappings;
    }
}

