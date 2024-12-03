/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.websocket.server.HandshakeRequest
 *  org.apache.tomcat.util.collections.CaseInsensitiveKeyMap
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.HandshakeRequest;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.apache.tomcat.util.res.StringManager;

public class WsHandshakeRequest
implements HandshakeRequest {
    private static final StringManager sm = StringManager.getManager(WsHandshakeRequest.class);
    private final URI requestUri;
    private final Map<String, List<String>> parameterMap;
    private final String queryString;
    private final Principal userPrincipal;
    private final Map<String, List<String>> headers;
    private final Object httpSession;
    private volatile HttpServletRequest request;

    public WsHandshakeRequest(HttpServletRequest request, Map<String, String> pathParams) {
        this.request = request;
        this.queryString = request.getQueryString();
        this.userPrincipal = request.getUserPrincipal();
        this.httpSession = request.getSession(false);
        this.requestUri = WsHandshakeRequest.buildRequestUri(request);
        Map originalParameters = request.getParameterMap();
        HashMap<String, List<String>> newParameters = new HashMap<String, List<String>>(originalParameters.size());
        for (Map.Entry entry : originalParameters.entrySet()) {
            newParameters.put((String)entry.getKey(), Collections.unmodifiableList(Arrays.asList((String[])entry.getValue())));
        }
        for (Map.Entry<Object, Object> entry : pathParams.entrySet()) {
            newParameters.put((String)entry.getKey(), Collections.singletonList((String)entry.getValue()));
        }
        this.parameterMap = Collections.unmodifiableMap(newParameters);
        CaseInsensitiveKeyMap newHeaders = new CaseInsensitiveKeyMap();
        Enumeration enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String headerName = (String)enumeration.nextElement();
            newHeaders.put(headerName, Collections.unmodifiableList(Collections.list(request.getHeaders(headerName))));
        }
        this.headers = Collections.unmodifiableMap(newHeaders);
    }

    public URI getRequestURI() {
        return this.requestUri;
    }

    public Map<String, List<String>> getParameterMap() {
        return this.parameterMap;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public boolean isUserInRole(String role) {
        if (this.request == null) {
            throw new IllegalStateException();
        }
        return this.request.isUserInRole(role);
    }

    public Object getHttpSession() {
        return this.httpSession;
    }

    void finished() {
        this.request = null;
    }

    private static URI buildRequestUri(HttpServletRequest req) {
        StringBuilder uri = new StringBuilder();
        String scheme = req.getScheme();
        int port = req.getServerPort();
        if (port < 0) {
            port = 80;
        }
        if ("http".equals(scheme)) {
            uri.append("ws");
        } else if ("https".equals(scheme)) {
            uri.append("wss");
        } else if ("wss".equals(scheme) || "ws".equals(scheme)) {
            uri.append(scheme);
        } else {
            throw new IllegalArgumentException(sm.getString("wsHandshakeRequest.unknownScheme", new Object[]{scheme}));
        }
        uri.append("://");
        uri.append(req.getServerName());
        if (scheme.equals("http") && port != 80 || scheme.equals("ws") && port != 80 || scheme.equals("wss") && port != 443 || scheme.equals("https") && port != 443) {
            uri.append(':');
            uri.append(port);
        }
        uri.append(req.getRequestURI());
        if (req.getQueryString() != null) {
            uri.append('?');
            uri.append(req.getQueryString());
        }
        try {
            return new URI(uri.toString());
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(sm.getString("wsHandshakeRequest.invalidUri", new Object[]{uri.toString()}), e);
        }
    }
}

