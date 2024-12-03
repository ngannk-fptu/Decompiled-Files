/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import org.apache.http.nio.reactor.IOSession;
import org.apache.http.protocol.HttpContext;

class SessionHttpContext
implements HttpContext {
    private final IOSession ioSession;

    public SessionHttpContext(IOSession ioSession) {
        this.ioSession = ioSession;
    }

    @Override
    public Object getAttribute(String id) {
        return this.ioSession.getAttribute(id);
    }

    @Override
    public Object removeAttribute(String id) {
        return this.ioSession.removeAttribute(id);
    }

    @Override
    public void setAttribute(String id, Object obj) {
        this.ioSession.setAttribute(id, obj);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ioSession=");
        sb.append(this.ioSession);
        sb.append("]");
        return sb.toString();
    }
}

