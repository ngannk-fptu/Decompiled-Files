/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.eventstreaming;

import com.amazonaws.services.s3.internal.eventstreaming.Message;
import com.amazonaws.services.s3.internal.eventstreaming.Prelude;
import com.amazonaws.services.s3.internal.eventstreaming.Utils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class MessageDecoder {
    private ByteBuffer buf = ByteBuffer.allocate(131072);

    public boolean hasPendingContent() {
        return this.buf.position() != 0;
    }

    public List<Message> feed(byte[] bytes) {
        return this.feed(bytes, 0, bytes.length);
    }

    public List<Message> feed(byte[] bytes, int offset, int length) {
        this.buf.put(bytes, offset, length);
        ByteBuffer readView = (ByteBuffer)this.buf.duplicate().flip();
        int bytesConsumed = 0;
        ArrayList<Message> result = new ArrayList<Message>();
        while (readView.remaining() >= 15) {
            int totalMessageLength = Utils.toIntExact(Prelude.decode(readView.duplicate()).getTotalLength());
            if (readView.remaining() < totalMessageLength) break;
            Message decoded = Message.decode(readView);
            result.add(decoded);
            bytesConsumed += totalMessageLength;
        }
        if (bytesConsumed > 0) {
            this.buf.flip();
            this.buf.position(this.buf.position() + bytesConsumed);
            this.buf.compact();
        }
        return result;
    }
}

