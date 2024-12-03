/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.web.socket.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.socket.sockjs.transport.TransportHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.DefaultSockJsService;

public class SockJsServiceRegistration {
    @Nullable
    private TaskScheduler scheduler;
    @Nullable
    private String clientLibraryUrl;
    @Nullable
    private Integer streamBytesLimit;
    @Nullable
    private Boolean sessionCookieNeeded;
    @Nullable
    private Long heartbeatTime;
    @Nullable
    private Long disconnectDelay;
    @Nullable
    private Integer httpMessageCacheSize;
    @Nullable
    private Boolean webSocketEnabled;
    private final List<TransportHandler> transportHandlers = new ArrayList<TransportHandler>();
    private final List<TransportHandler> transportHandlerOverrides = new ArrayList<TransportHandler>();
    private final List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
    private final List<String> allowedOrigins = new ArrayList<String>();
    private final List<String> allowedOriginPatterns = new ArrayList<String>();
    @Nullable
    private Boolean suppressCors;
    @Nullable
    private SockJsMessageCodec messageCodec;

    public SockJsServiceRegistration setTaskScheduler(TaskScheduler scheduler) {
        Assert.notNull((Object)scheduler, (String)"TaskScheduler is required");
        this.scheduler = scheduler;
        return this;
    }

    public SockJsServiceRegistration setClientLibraryUrl(String clientLibraryUrl) {
        this.clientLibraryUrl = clientLibraryUrl;
        return this;
    }

    public SockJsServiceRegistration setStreamBytesLimit(int streamBytesLimit) {
        this.streamBytesLimit = streamBytesLimit;
        return this;
    }

    public SockJsServiceRegistration setSessionCookieNeeded(boolean sessionCookieNeeded) {
        this.sessionCookieNeeded = sessionCookieNeeded;
        return this;
    }

    public SockJsServiceRegistration setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
        return this;
    }

    public SockJsServiceRegistration setDisconnectDelay(long disconnectDelay) {
        this.disconnectDelay = disconnectDelay;
        return this;
    }

    public SockJsServiceRegistration setHttpMessageCacheSize(int httpMessageCacheSize) {
        this.httpMessageCacheSize = httpMessageCacheSize;
        return this;
    }

    public SockJsServiceRegistration setWebSocketEnabled(boolean webSocketEnabled) {
        this.webSocketEnabled = webSocketEnabled;
        return this;
    }

    public SockJsServiceRegistration setTransportHandlers(TransportHandler ... handlers) {
        this.transportHandlers.clear();
        if (!ObjectUtils.isEmpty((Object[])handlers)) {
            this.transportHandlers.addAll(Arrays.asList(handlers));
        }
        return this;
    }

    public SockJsServiceRegistration setTransportHandlerOverrides(TransportHandler ... handlers) {
        this.transportHandlerOverrides.clear();
        if (!ObjectUtils.isEmpty((Object[])handlers)) {
            this.transportHandlerOverrides.addAll(Arrays.asList(handlers));
        }
        return this;
    }

    public SockJsServiceRegistration setInterceptors(HandshakeInterceptor ... interceptors) {
        this.interceptors.clear();
        if (!ObjectUtils.isEmpty((Object[])interceptors)) {
            this.interceptors.addAll(Arrays.asList(interceptors));
        }
        return this;
    }

    protected SockJsServiceRegistration setAllowedOrigins(String ... allowedOrigins) {
        this.allowedOrigins.clear();
        if (!ObjectUtils.isEmpty((Object[])allowedOrigins)) {
            this.allowedOrigins.addAll(Arrays.asList(allowedOrigins));
        }
        return this;
    }

    protected SockJsServiceRegistration setAllowedOriginPatterns(String ... allowedOriginPatterns) {
        this.allowedOriginPatterns.clear();
        if (!ObjectUtils.isEmpty((Object[])allowedOriginPatterns)) {
            this.allowedOriginPatterns.addAll(Arrays.asList(allowedOriginPatterns));
        }
        return this;
    }

    public SockJsServiceRegistration setSuppressCors(boolean suppressCors) {
        this.suppressCors = suppressCors;
        return this;
    }

    @Deprecated
    public SockJsServiceRegistration setSupressCors(boolean suppressCors) {
        return this.setSuppressCors(suppressCors);
    }

    public SockJsServiceRegistration setMessageCodec(SockJsMessageCodec codec) {
        this.messageCodec = codec;
        return this;
    }

    protected SockJsService getSockJsService() {
        TransportHandlingSockJsService service = this.createSockJsService();
        service.setHandshakeInterceptors(this.interceptors);
        if (this.clientLibraryUrl != null) {
            service.setSockJsClientLibraryUrl(this.clientLibraryUrl);
        }
        if (this.streamBytesLimit != null) {
            service.setStreamBytesLimit(this.streamBytesLimit);
        }
        if (this.sessionCookieNeeded != null) {
            service.setSessionCookieNeeded(this.sessionCookieNeeded);
        }
        if (this.heartbeatTime != null) {
            service.setHeartbeatTime(this.heartbeatTime);
        }
        if (this.disconnectDelay != null) {
            service.setDisconnectDelay(this.disconnectDelay);
        }
        if (this.httpMessageCacheSize != null) {
            service.setHttpMessageCacheSize(this.httpMessageCacheSize);
        }
        if (this.webSocketEnabled != null) {
            service.setWebSocketEnabled(this.webSocketEnabled);
        }
        if (this.suppressCors != null) {
            service.setSuppressCors(this.suppressCors);
        }
        service.setAllowedOrigins(this.allowedOrigins);
        service.setAllowedOriginPatterns(this.allowedOriginPatterns);
        if (this.messageCodec != null) {
            service.setMessageCodec(this.messageCodec);
        }
        return service;
    }

    @Nullable
    protected TaskScheduler getTaskScheduler() {
        return this.scheduler;
    }

    private TransportHandlingSockJsService createSockJsService() {
        Assert.state((this.scheduler != null ? 1 : 0) != 0, (String)"No TaskScheduler available");
        Assert.state((this.transportHandlers.isEmpty() || this.transportHandlerOverrides.isEmpty() ? 1 : 0) != 0, (String)"Specify either TransportHandlers or TransportHandler overrides, not both");
        return !this.transportHandlers.isEmpty() ? new TransportHandlingSockJsService(this.scheduler, this.transportHandlers) : new DefaultSockJsService(this.scheduler, this.transportHandlerOverrides);
    }
}

