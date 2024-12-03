/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.nio.ascii.TextDecoder;
import com.hazelcast.nio.ascii.TextEncoder;

public abstract class AbstractTextCommand
implements TextCommand {
    protected final TextCommandConstants.TextCommandType type;
    private TextDecoder decoder;
    private TextEncoder encoder;
    private long requestId = -1L;

    protected AbstractTextCommand(TextCommandConstants.TextCommandType type) {
        this.type = type;
    }

    @Override
    public int getFrameLength() {
        return 0;
    }

    @Override
    public TextCommandConstants.TextCommandType getType() {
        return this.type;
    }

    @Override
    public TextDecoder getDecoder() {
        return this.decoder;
    }

    @Override
    public TextEncoder getEncoder() {
        return this.encoder;
    }

    @Override
    public long getRequestId() {
        return this.requestId;
    }

    @Override
    public void init(TextDecoder decoder, long requestId) {
        this.decoder = decoder;
        this.requestId = requestId;
        this.encoder = decoder.getEncoder();
    }

    @Override
    public boolean isUrgent() {
        return false;
    }

    @Override
    public boolean shouldReply() {
        return true;
    }

    public String toString() {
        return "AbstractTextCommand[" + (Object)((Object)this.type) + "]{requestId=" + this.requestId + '}';
    }
}

