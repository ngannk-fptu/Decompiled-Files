/*
 * Decompiled with CFR 0.152.
 */
package javax.websocket;

import javax.websocket.Session;

public class SessionException
extends Exception {
    private static final long serialVersionUID = 1L;
    private final Session session;

    public SessionException(String message, Throwable cause, Session session) {
        super(message, cause);
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }
}

