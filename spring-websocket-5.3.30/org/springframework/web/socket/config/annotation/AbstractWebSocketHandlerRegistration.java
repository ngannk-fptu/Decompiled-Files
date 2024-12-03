/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.socket.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;

public abstract class AbstractWebSocketHandlerRegistration<M>
implements WebSocketHandlerRegistration {
    private final MultiValueMap<WebSocketHandler, String> handlerMap = new LinkedMultiValueMap();
    @Nullable
    private HandshakeHandler handshakeHandler;
    private final List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
    private final List<String> allowedOrigins = new ArrayList<String>();
    private final List<String> allowedOriginPatterns = new ArrayList<String>();
    @Nullable
    private SockJsServiceRegistration sockJsServiceRegistration;

    @Override
    public WebSocketHandlerRegistration addHandler(WebSocketHandler handler, String ... paths) {
        Assert.notNull((Object)handler, (String)"WebSocketHandler must not be null");
        Assert.notEmpty((Object[])paths, (String)"Paths must not be empty");
        this.handlerMap.put((Object)handler, Arrays.asList(paths));
        return this;
    }

    @Override
    public WebSocketHandlerRegistration setHandshakeHandler(@Nullable HandshakeHandler handshakeHandler) {
        this.handshakeHandler = handshakeHandler;
        return this;
    }

    @Nullable
    protected HandshakeHandler getHandshakeHandler() {
        return this.handshakeHandler;
    }

    @Override
    public WebSocketHandlerRegistration addInterceptors(HandshakeInterceptor ... interceptors) {
        if (!ObjectUtils.isEmpty((Object[])interceptors)) {
            this.interceptors.addAll(Arrays.asList(interceptors));
        }
        return this;
    }

    @Override
    public WebSocketHandlerRegistration setAllowedOrigins(String ... allowedOrigins) {
        this.allowedOrigins.clear();
        if (!ObjectUtils.isEmpty((Object[])allowedOrigins)) {
            this.allowedOrigins.addAll(Arrays.asList(allowedOrigins));
        }
        return this;
    }

    @Override
    public WebSocketHandlerRegistration setAllowedOriginPatterns(String ... allowedOriginPatterns) {
        this.allowedOriginPatterns.clear();
        if (!ObjectUtils.isEmpty((Object[])allowedOriginPatterns)) {
            this.allowedOriginPatterns.addAll(Arrays.asList(allowedOriginPatterns));
        }
        return this;
    }

    @Override
    public SockJsServiceRegistration withSockJS() {
        this.sockJsServiceRegistration = new SockJsServiceRegistration();
        HandshakeInterceptor[] interceptors = this.getInterceptors();
        if (interceptors.length > 0) {
            this.sockJsServiceRegistration.setInterceptors(interceptors);
        }
        if (this.handshakeHandler != null) {
            WebSocketTransportHandler transportHandler = new WebSocketTransportHandler(this.handshakeHandler);
            this.sockJsServiceRegistration.setTransportHandlerOverrides(transportHandler);
        }
        if (!this.allowedOrigins.isEmpty()) {
            this.sockJsServiceRegistration.setAllowedOrigins(StringUtils.toStringArray(this.allowedOrigins));
        }
        if (!this.allowedOriginPatterns.isEmpty()) {
            this.sockJsServiceRegistration.setAllowedOriginPatterns(StringUtils.toStringArray(this.allowedOriginPatterns));
        }
        return this.sockJsServiceRegistration;
    }

    protected HandshakeInterceptor[] getInterceptors() {
        ArrayList<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>(this.interceptors.size() + 1);
        interceptors.addAll(this.interceptors);
        OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(this.allowedOrigins);
        if (!ObjectUtils.isEmpty(this.allowedOriginPatterns)) {
            interceptor.setAllowedOriginPatterns(this.allowedOriginPatterns);
        }
        interceptors.add(interceptor);
        return interceptors.toArray(new HandshakeInterceptor[0]);
    }

    @Nullable
    protected SockJsServiceRegistration getSockJsServiceRegistration() {
        return this.sockJsServiceRegistration;
    }

    protected final M getMappings() {
        M mappings = this.createMappings();
        if (this.sockJsServiceRegistration != null) {
            SockJsService sockJsService = this.sockJsServiceRegistration.getSockJsService();
            this.handlerMap.forEach((wsHandler, paths) -> {
                for (String path : paths) {
                    String pathPattern = path.endsWith("/") ? path + "**" : path + "/**";
                    this.addSockJsServiceMapping(mappings, sockJsService, (WebSocketHandler)wsHandler, pathPattern);
                }
            });
        } else {
            HandshakeHandler handshakeHandler = this.getOrCreateHandshakeHandler();
            HandshakeInterceptor[] interceptors = this.getInterceptors();
            this.handlerMap.forEach((wsHandler, paths) -> {
                for (String path : paths) {
                    this.addWebSocketHandlerMapping(mappings, (WebSocketHandler)wsHandler, handshakeHandler, interceptors, path);
                }
            });
        }
        return mappings;
    }

    private HandshakeHandler getOrCreateHandshakeHandler() {
        return this.handshakeHandler != null ? this.handshakeHandler : new DefaultHandshakeHandler();
    }

    protected abstract M createMappings();

    protected abstract void addSockJsServiceMapping(M var1, SockJsService var2, WebSocketHandler var3, String var4);

    protected abstract void addWebSocketHandlerMapping(M var1, WebSocketHandler var2, HandshakeHandler var3, HandshakeInterceptor[] var4, String var5);
}

