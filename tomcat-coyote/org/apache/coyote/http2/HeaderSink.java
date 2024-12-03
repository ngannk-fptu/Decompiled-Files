/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.StreamException;

class HeaderSink
implements HpackDecoder.HeaderEmitter {
    HeaderSink() {
    }

    @Override
    public void emitHeader(String name, String value) {
    }

    @Override
    public void validateHeaders() throws StreamException {
    }

    @Override
    public void setHeaderException(StreamException streamException) {
    }
}

