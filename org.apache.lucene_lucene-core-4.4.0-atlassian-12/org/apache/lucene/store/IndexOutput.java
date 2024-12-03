/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public abstract class IndexOutput
extends DataOutput
implements Closeable {
    public abstract void flush() throws IOException;

    @Override
    public abstract void close() throws IOException;

    public abstract long getFilePointer();

    @Deprecated
    public abstract void seek(long var1) throws IOException;

    public abstract long length() throws IOException;

    public void setLength(long length) throws IOException {
    }
}

