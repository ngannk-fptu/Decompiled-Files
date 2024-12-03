/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.messaging;

import java.security.Principal;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

public class SessionDisconnectEvent
extends AbstractSubProtocolEvent {
    private final String sessionId;
    private final CloseStatus status;

    public SessionDisconnectEvent(Object source, Message<byte[]> message, String sessionId, CloseStatus closeStatus) {
        this(source, message, sessionId, closeStatus, null);
    }

    public SessionDisconnectEvent(Object source, Message<byte[]> message, String sessionId, CloseStatus closeStatus, @Nullable Principal user) {
        super(source, message, user);
        Assert.notNull((Object)sessionId, (String)"Session id must not be null");
        this.sessionId = sessionId;
        this.status = closeStatus;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public CloseStatus getCloseStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "SessionDisconnectEvent[sessionId=" + this.sessionId + ", " + this.status.toString() + "]";
    }
}

