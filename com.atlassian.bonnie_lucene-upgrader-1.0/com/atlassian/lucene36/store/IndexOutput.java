/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.DataOutput;
import java.io.Closeable;
import java.io.IOException;

public abstract class IndexOutput
extends DataOutput
implements Closeable {
    public abstract void flush() throws IOException;

    public abstract void close() throws IOException;

    public abstract long getFilePointer();

    public abstract void seek(long var1) throws IOException;

    public abstract long length() throws IOException;

    public void setLength(long length) throws IOException {
    }
}

