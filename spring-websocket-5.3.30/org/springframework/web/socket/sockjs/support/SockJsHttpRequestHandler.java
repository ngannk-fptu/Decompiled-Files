/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.context.Lifecycle
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.HttpRequestHandler
 *  org.springframework.web.context.ServletContextAware
 *  org.springframework.web.cors.CorsConfiguration
 *  org.springframework.web.cors.CorsConfigurationSource
 *  org.springframework.web.servlet.HandlerMapping
 */
package org.springframework.web.socket.sockjs.support;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.Lifecycle;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsService;

public class SockJsHttpRequestHandler
implements HttpRequestHandler,
CorsConfigurationSource,
Lifecycle,
ServletContextAware {
    private final SockJsService sockJsService;
    private final WebSocketHandler webSocketHandler;
    private volatile boolean running;

    public SockJsHttpRequestHandler(SockJsService sockJsService, WebSocketHandler webSocketHandler) {
        Assert.notNull((Object)sockJsService, (String)"SockJsService must not be null");
        Assert.notNull((Object)webSocketHandler, (String)"WebSocketHandler must not be null");
        this.sockJsService = sockJsService;
        this.webSocketHandler = new ExceptionWebSocketHandlerDecorator(new LoggingWebSocketHandlerDecorator(webSocketHandler));
    }

    public SockJsService getSockJsService() {
        return this.sockJsService;
    }

    public WebSocketHandler getWebSocketHandler() {
        return this.webSocketHandler;
    }

    public void setServletContext(ServletContext servletContext) {
        if (this.sockJsService instanceof ServletContextAware) {
            ((ServletContextAware)this.sockJsService).setServletContext(servletContext);
        }
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            if (this.sockJsService instanceof Lifecycle) {
                ((Lifecycle)this.sockJsService).start();
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            if (this.sockJsService instanceof Lifecycle) {
                ((Lifecycle)this.sockJsService).stop();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void handleRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        ServletServerHttpResponse response = new ServletServerHttpResponse(servletResponse);
        try {
            this.sockJsService.handleRequest((ServerHttpRequest)request, (ServerHttpResponse)response, this.getSockJsPath(servletRequest), this.webSocketHandler);
        }
        catch (Exception ex) {
            throw new SockJsException("Uncaught failure in SockJS request, uri=" + request.getURI(), ex);
        }
    }

    private String getSockJsPath(HttpServletRequest servletRequest) {
        String attribute = HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;
        String path = (String)servletRequest.getAttribute(attribute);
        return path.length() > 0 && path.charAt(0) != '/' ? "/" + path : path;
    }

    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        if (this.sockJsService instanceof CorsConfigurationSource) {
            return ((CorsConfigurationSource)this.sockJsService).getCorsConfiguration(request);
        }
        return null;
    }
}

