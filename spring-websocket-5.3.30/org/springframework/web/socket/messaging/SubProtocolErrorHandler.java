/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 */
package org.springframework.web.socket.messaging;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

public interface SubProtocolErrorHandler<P> {
    @Nullable
    public Message<P> handleClientMessageProcessingError(@Nullable Message<P> var1, Throwable var2);

    @Nullable
    public Message<P> handleErrorMessageToClient(Message<P> var1);
}

