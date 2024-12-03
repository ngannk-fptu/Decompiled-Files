/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.lucene.store.DataOutput;

public class OutputStreamDataOutput
extends DataOutput
implements Closeable {
    private final OutputStream os;

    public OutputStreamDataOutput(OutputStream os) {
        this.os = os;
    }

    @Override
    public void writeByte(byte b) throws IOException {
        this.os.write(b);
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        this.os.write(b, offset, length);
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }
}

