/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.OutputStream;
import org.xerial.snappy.SnappyOutputStream;
import org.xerial.snappy.buffer.CachedBufferAllocator;

public class SnappyHadoopCompatibleOutputStream
extends SnappyOutputStream {
    public SnappyHadoopCompatibleOutputStream(OutputStream outputStream) {
        this(outputStream, 32768);
    }

    public SnappyHadoopCompatibleOutputStream(OutputStream outputStream, int n) {
        super(outputStream, n, CachedBufferAllocator.getBufferAllocatorFactory());
    }

    @Override
    protected int writeHeader() {
        return 0;
    }

    @Override
    protected void writeBlockPreemble() {
        this.writeCurrentDataSize();
    }
}

