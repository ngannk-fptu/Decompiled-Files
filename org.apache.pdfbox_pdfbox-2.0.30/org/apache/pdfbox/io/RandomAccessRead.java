/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.Closeable;
import java.io.IOException;

public interface RandomAccessRead
extends Closeable {
    public int read() throws IOException;

    public int read(byte[] var1) throws IOException;

    public int read(byte[] var1, int var2, int var3) throws IOException;

    public long getPosition() throws IOException;

    public void seek(long var1) throws IOException;

    public long length() throws IOException;

    public boolean isClosed();

    public int peek() throws IOException;

    public void rewind(int var1) throws IOException;

    public byte[] readFully(int var1) throws IOException;

    public boolean isEOF() throws IOException;

    public int available() throws IOException;
}

