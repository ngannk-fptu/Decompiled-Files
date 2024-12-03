/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.file;

import java.io.Closeable;
import java.io.IOException;

public interface SeekableInput
extends Closeable {
    public void seek(long var1) throws IOException;

    public long tell() throws IOException;

    public long length() throws IOException;

    public int read(byte[] var1, int var2, int var3) throws IOException;
}

