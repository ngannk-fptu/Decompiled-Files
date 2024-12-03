/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfparser;

import java.io.Closeable;
import java.io.IOException;

interface SequentialSource
extends Closeable {
    public int read() throws IOException;

    public int read(byte[] var1) throws IOException;

    public int read(byte[] var1, int var2, int var3) throws IOException;

    public long getPosition() throws IOException;

    public int peek() throws IOException;

    public void unread(int var1) throws IOException;

    public void unread(byte[] var1) throws IOException;

    public void unread(byte[] var1, int var2, int var3) throws IOException;

    public byte[] readFully(int var1) throws IOException;

    public boolean isEOF() throws IOException;

    public boolean isClosed() throws IOException;
}

