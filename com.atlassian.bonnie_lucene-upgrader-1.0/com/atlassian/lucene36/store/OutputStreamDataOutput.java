/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.DataOutput;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamDataOutput
extends DataOutput
implements Closeable {
    private final OutputStream os;

    public OutputStreamDataOutput(OutputStream os) {
        this.os = os;
    }

    public void writeByte(byte b) throws IOException {
        this.os.write(b);
    }

    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        this.os.write(b, offset, length);
    }

    public void close() throws IOException {
        this.os.close();
    }
}

