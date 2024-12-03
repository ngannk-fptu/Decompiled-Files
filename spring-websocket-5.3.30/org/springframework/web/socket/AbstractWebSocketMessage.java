/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.web.socket;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketMessage;

public abstract class AbstractWebSocketMessage<T>
implements WebSocketMessage<T> {
    private final T payload;
    private final boolean last;

    AbstractWebSocketMessage(T payload) {
        this(payload, true);
    }

    AbstractWebSocketMessage(T payload, boolean isLast) {
        Assert.notNull(payload, (String)"payload must not be null");
        this.payload = payload;
        this.last = isLast;
    }

    @Override
    public T getPayload() {
        return this.payload;
    }

    @Override
    public boolean isLast() {
        return this.last;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractWebSocketMessage)) {
            return false;
        }
        AbstractWebSocketMessage otherMessage = (AbstractWebSocketMessage)other;
        return ObjectUtils.nullSafeEquals(this.payload, otherMessage.payload);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.payload);
    }

    public String toString() {
        return this.getClass().getSimpleName() + " payload=[" + this.toStringPayload() + "], byteCount=" + this.getPayloadLength() + ", last=" + this.isLast() + "]";
    }

    protected abstract String toStringPayload();
}

