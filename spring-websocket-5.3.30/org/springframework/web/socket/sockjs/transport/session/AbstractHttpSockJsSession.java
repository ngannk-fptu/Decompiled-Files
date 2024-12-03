/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.server.ServerHttpAsyncRequestControl
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.filter.ShallowEtagHeaderFilter
 */
package org.springframework.web.socket.sockjs.transport.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.session.AbstractSockJsSession;

public abstract class AbstractHttpSockJsSession
extends AbstractSockJsSession {
    private final Queue<String> messageCache;
    @Nullable
    private volatile URI uri;
    @Nullable
    private volatile HttpHeaders handshakeHeaders;
    @Nullable
    private volatile Principal principal;
    @Nullable
    private volatile InetSocketAddress localAddress;
    @Nullable
    private volatile InetSocketAddress remoteAddress;
    @Nullable
    private volatile String acceptedProtocol;
    @Nullable
    private volatile ServerHttpResponse response;
    @Nullable
    private volatile SockJsFrameFormat frameFormat;
    @Nullable
    private volatile ServerHttpAsyncRequestControl asyncRequestControl;
    private boolean readyToSend;

    public AbstractHttpSockJsSession(String id, SockJsServiceConfig config, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        super(id, config, wsHandler, attributes);
        this.messageCache = new LinkedBlockingQueue<String>(config.getHttpMessageCacheSize());
    }

    @Override
    public URI getUri() {
        URI uri = this.uri;
        Assert.state((uri != null ? 1 : 0) != 0, (String)"No initial request yet");
        return uri;
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        HttpHeaders headers = this.handshakeHeaders;
        Assert.state((headers != null ? 1 : 0) != 0, (String)"No initial request yet");
        return headers;
    }

    @Override
    @Nullable
    public Principal getPrincipal() {
        return this.principal;
    }

    @Override
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setAcceptedProtocol(@Nullable String protocol) {
        this.acceptedProtocol = protocol;
    }

    @Override
    @Nullable
    public String getAcceptedProtocol() {
        return this.acceptedProtocol;
    }

    protected Queue<String> getMessageCache() {
        return this.messageCache;
    }

    @Override
    public boolean isActive() {
        ServerHttpAsyncRequestControl control = this.asyncRequestControl;
        return control != null && !control.isCompleted();
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
    }

    @Override
    public int getTextMessageSizeLimit() {
        return -1;
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return -1;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return Collections.emptyList();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleInitialRequest(ServerHttpRequest request, ServerHttpResponse response, SockJsFrameFormat frameFormat) throws SockJsException {
        this.uri = request.getURI();
        this.handshakeHeaders = request.getHeaders();
        this.principal = request.getPrincipal();
        try {
            this.localAddress = request.getLocalAddress();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.remoteAddress = request.getRemoteAddress();
        }
        catch (Exception exception) {
            // empty catch block
        }
        Object object = this.responseLock;
        synchronized (object) {
            try {
                ServerHttpAsyncRequestControl control;
                this.response = response;
                this.frameFormat = frameFormat;
                this.asyncRequestControl = control = request.getAsyncRequestControl(response);
                control.start(-1L);
                this.disableShallowEtagHeaderFilter(request);
                this.delegateConnectionEstablished();
                this.handleRequestInternal(request, response, true);
                this.readyToSend = this.isActive();
            }
            catch (Throwable ex) {
                this.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
                throw new SockJsTransportFailureException("Failed to open session", this.getId(), ex);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleSuccessiveRequest(ServerHttpRequest request, ServerHttpResponse response, SockJsFrameFormat frameFormat) throws SockJsException {
        Object object = this.responseLock;
        synchronized (object) {
            try {
                ServerHttpAsyncRequestControl control;
                if (this.isClosed()) {
                    String formattedFrame = frameFormat.format(SockJsFrame.closeFrameGoAway());
                    response.getBody().write(formattedFrame.getBytes(SockJsFrame.CHARSET));
                    return;
                }
                this.response = response;
                this.frameFormat = frameFormat;
                this.asyncRequestControl = control = request.getAsyncRequestControl(response);
                control.start(-1L);
                this.disableShallowEtagHeaderFilter(request);
                this.handleRequestInternal(request, response, false);
                this.readyToSend = this.isActive();
            }
            catch (Throwable ex) {
                this.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
                throw new SockJsTransportFailureException("Failed to handle SockJS receive request", this.getId(), ex);
            }
        }
    }

    private void disableShallowEtagHeaderFilter(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
            ShallowEtagHeaderFilter.disableContentCaching((ServletRequest)servletRequest);
        }
    }

    protected abstract void handleRequestInternal(ServerHttpRequest var1, ServerHttpResponse var2, boolean var3) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected final void sendMessageInternal(String message) throws SockJsTransportFailureException {
        Object object = this.responseLock;
        synchronized (object) {
            this.messageCache.add(message);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)(this.messageCache.size() + " message(s) to flush in session " + this.getId()));
            }
            if (this.isActive() && this.readyToSend) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)"Session is active, ready to flush.");
                }
                this.cancelHeartbeat();
                this.flushCache();
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)"Session is not active, not ready to flush.");
            }
        }
    }

    protected abstract void flushCache() throws SockJsTransportFailureException;

    @Override
    protected void disconnect(CloseStatus status) {
        this.resetRequest();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void resetRequest() {
        Object object = this.responseLock;
        synchronized (object) {
            ServerHttpAsyncRequestControl control = this.asyncRequestControl;
            this.asyncRequestControl = null;
            this.readyToSend = false;
            this.response = null;
            this.updateLastActiveTime();
            if (control != null && !control.isCompleted() && control.isStarted()) {
                try {
                    control.complete();
                }
                catch (Throwable ex) {
                    this.logger.debug((Object)("Failed to complete request: " + ex.getMessage()));
                }
            }
        }
    }

    @Override
    protected void writeFrameInternal(SockJsFrame frame) throws IOException {
        if (this.isActive()) {
            SockJsFrameFormat frameFormat = this.frameFormat;
            ServerHttpResponse response = this.response;
            if (frameFormat != null && response != null) {
                String formattedFrame = frameFormat.format(frame);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Writing to HTTP response: " + formattedFrame));
                }
                response.getBody().write(formattedFrame.getBytes(SockJsFrame.CHARSET));
                response.flush();
            }
        }
    }
}

