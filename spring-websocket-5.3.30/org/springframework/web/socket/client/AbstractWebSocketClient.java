/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.socket.client;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractWebSocketClient
implements WebSocketClient {
    private static final Set<String> specialHeaders = new HashSet<String>();
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler webSocketHandler, String uriTemplate, Object ... uriVars) {
        Assert.notNull((Object)uriTemplate, (String)"'uriTemplate' must not be null");
        URI uri = UriComponentsBuilder.fromUriString((String)uriTemplate).buildAndExpand(uriVars).encode().toUri();
        return this.doHandshake(webSocketHandler, null, uri);
    }

    @Override
    public final ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler webSocketHandler, @Nullable WebSocketHttpHeaders headers, URI uri) {
        Assert.notNull((Object)webSocketHandler, (String)"WebSocketHandler must not be null");
        this.assertUri(uri);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Connecting to " + uri));
        }
        HttpHeaders headersToUse = new HttpHeaders();
        if (headers != null) {
            headers.forEach((header, values) -> {
                if (values != null && !specialHeaders.contains(header.toLowerCase())) {
                    headersToUse.put(header, values);
                }
            });
        }
        List<String> subProtocols = headers != null ? headers.getSecWebSocketProtocol() : Collections.emptyList();
        List<WebSocketExtension> extensions = headers != null ? headers.getSecWebSocketExtensions() : Collections.emptyList();
        return this.doHandshakeInternal(webSocketHandler, headersToUse, uri, subProtocols, extensions, Collections.emptyMap());
    }

    protected void assertUri(URI uri) {
        Assert.notNull((Object)uri, (String)"URI must not be null");
        String scheme = uri.getScheme();
        if (!"ws".equals(scheme) && !"wss".equals(scheme)) {
            throw new IllegalArgumentException("Invalid scheme: " + scheme);
        }
    }

    protected abstract ListenableFuture<WebSocketSession> doHandshakeInternal(WebSocketHandler var1, HttpHeaders var2, URI var3, List<String> var4, List<WebSocketExtension> var5, Map<String, Object> var6);

    static {
        specialHeaders.add("cache-control");
        specialHeaders.add("connection");
        specialHeaders.add("host");
        specialHeaders.add("sec-websocket-extensions");
        specialHeaders.add("sec-websocket-key");
        specialHeaders.add("sec-websocket-protocol");
        specialHeaders.add("sec-websocket-version");
        specialHeaders.add("pragma");
        specialHeaders.add("upgrade");
    }
}

