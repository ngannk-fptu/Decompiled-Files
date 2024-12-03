/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.Lifecycle
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.SettableListenableFuture
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.sockjs.client.DefaultTransportRequest;
import org.springframework.web.socket.sockjs.client.InfoReceiver;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsUrlInfo;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.util.UriComponentsBuilder;

public class SockJsClient
implements WebSocketClient,
Lifecycle {
    private static final boolean jackson2Present = ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)SockJsClient.class.getClassLoader());
    private static final Log logger = LogFactory.getLog(SockJsClient.class);
    private static final Set<String> supportedProtocols = new HashSet<String>(4);
    private final List<Transport> transports;
    @Nullable
    private String[] httpHeaderNames;
    private InfoReceiver infoReceiver;
    @Nullable
    private SockJsMessageCodec messageCodec;
    @Nullable
    private TaskScheduler connectTimeoutScheduler;
    private volatile boolean running;
    private final Map<URI, ServerInfo> serverInfoCache = new ConcurrentHashMap<URI, ServerInfo>();

    public SockJsClient(List<Transport> transports) {
        Assert.notEmpty(transports, (String)"No transports provided");
        this.transports = new ArrayList<Transport>(transports);
        this.infoReceiver = SockJsClient.initInfoReceiver(transports);
        if (jackson2Present) {
            this.messageCodec = new Jackson2SockJsMessageCodec();
        }
    }

    private static InfoReceiver initInfoReceiver(List<Transport> transports) {
        for (Transport transport : transports) {
            if (!(transport instanceof InfoReceiver)) continue;
            return (InfoReceiver)((Object)transport);
        }
        return new RestTemplateXhrTransport();
    }

    public void setHttpHeaderNames(String ... httpHeaderNames) {
        this.httpHeaderNames = httpHeaderNames;
    }

    @Nullable
    public String[] getHttpHeaderNames() {
        return this.httpHeaderNames;
    }

    public void setInfoReceiver(InfoReceiver infoReceiver) {
        Assert.notNull((Object)infoReceiver, (String)"InfoReceiver is required");
        this.infoReceiver = infoReceiver;
    }

    public InfoReceiver getInfoReceiver() {
        return this.infoReceiver;
    }

    public void setMessageCodec(SockJsMessageCodec messageCodec) {
        Assert.notNull((Object)messageCodec, (String)"SockJsMessageCodec is required");
        this.messageCodec = messageCodec;
    }

    public SockJsMessageCodec getMessageCodec() {
        Assert.state((this.messageCodec != null ? 1 : 0) != 0, (String)"No SockJsMessageCodec set");
        return this.messageCodec;
    }

    public void setConnectTimeoutScheduler(TaskScheduler connectTimeoutScheduler) {
        this.connectTimeoutScheduler = connectTimeoutScheduler;
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            for (Transport transport : this.transports) {
                Lifecycle lifecycle;
                if (!(transport instanceof Lifecycle) || (lifecycle = (Lifecycle)transport).isRunning()) continue;
                lifecycle.start();
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            for (Transport transport : this.transports) {
                Lifecycle lifecycle;
                if (!(transport instanceof Lifecycle) || !(lifecycle = (Lifecycle)transport).isRunning()) continue;
                lifecycle.stop();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler handler, String uriTemplate, Object ... uriVars) {
        Assert.notNull((Object)uriTemplate, (String)"uriTemplate must not be null");
        URI uri = UriComponentsBuilder.fromUriString((String)uriTemplate).buildAndExpand(uriVars).encode().toUri();
        return this.doHandshake(handler, null, uri);
    }

    @Override
    public final ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler handler, @Nullable WebSocketHttpHeaders headers, URI url) {
        Assert.notNull((Object)handler, (String)"WebSocketHandler is required");
        Assert.notNull((Object)url, (String)"URL is required");
        String scheme = url.getScheme();
        if (!supportedProtocols.contains(scheme)) {
            throw new IllegalArgumentException("Invalid scheme: '" + scheme + "'");
        }
        SettableListenableFuture connectFuture = new SettableListenableFuture();
        try {
            SockJsUrlInfo sockJsUrlInfo = new SockJsUrlInfo(url);
            ServerInfo serverInfo = this.getServerInfo(sockJsUrlInfo, this.getHttpRequestHeaders(headers));
            this.createRequest(sockJsUrlInfo, headers, serverInfo).connect(handler, (SettableListenableFuture<WebSocketSession>)connectFuture);
        }
        catch (Exception exception) {
            if (logger.isErrorEnabled()) {
                logger.error((Object)("Initial SockJS \"Info\" request to server failed, url=" + url), (Throwable)exception);
            }
            connectFuture.setException((Throwable)exception);
        }
        return connectFuture;
    }

    @Nullable
    private HttpHeaders getHttpRequestHeaders(@Nullable HttpHeaders webSocketHttpHeaders) {
        if (this.getHttpHeaderNames() == null || webSocketHttpHeaders == null) {
            return webSocketHttpHeaders;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String name : this.getHttpHeaderNames()) {
            List values = webSocketHttpHeaders.get((Object)name);
            if (values == null) continue;
            httpHeaders.put(name, values);
        }
        return httpHeaders;
    }

    private ServerInfo getServerInfo(SockJsUrlInfo sockJsUrlInfo, @Nullable HttpHeaders headers) {
        URI infoUrl = sockJsUrlInfo.getInfoUrl();
        ServerInfo info = this.serverInfoCache.get(infoUrl);
        if (info == null) {
            long start = System.currentTimeMillis();
            String response = this.infoReceiver.executeInfoRequest(infoUrl, headers);
            long infoRequestTime = System.currentTimeMillis() - start;
            info = new ServerInfo(response, infoRequestTime);
            this.serverInfoCache.put(infoUrl, info);
        }
        return info;
    }

    private DefaultTransportRequest createRequest(SockJsUrlInfo urlInfo, @Nullable HttpHeaders headers, ServerInfo serverInfo) {
        ArrayList<DefaultTransportRequest> requests = new ArrayList<DefaultTransportRequest>(this.transports.size());
        for (Transport transport : this.transports) {
            for (TransportType type : transport.getTransportTypes()) {
                if (!serverInfo.isWebSocketEnabled() && TransportType.WEBSOCKET.equals((Object)type)) continue;
                requests.add(new DefaultTransportRequest(urlInfo, headers, this.getHttpRequestHeaders(headers), transport, type, this.getMessageCodec()));
            }
        }
        if (CollectionUtils.isEmpty(requests)) {
            throw new IllegalStateException("No transports: " + urlInfo + ", webSocketEnabled=" + serverInfo.isWebSocketEnabled());
        }
        for (int i = 0; i < requests.size() - 1; ++i) {
            DefaultTransportRequest request = (DefaultTransportRequest)requests.get(i);
            Principal user = this.getUser();
            if (user != null) {
                request.setUser(user);
            }
            if (this.connectTimeoutScheduler != null) {
                request.setTimeoutValue(serverInfo.getRetransmissionTimeout());
                request.setTimeoutScheduler(this.connectTimeoutScheduler);
            }
            request.setFallbackRequest((DefaultTransportRequest)requests.get(i + 1));
        }
        return (DefaultTransportRequest)requests.get(0);
    }

    @Nullable
    protected Principal getUser() {
        return null;
    }

    public void clearServerInfoCache() {
        this.serverInfoCache.clear();
    }

    static {
        supportedProtocols.add("ws");
        supportedProtocols.add("wss");
        supportedProtocols.add("http");
        supportedProtocols.add("https");
    }

    private static class ServerInfo {
        private final boolean webSocketEnabled;
        private final long responseTime;

        public ServerInfo(String response, long responseTime) {
            this.responseTime = responseTime;
            this.webSocketEnabled = !response.matches(".*[\"']websocket[\"']\\s*:\\s*false.*");
        }

        public boolean isWebSocketEnabled() {
            return this.webSocketEnabled;
        }

        public long getRetransmissionTimeout() {
            return this.responseTime > 100L ? 4L * this.responseTime : this.responseTime + 300L;
        }
    }
}

