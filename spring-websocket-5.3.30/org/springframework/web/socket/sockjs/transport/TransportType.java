/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpMethod
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.sockjs.transport;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

public enum TransportType {
    WEBSOCKET("websocket", HttpMethod.GET, "origin"),
    XHR("xhr", HttpMethod.POST, "cors", "jsessionid", "no_cache"),
    XHR_SEND("xhr_send", HttpMethod.POST, "cors", "jsessionid", "no_cache"),
    XHR_STREAMING("xhr_streaming", HttpMethod.POST, "cors", "jsessionid", "no_cache"),
    EVENT_SOURCE("eventsource", HttpMethod.GET, "origin", "jsessionid", "no_cache"),
    HTML_FILE("htmlfile", HttpMethod.GET, "cors", "jsessionid", "no_cache");

    private static final Map<String, TransportType> TRANSPORT_TYPES;
    private final String value;
    private final HttpMethod httpMethod;
    private final List<String> headerHints;

    @Nullable
    public static TransportType fromValue(String value) {
        return TRANSPORT_TYPES.get(value);
    }

    private TransportType(String value, HttpMethod httpMethod, String ... headerHints) {
        this.value = value;
        this.httpMethod = httpMethod;
        this.headerHints = Arrays.asList(headerHints);
    }

    public String value() {
        return this.value;
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public boolean sendsNoCacheInstruction() {
        return this.headerHints.contains("no_cache");
    }

    public boolean sendsSessionCookie() {
        return this.headerHints.contains("jsessionid");
    }

    public boolean supportsCors() {
        return this.headerHints.contains("cors");
    }

    public boolean supportsOrigin() {
        return this.headerHints.contains("cors") || this.headerHints.contains("origin");
    }

    public String toString() {
        return this.value;
    }

    static {
        HashMap<String, TransportType> transportTypes = new HashMap<String, TransportType>();
        for (TransportType type : TransportType.values()) {
            transportTypes.put(type.value, type);
        }
        TRANSPORT_TYPES = Collections.unmodifiableMap(transportTypes);
    }
}

