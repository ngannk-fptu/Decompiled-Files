/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.Lifecycle
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.HttpRequestHandler
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.socket.server.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HandshakeInterceptorChain;

public class WebSocketHttpRequestHandler
implements HttpRequestHandler,
Lifecycle,
ServletContextAware {
    private static final Log logger = LogFactory.getLog(WebSocketHttpRequestHandler.class);
    private final WebSocketHandler wsHandler;
    private final HandshakeHandler handshakeHandler;
    private final List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();
    private volatile boolean running;

    public WebSocketHttpRequestHandler(WebSocketHandler wsHandler) {
        this(wsHandler, new DefaultHandshakeHandler());
    }

    public WebSocketHttpRequestHandler(WebSocketHandler wsHandler, HandshakeHandler handshakeHandler) {
        Assert.notNull((Object)wsHandler, (String)"wsHandler must not be null");
        Assert.notNull((Object)handshakeHandler, (String)"handshakeHandler must not be null");
        this.wsHandler = this.decorate(wsHandler);
        this.handshakeHandler = handshakeHandler;
    }

    protected WebSocketHandler decorate(WebSocketHandler handler) {
        return new ExceptionWebSocketHandlerDecorator(new LoggingWebSocketHandlerDecorator(handler));
    }

    public WebSocketHandler getWebSocketHandler() {
        return this.wsHandler;
    }

    public HandshakeHandler getHandshakeHandler() {
        return this.handshakeHandler;
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

    public void setServletContext(ServletContext servletContext) {
        if (this.handshakeHandler instanceof ServletContextAware) {
            ((ServletContextAware)this.handshakeHandler).setServletContext(servletContext);
        }
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            if (this.handshakeHandler instanceof Lifecycle) {
                ((Lifecycle)this.handshakeHandler).start();
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            if (this.handshakeHandler instanceof Lifecycle) {
                ((Lifecycle)this.handshakeHandler).stop();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServletServerHttpResponse response = new ServletServerHttpResponse(servletResponse);
        HandshakeInterceptorChain chain = new HandshakeInterceptorChain(this.interceptors, this.wsHandler);
        HandshakeFailureException failure = null;
        try {
            HashMap<String, Object> attributes;
            if (logger.isDebugEnabled()) {
                logger.debug((Object)(servletRequest.getMethod() + " " + servletRequest.getRequestURI()));
            }
            if (!chain.applyBeforeHandshake((ServerHttpRequest)request, (ServerHttpResponse)response, attributes = new HashMap<String, Object>())) {
                return;
            }
            this.handshakeHandler.doHandshake((ServerHttpRequest)request, (ServerHttpResponse)response, this.wsHandler, attributes);
            chain.applyAfterHandshake((ServerHttpRequest)request, (ServerHttpResponse)response, null);
        }
        catch (HandshakeFailureException ex) {
            failure = ex;
        }
        catch (Exception ex) {
            failure = new HandshakeFailureException("Uncaught failure for request " + request.getURI(), ex);
        }
        finally {
            if (failure != null) {
                chain.applyAfterHandshake((ServerHttpRequest)request, (ServerHttpResponse)response, (Exception)((Object)failure));
                response.close();
                throw failure;
            }
            response.close();
        }
    }
}

