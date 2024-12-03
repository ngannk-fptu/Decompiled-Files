/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.MessageHeaders
 *  org.springframework.messaging.simp.stomp.StompCommand
 *  org.springframework.messaging.simp.stomp.StompHeaderAccessor
 *  org.springframework.messaging.support.MessageBuilder
 *  org.springframework.messaging.support.MessageHeaderAccessor
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.messaging;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;
import org.springframework.web.socket.messaging.SubProtocolErrorHandler;

public class StompSubProtocolErrorHandler
implements SubProtocolErrorHandler<byte[]> {
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    @Override
    @Nullable
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
        String receiptId;
        StompHeaderAccessor accessor = StompHeaderAccessor.create((StompCommand)StompCommand.ERROR);
        accessor.setMessage(ex.getMessage());
        accessor.setLeaveMutable(true);
        StompHeaderAccessor clientHeaderAccessor = null;
        if (clientMessage != null && (clientHeaderAccessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class)) != null && (receiptId = clientHeaderAccessor.getReceipt()) != null) {
            accessor.setReceiptId(receiptId);
        }
        return this.handleInternal(accessor, EMPTY_PAYLOAD, ex, clientHeaderAccessor);
    }

    @Override
    @Nullable
    public Message<byte[]> handleErrorMessageToClient(Message<byte[]> errorMessage) {
        StompHeaderAccessor accessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor(errorMessage, StompHeaderAccessor.class);
        Assert.notNull((Object)accessor, (String)"No StompHeaderAccessor");
        if (!accessor.isMutable()) {
            accessor = StompHeaderAccessor.wrap(errorMessage);
        }
        return this.handleInternal(accessor, (byte[])errorMessage.getPayload(), null, null);
    }

    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload, @Nullable Throwable cause, @Nullable StompHeaderAccessor clientHeaderAccessor) {
        return MessageBuilder.createMessage((Object)errorPayload, (MessageHeaders)errorHeaderAccessor.getMessageHeaders());
    }
}

