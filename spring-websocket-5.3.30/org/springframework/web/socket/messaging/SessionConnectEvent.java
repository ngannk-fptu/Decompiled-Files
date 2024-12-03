/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 */
package org.springframework.web.socket.messaging;

import java.security.Principal;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

public class SessionConnectEvent
extends AbstractSubProtocolEvent {
    public SessionConnectEvent(Object source, Message<byte[]> message) {
        super(source, message);
    }

    public SessionConnectEvent(Object source, Message<byte[]> message, @Nullable Principal user) {
        super(source, message, user);
    }
}

