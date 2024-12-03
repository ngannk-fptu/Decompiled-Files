/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.Closeable;
import java.io.IOException;

public interface SequentialRead
extends Closeable {
    public int read() throws IOException;

    public int read(byte[] var1) throws IOException;

    public int read(byte[] var1, int var2, int var3) throws IOException;
}

