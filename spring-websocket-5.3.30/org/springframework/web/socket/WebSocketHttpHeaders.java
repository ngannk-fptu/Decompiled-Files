/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.web.socket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketExtension;

public class WebSocketHttpHeaders
extends HttpHeaders {
    public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";
    public static final String SEC_WEBSOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";
    public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
    public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
    private static final long serialVersionUID = -6644521016187828916L;
    private final HttpHeaders headers;

    public WebSocketHttpHeaders() {
        this(new HttpHeaders());
    }

    public WebSocketHttpHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Deprecated
    public static WebSocketHttpHeaders readOnlyWebSocketHttpHeaders(WebSocketHttpHeaders headers) {
        return new WebSocketHttpHeaders(HttpHeaders.readOnlyHttpHeaders((HttpHeaders)headers));
    }

    public void setSecWebSocketAccept(@Nullable String secWebSocketAccept) {
        this.set(SEC_WEBSOCKET_ACCEPT, secWebSocketAccept);
    }

    @Nullable
    public String getSecWebSocketAccept() {
        return this.getFirst(SEC_WEBSOCKET_ACCEPT);
    }

    public List<WebSocketExtension> getSecWebSocketExtensions() {
        Object values = this.get(SEC_WEBSOCKET_EXTENSIONS);
        if (CollectionUtils.isEmpty((Collection)values)) {
            return Collections.emptyList();
        }
        ArrayList<WebSocketExtension> result = new ArrayList<WebSocketExtension>(values.size());
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            String value = (String)iterator.next();
            result.addAll(WebSocketExtension.parseExtensions(value));
        }
        return result;
    }

    public void setSecWebSocketExtensions(List<WebSocketExtension> extensions) {
        ArrayList<String> result = new ArrayList<String>(extensions.size());
        for (WebSocketExtension extension : extensions) {
            result.add(extension.toString());
        }
        this.set(SEC_WEBSOCKET_EXTENSIONS, this.toCommaDelimitedString(result));
    }

    public void setSecWebSocketKey(@Nullable String secWebSocketKey) {
        this.set(SEC_WEBSOCKET_KEY, secWebSocketKey);
    }

    @Nullable
    public String getSecWebSocketKey() {
        return this.getFirst(SEC_WEBSOCKET_KEY);
    }

    public void setSecWebSocketProtocol(String secWebSocketProtocol) {
        this.set(SEC_WEBSOCKET_PROTOCOL, secWebSocketProtocol);
    }

    public void setSecWebSocketProtocol(List<String> secWebSocketProtocols) {
        this.set(SEC_WEBSOCKET_PROTOCOL, this.toCommaDelimitedString(secWebSocketProtocols));
    }

    public List<String> getSecWebSocketProtocol() {
        Object values = this.get(SEC_WEBSOCKET_PROTOCOL);
        if (CollectionUtils.isEmpty((Collection)values)) {
            return Collections.emptyList();
        }
        if (values.size() == 1) {
            return this.getValuesAsList(SEC_WEBSOCKET_PROTOCOL);
        }
        return values;
    }

    public void setSecWebSocketVersion(@Nullable String secWebSocketVersion) {
        this.set(SEC_WEBSOCKET_VERSION, secWebSocketVersion);
    }

    @Nullable
    public String getSecWebSocketVersion() {
        return this.getFirst(SEC_WEBSOCKET_VERSION);
    }

    @Nullable
    public String getFirst(String headerName) {
        return this.headers.getFirst(headerName);
    }

    public void add(String headerName, @Nullable String headerValue) {
        this.headers.add(headerName, headerValue);
    }

    public void set(String headerName, @Nullable String headerValue) {
        this.headers.set(headerName, headerValue);
    }

    public void setAll(Map<String, String> values) {
        this.headers.setAll(values);
    }

    public Map<String, String> toSingleValueMap() {
        return this.headers.toSingleValueMap();
    }

    public int size() {
        return this.headers.size();
    }

    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    public List<String> get(Object key) {
        return this.headers.get(key);
    }

    public List<String> put(String key, List<String> value) {
        return this.headers.put(key, value);
    }

    public List<String> remove(Object key) {
        return this.headers.remove(key);
    }

    public void putAll(Map<? extends String, ? extends List<String>> m) {
        this.headers.putAll(m);
    }

    public void clear() {
        this.headers.clear();
    }

    public Set<String> keySet() {
        return this.headers.keySet();
    }

    public Collection<List<String>> values() {
        return this.headers.values();
    }

    public Set<Map.Entry<String, List<String>>> entrySet() {
        return this.headers.entrySet();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WebSocketHttpHeaders)) {
            return false;
        }
        WebSocketHttpHeaders otherHeaders = (WebSocketHttpHeaders)((Object)other);
        return this.headers.equals((Object)otherHeaders.headers);
    }

    public int hashCode() {
        return this.headers.hashCode();
    }

    public String toString() {
        return this.headers.toString();
    }
}

