/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.AsciiString
 */
package io.netty.handler.codec.spdy;

import io.netty.util.AsciiString;

public final class SpdyHttpHeaders {
    private SpdyHttpHeaders() {
    }

    public static final class Names {
        public static final AsciiString STREAM_ID = AsciiString.cached((String)"x-spdy-stream-id");
        public static final AsciiString ASSOCIATED_TO_STREAM_ID = AsciiString.cached((String)"x-spdy-associated-to-stream-id");
        public static final AsciiString PRIORITY = AsciiString.cached((String)"x-spdy-priority");
        public static final AsciiString SCHEME = AsciiString.cached((String)"x-spdy-scheme");

        private Names() {
        }
    }
}

