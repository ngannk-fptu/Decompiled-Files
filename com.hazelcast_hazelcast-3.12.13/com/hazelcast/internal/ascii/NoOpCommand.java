/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.util.StringUtil;
import java.nio.ByteBuffer;

public class NoOpCommand
extends AbstractTextCommand {
    private final ByteBuffer response;

    public NoOpCommand(byte[] response) {
        super(TextCommandConstants.TextCommandType.NO_OP);
        this.response = ByteBuffer.wrap(response);
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        while (dst.hasRemaining() && this.response.hasRemaining()) {
            dst.put(this.response.get());
        }
        return !this.response.hasRemaining();
    }

    @Override
    public String toString() {
        return "NoOpCommand {" + StringUtil.bytesToString(this.response.array()) + "}";
    }
}

