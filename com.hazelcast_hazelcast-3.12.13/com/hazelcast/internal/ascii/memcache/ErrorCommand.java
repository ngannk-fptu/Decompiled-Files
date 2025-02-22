/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.StringUtil;
import java.nio.ByteBuffer;

public class ErrorCommand
extends AbstractTextCommand {
    private ByteBuffer response;
    private final String message;

    public ErrorCommand(TextCommandConstants.TextCommandType type) {
        this(type, null);
    }

    public ErrorCommand(TextCommandConstants.TextCommandType type, String message) {
        super(type);
        byte[] error = TextCommandConstants.ERROR;
        if (type == TextCommandConstants.TextCommandType.ERROR_CLIENT) {
            error = TextCommandConstants.CLIENT_ERROR;
        } else if (type == TextCommandConstants.TextCommandType.ERROR_SERVER) {
            error = TextCommandConstants.SERVER_ERROR;
        }
        this.message = message;
        byte[] msg = message == null ? null : StringUtil.stringToBytes(message);
        int total = error.length;
        if (msg != null) {
            total += msg.length;
        }
        this.response = ByteBuffer.allocate(total += 2);
        this.response.put(error);
        if (msg != null) {
            this.response.put(msg);
        }
        this.response.put(TextCommandConstants.RETURN);
        this.response.flip();
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        IOUtil.copyToHeapBuffer(this.response, dst);
        return !this.response.hasRemaining();
    }

    @Override
    public String toString() {
        return "ErrorCommand{type=" + (Object)((Object)this.type) + ", msg=" + this.message + '}' + super.toString();
    }
}

