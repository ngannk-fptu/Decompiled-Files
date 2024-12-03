/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract class SimpleOutputStream
extends OutputStream {
    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void write(int b) throws IOException {
        byte[] buf = new byte[]{(byte)b};
        this.write(buf, 0, 1);
    }
}

