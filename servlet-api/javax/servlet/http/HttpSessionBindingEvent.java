/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

public class HttpSessionBindingEvent
extends HttpSessionEvent {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Object value;

    public HttpSessionBindingEvent(HttpSession session, String name) {
        super(session);
        this.name = name;
        this.value = null;
    }

    public HttpSessionBindingEvent(HttpSession session, String name, Object value) {
        super(session);
        this.name = name;
        this.value = value;
    }

    @Override
    public HttpSession getSession() {
        return super.getSession();
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}

