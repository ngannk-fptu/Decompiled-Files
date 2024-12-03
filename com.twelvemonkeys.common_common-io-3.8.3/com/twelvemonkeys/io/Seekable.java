/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import java.io.IOException;

public interface Seekable {
    public long getStreamPosition() throws IOException;

    public void seek(long var1) throws IOException;

    public void mark();

    public void reset() throws IOException;

    public void flushBefore(long var1) throws IOException;

    public void flush() throws IOException;

    public long getFlushedPosition() throws IOException;

    public boolean isCached();

    public boolean isCachedMemory();

    public boolean isCachedFile();

    public void close() throws IOException;
}

