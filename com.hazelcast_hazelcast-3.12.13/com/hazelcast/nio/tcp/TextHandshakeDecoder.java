/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.nio.ascii.MemcacheTextDecoder;
import com.hazelcast.nio.ascii.RestApiTextDecoder;
import com.hazelcast.nio.tcp.SingleProtocolDecoder;
import java.nio.ByteBuffer;

public class TextHandshakeDecoder
extends SingleProtocolDecoder {
    public TextHandshakeDecoder(ProtocolType supportedProtocol, InboundHandler next) {
        super(supportedProtocol, new InboundHandler[]{next}, null);
    }

    @Override
    protected void verifyProtocol(String incomingProtocol) {
        if (ProtocolType.REST.equals((Object)this.supportedProtocol)) {
            if (!RestApiTextDecoder.TEXT_PARSERS.isCommandPrefix(incomingProtocol)) {
                throw new IllegalStateException("Unsupported protocol exchange detected, expected protocol: REST");
            }
        } else if (!MemcacheTextDecoder.TEXT_PARSERS.isCommandPrefix(incomingProtocol)) {
            throw new IllegalStateException("Unsupported protocol exchange detected, expected protocol: MEMCACHED");
        }
    }

    @Override
    protected void setupNextDecoder() {
        super.setupNextDecoder();
        ByteBuffer src = (ByteBuffer)this.src;
        ByteBuffer dst = (ByteBuffer)this.inboundHandlers[0].src();
        src.flip();
        dst.put(src);
    }
}

