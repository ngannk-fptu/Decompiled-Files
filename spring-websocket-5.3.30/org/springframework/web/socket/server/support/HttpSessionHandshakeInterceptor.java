/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.server.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class HttpSessionHandshakeInterceptor
implements HandshakeInterceptor {
    public static final String HTTP_SESSION_ID_ATTR_NAME = "HTTP.SESSION.ID";
    private final Collection<String> attributeNames;
    private boolean copyAllAttributes;
    private boolean copyHttpSessionId = true;
    private boolean createSession;

    public HttpSessionHandshakeInterceptor() {
        this.attributeNames = Collections.emptyList();
        this.copyAllAttributes = true;
    }

    public HttpSessionHandshakeInterceptor(Collection<String> attributeNames) {
        this.attributeNames = Collections.unmodifiableCollection(attributeNames);
        this.copyAllAttributes = false;
    }

    public Collection<String> getAttributeNames() {
        return this.attributeNames;
    }

    public void setCopyAllAttributes(boolean copyAllAttributes) {
        this.copyAllAttributes = copyAllAttributes;
    }

    public boolean isCopyAllAttributes() {
        return this.copyAllAttributes;
    }

    public void setCopyHttpSessionId(boolean copyHttpSessionId) {
        this.copyHttpSessionId = copyHttpSessionId;
    }

    public boolean isCopyHttpSessionId() {
        return this.copyHttpSessionId;
    }

    public void setCreateSession(boolean createSession) {
        this.createSession = createSession;
    }

    public boolean isCreateSession() {
        return this.createSession;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpSession session = this.getSession(request);
        if (session != null) {
            if (this.isCopyHttpSessionId()) {
                attributes.put(HTTP_SESSION_ID_ATTR_NAME, session.getId());
            }
            Enumeration names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                if (!this.isCopyAllAttributes() && !this.getAttributeNames().contains(name)) continue;
                attributes.put(name, session.getAttribute(name));
            }
        }
        return true;
    }

    @Nullable
    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest)request;
            return serverRequest.getServletRequest().getSession(this.isCreateSession());
        }
        return null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception ex) {
    }
}

