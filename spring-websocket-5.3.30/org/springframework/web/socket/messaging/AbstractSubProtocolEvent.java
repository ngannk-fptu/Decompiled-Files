/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.messaging;

import java.security.Principal;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

public abstract class AbstractSubProtocolEvent
extends ApplicationEvent {
    private final Message<byte[]> message;
    @Nullable
    private final Principal user;

    protected AbstractSubProtocolEvent(Object source, Message<byte[]> message) {
        this(source, message, null);
    }

    protected AbstractSubProtocolEvent(Object source, Message<byte[]> message, @Nullable Principal user) {
        super(source);
        Assert.notNull(message, (String)"Message must not be null");
        this.message = message;
        this.user = user;
    }

    public Message<byte[]> getMessage() {
        return this.message;
    }

    @Nullable
    public Principal getUser() {
        return this.user;
    }

    public String toString() {
        return ((Object)((Object)this)).getClass().getSimpleName() + "[" + this.message + "]";
    }
}

