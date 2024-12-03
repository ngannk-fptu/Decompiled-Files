/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.StreamType;

final class StreamSetterArgs {
    private long length;
    final StreamType streamType;

    final long getLength() {
        return this.length;
    }

    final void setLength(long newLength) {
        assert (-1L == this.length);
        assert (newLength >= 0L);
        this.length = newLength;
    }

    StreamSetterArgs(StreamType streamType, long length) {
        this.streamType = streamType;
        this.length = length;
    }
}

