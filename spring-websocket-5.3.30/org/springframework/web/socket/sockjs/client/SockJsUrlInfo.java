/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.IdGenerator
 *  org.springframework.util.JdkIdGenerator
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.util.UriComponentsBuilder;

public class SockJsUrlInfo {
    private static final IdGenerator idGenerator = new JdkIdGenerator();
    private final URI sockJsUrl;
    @Nullable
    private String serverId;
    @Nullable
    private String sessionId;
    @Nullable
    private UUID uuid;

    public SockJsUrlInfo(URI sockJsUrl) {
        this.sockJsUrl = sockJsUrl;
    }

    public URI getSockJsUrl() {
        return this.sockJsUrl;
    }

    public String getServerId() {
        if (this.serverId == null) {
            this.serverId = String.valueOf(Math.abs(this.getUuid().getMostSignificantBits()) % 1000L);
        }
        return this.serverId;
    }

    public String getSessionId() {
        if (this.sessionId == null) {
            this.sessionId = StringUtils.delete((String)this.getUuid().toString(), (String)"-");
        }
        return this.sessionId;
    }

    protected UUID getUuid() {
        if (this.uuid == null) {
            this.uuid = idGenerator.generateId();
        }
        return this.uuid;
    }

    public URI getInfoUrl() {
        return UriComponentsBuilder.fromUri((URI)this.sockJsUrl).scheme(this.getScheme(TransportType.XHR)).pathSegment(new String[]{"info"}).build(true).toUri();
    }

    public URI getTransportUrl(TransportType transportType) {
        return UriComponentsBuilder.fromUri((URI)this.sockJsUrl).scheme(this.getScheme(transportType)).pathSegment(new String[]{this.getServerId()}).pathSegment(new String[]{this.getSessionId()}).pathSegment(new String[]{transportType.toString()}).build(true).toUri();
    }

    private String getScheme(TransportType transportType) {
        String scheme = this.sockJsUrl.getScheme();
        if (TransportType.WEBSOCKET.equals((Object)transportType)) {
            if (!scheme.startsWith("ws")) {
                scheme = "https".equals(scheme) ? "wss" : "ws";
            }
        } else if (!scheme.startsWith("http")) {
            scheme = "wss".equals(scheme) ? "https" : "http";
        }
        return scheme;
    }

    public String toString() {
        return "SockJsUrlInfo[url=" + this.sockJsUrl + "]";
    }
}

