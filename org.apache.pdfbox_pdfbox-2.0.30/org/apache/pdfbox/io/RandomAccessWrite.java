/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.Closeable;
import java.io.IOException;

public interface RandomAccessWrite
extends Closeable {
    public void write(int var1) throws IOException;

    public void write(byte[] var1) throws IOException;

    public void write(byte[] var1, int var2, int var3) throws IOException;

    public void clear() throws IOException;
}

