/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.HandshakeResponse
 *  org.apache.tomcat.util.collections.CaseInsensitiveKeyMap
 */
package org.apache.tomcat.websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.websocket.HandshakeResponse;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;

public class WsHandshakeResponse
implements HandshakeResponse {
    private final Map<String, List<String>> headers = new CaseInsensitiveKeyMap();

    public WsHandshakeResponse() {
    }

    public WsHandshakeResponse(Map<String, List<String>> headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (this.headers.containsKey(entry.getKey())) {
                this.headers.get(entry.getKey()).addAll((Collection<String>)entry.getValue());
                continue;
            }
            ArrayList values = new ArrayList(entry.getValue());
            this.headers.put(entry.getKey(), values);
        }
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }
}

