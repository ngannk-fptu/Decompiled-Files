/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpRequest
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.InvalidMediaTypeException
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.DigestUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.cors.CorsConfiguration
 *  org.springframework.web.cors.CorsConfigurationSource
 *  org.springframework.web.util.WebUtils
 */
package org.springframework.web.socket.sockjs.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.util.WebUtils;

public abstract class AbstractSockJsService
implements SockJsService,
CorsConfigurationSource {
    private static final String XFRAME_OPTIONS_HEADER = "X-Frame-Options";
    private static final long ONE_YEAR = TimeUnit.DAYS.toSeconds(365L);
    private static final Random random = new Random();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final TaskScheduler taskScheduler;
    private String name = "SockJSService@" + ObjectUtils.getIdentityHexString((Object)this);
    private String clientLibraryUrl = "https://cdn.jsdelivr.net/sockjs/1.0.0/sockjs.min.js";
    private int streamBytesLimit = 131072;
    private boolean sessionCookieNeeded = true;
    private long heartbeatTime = TimeUnit.SECONDS.toMillis(25L);
    private long disconnectDelay = TimeUnit.SECONDS.toMillis(5L);
    private int httpMessageCacheSize = 100;
    private boolean webSocketEnabled = true;
    private boolean suppressCors = false;
    protected final CorsConfiguration corsConfiguration;
    private final SockJsRequestHandler infoHandler = new InfoHandler();
    private final SockJsRequestHandler iframeHandler = new IframeHandler();

    public AbstractSockJsService(TaskScheduler scheduler) {
        Assert.notNull((Object)scheduler, (String)"TaskScheduler must not be null");
        this.taskScheduler = scheduler;
        this.corsConfiguration = AbstractSockJsService.initCorsConfiguration();
    }

    private static CorsConfiguration initCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.setAllowedOrigins(Collections.emptyList());
        config.setAllowedOriginPatterns(Collections.emptyList());
        config.setAllowCredentials(Boolean.valueOf(true));
        config.setMaxAge(Long.valueOf(ONE_YEAR));
        config.addAllowedHeader("*");
        return config;
    }

    public TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSockJsClientLibraryUrl(String clientLibraryUrl) {
        this.clientLibraryUrl = clientLibraryUrl;
    }

    public String getSockJsClientLibraryUrl() {
        return this.clientLibraryUrl;
    }

    public void setStreamBytesLimit(int streamBytesLimit) {
        this.streamBytesLimit = streamBytesLimit;
    }

    public int getStreamBytesLimit() {
        return this.streamBytesLimit;
    }

    public void setSessionCookieNeeded(boolean sessionCookieNeeded) {
        this.sessionCookieNeeded = sessionCookieNeeded;
    }

    public boolean isSessionCookieNeeded() {
        return this.sessionCookieNeeded;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public long getHeartbeatTime() {
        return this.heartbeatTime;
    }

    public void setDisconnectDelay(long disconnectDelay) {
        this.disconnectDelay = disconnectDelay;
    }

    public long getDisconnectDelay() {
        return this.disconnectDelay;
    }

    public void setHttpMessageCacheSize(int httpMessageCacheSize) {
        this.httpMessageCacheSize = httpMessageCacheSize;
    }

    public int getHttpMessageCacheSize() {
        return this.httpMessageCacheSize;
    }

    public void setWebSocketEnabled(boolean webSocketEnabled) {
        this.webSocketEnabled = webSocketEnabled;
    }

    public boolean isWebSocketEnabled() {
        return this.webSocketEnabled;
    }

    public void setSuppressCors(boolean suppressCors) {
        this.suppressCors = suppressCors;
    }

    public boolean shouldSuppressCors() {
        return this.suppressCors;
    }

    public void setAllowedOrigins(Collection<String> allowedOrigins) {
        Assert.notNull(allowedOrigins, (String)"Allowed origins Collection must not be null");
        this.corsConfiguration.setAllowedOrigins(new ArrayList<String>(allowedOrigins));
    }

    public Collection<String> getAllowedOrigins() {
        return this.corsConfiguration.getAllowedOrigins();
    }

    public void setAllowedOriginPatterns(Collection<String> allowedOriginPatterns) {
        Assert.notNull(allowedOriginPatterns, (String)"Allowed origin patterns Collection must not be null");
        this.corsConfiguration.setAllowedOriginPatterns(new ArrayList<String>(allowedOriginPatterns));
    }

    public Collection<String> getAllowedOriginPatterns() {
        return this.corsConfiguration.getAllowedOriginPatterns();
    }

    @Override
    public final void handleRequest(ServerHttpRequest request, ServerHttpResponse response, @Nullable String sockJsPath, WebSocketHandler wsHandler) throws SockJsException {
        if (sockJsPath == null) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)LogFormatUtils.formatValue((Object)("Expected SockJS path. Failing request: " + request.getURI()), (int)-1, (boolean)true));
            }
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return;
        }
        try {
            request.getHeaders();
        }
        catch (InvalidMediaTypeException invalidMediaTypeException) {
            // empty catch block
        }
        String requestInfo = this.logger.isDebugEnabled() ? request.getMethod() + " " + request.getURI() : null;
        try {
            if (sockJsPath.isEmpty() || sockJsPath.equals("/")) {
                if (requestInfo != null) {
                    this.logger.debug((Object)("Processing transport request: " + requestInfo));
                }
                if ("websocket".equalsIgnoreCase(request.getHeaders().getUpgrade())) {
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return;
                }
                response.getHeaders().setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
                response.getBody().write("Welcome to SockJS!\n".getBytes(StandardCharsets.UTF_8));
            } else if (sockJsPath.equals("/info")) {
                if (requestInfo != null) {
                    this.logger.debug((Object)("Processing transport request: " + requestInfo));
                }
                this.infoHandler.handle(request, response);
            } else if (sockJsPath.matches("/iframe[0-9-.a-z_]*.html")) {
                if (!this.getAllowedOrigins().isEmpty() && !this.getAllowedOrigins().contains("*") || !this.getAllowedOriginPatterns().isEmpty()) {
                    if (requestInfo != null) {
                        this.logger.debug((Object)("Iframe support is disabled when an origin check is required. Ignoring transport request: " + requestInfo));
                    }
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return;
                }
                if (this.getAllowedOrigins().isEmpty()) {
                    response.getHeaders().add(XFRAME_OPTIONS_HEADER, "SAMEORIGIN");
                }
                if (requestInfo != null) {
                    this.logger.debug((Object)("Processing transport request: " + requestInfo));
                }
                this.iframeHandler.handle(request, response);
            } else if (sockJsPath.equals("/websocket")) {
                if (this.isWebSocketEnabled()) {
                    if (requestInfo != null) {
                        this.logger.debug((Object)("Processing transport request: " + requestInfo));
                    }
                    this.handleRawWebSocketRequest(request, response, wsHandler);
                } else if (requestInfo != null) {
                    this.logger.debug((Object)("WebSocket disabled. Ignoring transport request: " + requestInfo));
                }
            } else {
                String[] pathSegments = StringUtils.tokenizeToStringArray((String)sockJsPath.substring(1), (String)"/");
                if (pathSegments.length != 3) {
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn((Object)LogFormatUtils.formatValue((Object)("Invalid SockJS path '" + sockJsPath + "' - required to have 3 path segments"), (int)-1, (boolean)true));
                    }
                    if (requestInfo != null) {
                        this.logger.debug((Object)("Ignoring transport request: " + requestInfo));
                    }
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return;
                }
                String serverId = pathSegments[0];
                String sessionId = pathSegments[1];
                String transport = pathSegments[2];
                if (!this.isWebSocketEnabled() && transport.equals("websocket")) {
                    if (requestInfo != null) {
                        this.logger.debug((Object)("WebSocket disabled. Ignoring transport request: " + requestInfo));
                    }
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return;
                }
                if (!this.validateRequest(serverId, sessionId, transport) || !this.validatePath(request)) {
                    if (requestInfo != null) {
                        this.logger.debug((Object)("Ignoring transport request: " + requestInfo));
                    }
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return;
                }
                if (requestInfo != null) {
                    this.logger.debug((Object)("Processing transport request: " + requestInfo));
                }
                this.handleTransportRequest(request, response, wsHandler, sessionId, transport);
            }
            response.close();
        }
        catch (IOException ex) {
            throw new SockJsException("Failed to write to the response", null, ex);
        }
    }

    protected boolean validateRequest(String serverId, String sessionId, String transport) {
        if (!(StringUtils.hasText((String)serverId) && StringUtils.hasText((String)sessionId) && StringUtils.hasText((String)transport))) {
            this.logger.warn((Object)"No server, session, or transport path segment in SockJS request.");
            return false;
        }
        if (serverId.contains(".") || sessionId.contains(".")) {
            this.logger.warn((Object)"Either server or session contains a \".\" which is not allowed by SockJS protocol.");
            return false;
        }
        return true;
    }

    private boolean validatePath(ServerHttpRequest request) {
        int index;
        String path = request.getURI().getPath();
        return path.indexOf(59, index = path.lastIndexOf(47) + 1) == -1;
    }

    protected boolean checkOrigin(ServerHttpRequest request, ServerHttpResponse response, HttpMethod ... httpMethods) throws IOException {
        if (WebUtils.isSameOrigin((HttpRequest)request)) {
            return true;
        }
        if (this.corsConfiguration.checkOrigin(request.getHeaders().getOrigin()) == null) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)("Origin header value '" + request.getHeaders().getOrigin() + "' not allowed."));
            }
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        return true;
    }

    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        if (!this.suppressCors && request.getHeader("Origin") != null) {
            return this.corsConfiguration;
        }
        return null;
    }

    protected void addCacheHeaders(ServerHttpResponse response) {
        response.getHeaders().setCacheControl("public, max-age=" + ONE_YEAR);
        response.getHeaders().setExpires(System.currentTimeMillis() + ONE_YEAR * 1000L);
    }

    protected void addNoCacheHeaders(ServerHttpResponse response) {
        response.getHeaders().setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
    }

    protected void sendMethodNotAllowed(ServerHttpResponse response, HttpMethod ... httpMethods) {
        this.logger.warn((Object)"Sending Method Not Allowed (405)");
        response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
        response.getHeaders().setAllow(new LinkedHashSet<HttpMethod>(Arrays.asList(httpMethods)));
    }

    protected abstract void handleRawWebSocketRequest(ServerHttpRequest var1, ServerHttpResponse var2, WebSocketHandler var3) throws IOException;

    protected abstract void handleTransportRequest(ServerHttpRequest var1, ServerHttpResponse var2, WebSocketHandler var3, String var4, String var5) throws SockJsException;

    private class IframeHandler
    implements SockJsRequestHandler {
        private static final String IFRAME_CONTENT = "<!DOCTYPE html>\n<html>\n<head>\n  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n  <title>SockJS iframe</title>\n  <script>\n    document.domain = document.domain;\n    _sockjs_onload = function(){SockJS.bootstrap_iframe();};\n  </script>\n  <script src=\"%s\"></script>\n</head>\n<body>\n  <h2>Don't panic!</h2>\n  <p>This is a SockJS hidden iframe. It's used for cross domain magic.</p>\n</body>\n</html>";

        private IframeHandler() {
        }

        @Override
        public void handle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
            if (request.getMethod() != HttpMethod.GET) {
                AbstractSockJsService.this.sendMethodNotAllowed(response, HttpMethod.GET);
                return;
            }
            String content = String.format(IFRAME_CONTENT, AbstractSockJsService.this.getSockJsClientLibraryUrl());
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            StringBuilder builder = new StringBuilder("\"0");
            DigestUtils.appendMd5DigestAsHex((byte[])contentBytes, (StringBuilder)builder);
            builder.append('\"');
            String etagValue = builder.toString();
            List ifNoneMatch = request.getHeaders().getIfNoneMatch();
            if (!CollectionUtils.isEmpty((Collection)ifNoneMatch) && ((String)ifNoneMatch.get(0)).equals(etagValue)) {
                response.setStatusCode(HttpStatus.NOT_MODIFIED);
                return;
            }
            response.getHeaders().setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));
            response.getHeaders().setContentLength((long)contentBytes.length);
            AbstractSockJsService.this.addNoCacheHeaders(response);
            response.getHeaders().setETag(etagValue);
            response.getBody().write(contentBytes);
        }
    }

    private class InfoHandler
    implements SockJsRequestHandler {
        private static final String INFO_CONTENT = "{\"entropy\":%s,\"origins\":[\"*:*\"],\"cookie_needed\":%s,\"websocket\":%s}";

        private InfoHandler() {
        }

        @Override
        public void handle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
            if (request.getMethod() == HttpMethod.GET) {
                AbstractSockJsService.this.addNoCacheHeaders(response);
                if (AbstractSockJsService.this.checkOrigin(request, response, new HttpMethod[0])) {
                    response.getHeaders().setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
                    String content = String.format(INFO_CONTENT, random.nextInt(), AbstractSockJsService.this.isSessionCookieNeeded(), AbstractSockJsService.this.isWebSocketEnabled());
                    response.getBody().write(content.getBytes());
                }
            } else if (request.getMethod() == HttpMethod.OPTIONS) {
                if (AbstractSockJsService.this.checkOrigin(request, response, new HttpMethod[0])) {
                    AbstractSockJsService.this.addCacheHeaders(response);
                    response.setStatusCode(HttpStatus.NO_CONTENT);
                }
            } else {
                AbstractSockJsService.this.sendMethodNotAllowed(response, HttpMethod.GET, HttpMethod.OPTIONS);
            }
        }
    }

    private static interface SockJsRequestHandler {
        public void handle(ServerHttpRequest var1, ServerHttpResponse var2) throws IOException;
    }
}

