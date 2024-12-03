/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.frame;

import org.apache.hc.core5.http2.frame.FrameFlag;
import org.apache.hc.core5.http2.frame.FrameType;

public abstract class Frame<T> {
    private final int type;
    private final int flags;
    private final int streamId;

    public Frame(int type, int flags, int streamId) {
        this.type = type;
        this.flags = flags;
        this.streamId = streamId;
    }

    public boolean isType(FrameType type) {
        return this.getType() == type.value;
    }

    public boolean isFlagSet(FrameFlag flag) {
        return (this.getFlags() & flag.value) != 0;
    }

    public int getType() {
        return this.type;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getStreamId() {
        return this.streamId;
    }

    public abstract T getPayload();

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("type=").append(this.type);
        sb.append(", flags=").append(this.flags);
        sb.append(", streamId=").append(this.streamId);
        sb.append(", payoad=").append(this.getPayload());
        sb.append(']');
        return sb.toString();
    }
}

