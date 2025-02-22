/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.nio.IOUtil;
import java.nio.ByteBuffer;

public class SetCommand
extends AbstractTextCommand {
    private ByteBuffer response;
    private final String key;
    private final int flag;
    private final int expiration;
    private final int valueLen;
    private final boolean noreply;
    private final ByteBuffer bbValue;

    public SetCommand(TextCommandConstants.TextCommandType type, String key, int flag, int expiration, int valueLen, boolean noreply) {
        super(type);
        this.key = key;
        this.flag = flag;
        this.expiration = expiration;
        this.valueLen = valueLen;
        this.noreply = noreply;
        this.bbValue = ByteBuffer.allocate(valueLen);
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        this.copy(src);
        if (!this.bbValue.hasRemaining()) {
            while (src.hasRemaining()) {
                char c = (char)src.get();
                if (c != '\n') continue;
                this.bbValue.flip();
                return true;
            }
        }
        return false;
    }

    void copy(ByteBuffer cb) {
        if (cb.isDirect()) {
            int n = Math.min(cb.remaining(), this.bbValue.remaining());
            if (n > 0) {
                cb.get(this.bbValue.array(), this.bbValue.position(), n);
                this.bbValue.position(this.bbValue.position() + n);
            }
        } else {
            IOUtil.copyToHeapBuffer(cb, this.bbValue);
        }
    }

    public void setResponse(byte[] value) {
        this.response = ByteBuffer.wrap(value);
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        if (this.response == null) {
            this.response = ByteBuffer.wrap(TextCommandConstants.STORED);
        }
        while (dst.hasRemaining() && this.response.hasRemaining()) {
            dst.put(this.response.get());
        }
        return !this.response.hasRemaining();
    }

    @Override
    public boolean shouldReply() {
        return !this.noreply;
    }

    public int getExpiration() {
        return this.expiration;
    }

    public String getKey() {
        return this.key;
    }

    public byte[] getValue() {
        return this.bbValue.array();
    }

    public int getFlag() {
        return this.flag;
    }

    @Override
    public String toString() {
        return "SetCommand [" + (Object)((Object)this.type) + "]{key='" + this.key + '\'' + ", flag=" + this.flag + ", expiration=" + this.expiration + ", valueLen=" + this.valueLen + ", value=" + this.bbValue + '}' + super.toString();
    }
}

