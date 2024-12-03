/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.Lifecycle
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.web.socket.sockjs.transport;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.context.Lifecycle;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HandshakeInterceptorChain;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.socket.sockjs.support.AbstractSockJsService;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.SockJsSessionFactory;
import org.springframework.web.socket.sockjs.transport.TransportHandler;
import org.springframework.web.socket.sockjs.transport.TransportType;

public class TransportHandlingSockJsService
extends AbstractSockJsService
implements SockJsServiceConfig,
Lifecycle {
    private static final boolean jackson2Present = ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)TransportHandlingSockJsService.class.getClassLoader());
    private final Map<TransportType, TransportHandler> handlers = new EnumMap<TransportType, TransportHandler>(TransportType.class);
    @Nullable
    private SockJsMessageCodec messageCodec;
    private final List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
    private final Map<String, SockJsSession> sessions = new ConcurrentHashMap<String, SockJsSession>();
    @Nullable
    private ScheduledFuture<?> sessionCleanupTask;
    private volatile boolean running;

    public TransportHandlingSockJsService(TaskScheduler scheduler, TransportHandler ... handlers) {
        this(scheduler, Arrays.asList(handlers));
    }

    public TransportHandlingSockJsService(TaskScheduler scheduler, Collection<TransportHandler> handlers) {
        super(scheduler);
        if (CollectionUtils.isEmpty(handlers)) {
            this.logger.warn((Object)"No transport handlers specified for TransportHandlingSockJsService");
        } else {
            for (TransportHandler handler : handlers) {
                handler.initialize(this);
                this.handlers.put(handler.getTransportType(), handler);
            }
        }
        if (jackson2Present) {
            this.messageCodec = new Jackson2SockJsMessageCodec();
        }
    }

    public Map<TransportType, TransportHandler> getTransportHandlers() {
        return Collections.unmodifiableMap(this.handlers);
    }

    public void setMessageCodec(SockJsMessageCodec messageCodec) {
        this.messageCodec = messageCodec;
    }

    @Override
    public SockJsMessageCodec getMessageCodec() {
        Assert.state((this.messageCodec != null ? 1 : 0) != 0, (String)"A SockJsMessageCodec is required but not available: Add Jackson to the classpath, or configure a custom SockJsMessageCodec.");
        return this.messageCodec;
    }

    public void setHandshakeInterceptors(@Nullable List<HandshakeInterceptor> interceptors) {
        this.interceptors.clear();
        if (interceptors != null) {
            this.interceptors.addAll(interceptors);
        }
    }

    public List<HandshakeInterceptor> getHandshakeInterceptors() {
        return this.interceptors;
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            for (TransportHandler handler : this.handlers.values()) {
                if (!(handler instanceof Lifecycle)) continue;
                ((Lifecycle)handler).start();
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            for (TransportHandler handler : this.handlers.values()) {
                if (!(handler instanceof Lifecycle)) continue;
                ((Lifecycle)handler).stop();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void handleRawWebSocketRequest(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler) throws IOException {
        block10: {
            TransportHandler transportHandler = this.handlers.get((Object)TransportType.WEBSOCKET);
            if (!(transportHandler instanceof HandshakeHandler)) {
                this.logger.error((Object)"No handler configured for raw WebSocket messages");
                response.setStatusCode(HttpStatus.NOT_FOUND);
                return;
            }
            HandshakeInterceptorChain chain = new HandshakeInterceptorChain(this.interceptors, handler);
            HandshakeFailureException failure = null;
            try {
                HashMap<String, Object> attributes = new HashMap<String, Object>();
                if (!chain.applyBeforeHandshake(request, response, attributes)) {
                    return;
                }
                ((HandshakeHandler)((Object)transportHandler)).doHandshake(request, response, handler, attributes);
                chain.applyAfterHandshake(request, response, null);
            }
            catch (HandshakeFailureException ex) {
                failure = ex;
            }
            catch (Exception ex) {
                failure = new HandshakeFailureException("Uncaught failure for request " + request.getURI(), ex);
            }
            finally {
                if (failure == null) break block10;
                chain.applyAfterHandshake(request, response, (Exception)((Object)failure));
                throw failure;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void handleTransportRequest(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, String sessionId, String transport) throws SockJsException {
        TransportType transportType = TransportType.fromValue(transport);
        if (transportType == null) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)LogFormatUtils.formatValue((Object)("Unknown transport type for " + request.getURI()), (int)-1, (boolean)true));
            }
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return;
        }
        TransportHandler transportHandler = this.handlers.get((Object)transportType);
        if (transportHandler == null) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)LogFormatUtils.formatValue((Object)("No TransportHandler for " + request.getURI()), (int)-1, (boolean)true));
            }
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return;
        }
        SockJsException failure = null;
        HandshakeInterceptorChain chain = new HandshakeInterceptorChain(this.interceptors, handler);
        try {
            int status;
            HttpMethod supportedMethod = transportType.getHttpMethod();
            if (supportedMethod != request.getMethod()) {
                if (request.getMethod() == HttpMethod.OPTIONS && transportType.supportsCors()) {
                    if (!this.checkOrigin(request, response, HttpMethod.OPTIONS, supportedMethod)) return;
                    response.setStatusCode(HttpStatus.NO_CONTENT);
                    this.addCacheHeaders(response);
                    return;
                }
                if (transportType.supportsCors()) {
                    this.sendMethodNotAllowed(response, supportedMethod, HttpMethod.OPTIONS);
                    return;
                }
                this.sendMethodNotAllowed(response, supportedMethod);
                return;
            }
            SockJsSession session = this.sessions.get(sessionId);
            boolean isNewSession = false;
            if (session == null) {
                if (!(transportHandler instanceof SockJsSessionFactory)) {
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    if (!this.logger.isDebugEnabled()) return;
                    this.logger.debug((Object)("Session not found, sessionId=" + sessionId + ". The session may have been closed (e.g. missed heart-beat) while a message was coming in."));
                    return;
                }
                HashMap<String, Object> attributes = new HashMap<String, Object>();
                if (!chain.applyBeforeHandshake(request, response, attributes)) {
                    return;
                }
                SockJsSessionFactory sessionFactory = (SockJsSessionFactory)((Object)transportHandler);
                session = this.createSockJsSession(sessionId, sessionFactory, handler, attributes);
                isNewSession = true;
            } else {
                Principal principal = session.getPrincipal();
                if (principal != null && !principal.equals(request.getPrincipal())) {
                    this.logger.debug((Object)"The user for the session does not match the user for the request.");
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return;
                }
                if (!transportHandler.checkSessionType(session)) {
                    this.logger.debug((Object)"Session type does not match the transport type for the request.");
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return;
                }
            }
            if (transportType.sendsNoCacheInstruction()) {
                this.addNoCacheHeaders(response);
            }
            if (transportType.supportsCors() && !this.checkOrigin(request, response, new HttpMethod[0])) {
                return;
            }
            transportHandler.handleRequest(request, response, handler, session);
            if (isNewSession && response instanceof ServletServerHttpResponse && HttpStatus.valueOf((int)(status = ((ServletServerHttpResponse)response).getServletResponse().getStatus())).is4xxClientError()) {
                this.sessions.remove(sessionId);
            }
            chain.applyAfterHandshake(request, response, null);
            return;
        }
        catch (SockJsException ex) {
            failure = ex;
        }
        catch (Exception ex) {
            failure = new SockJsException("Uncaught failure for request " + request.getURI(), sessionId, ex);
        }
        finally {
            if (failure == null) return;
            chain.applyAfterHandshake(request, response, (Exception)((Object)failure));
            throw failure;
        }
    }

    @Override
    protected boolean validateRequest(String serverId, String sessionId, String transport) {
        TransportType transportType;
        if (!super.validateRequest(serverId, sessionId, transport)) {
            return false;
        }
        if (!((this.getAllowedOrigins().isEmpty() || this.getAllowedOrigins().contains("*")) && this.getAllowedOriginPatterns().isEmpty() || (transportType = TransportType.fromValue(transport)) != null && transportType.supportsOrigin())) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)("Origin check enabled but transport '" + transport + "' does not support it."));
            }
            return false;
        }
        return true;
    }

    private SockJsSession createSockJsSession(String sessionId, SockJsSessionFactory sessionFactory, WebSocketHandler handler, Map<String, Object> attributes) {
        SockJsSession session = this.sessions.get(sessionId);
        if (session != null) {
            return session;
        }
        if (this.sessionCleanupTask == null) {
            this.scheduleSessionTask();
        }
        session = sessionFactory.createSession(sessionId, handler, attributes);
        this.sessions.put(sessionId, session);
        return session;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void scheduleSessionTask() {
        Map<String, SockJsSession> map = this.sessions;
        synchronized (map) {
            if (this.sessionCleanupTask != null) {
                return;
            }
            this.sessionCleanupTask = this.getTaskScheduler().scheduleAtFixedRate(() -> {
                ArrayList<String> removedIds = new ArrayList<String>();
                for (SockJsSession session : this.sessions.values()) {
                    try {
                        if (session.getTimeSinceLastActive() <= this.getDisconnectDelay()) continue;
                        this.sessions.remove(session.getId());
                        removedIds.add(session.getId());
                        session.close();
                    }
                    catch (Throwable ex) {
                        this.logger.debug((Object)("Failed to close " + session), ex);
                    }
                }
                if (this.logger.isDebugEnabled() && !removedIds.isEmpty()) {
                    this.logger.debug((Object)("Closed " + removedIds.size() + " sessions: " + removedIds));
                }
            }, this.getDisconnectDelay());
        }
    }
}

