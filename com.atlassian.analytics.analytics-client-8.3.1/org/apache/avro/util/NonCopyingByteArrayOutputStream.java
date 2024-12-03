/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class NonCopyingByteArrayOutputStream
extends ByteArrayOutputStream {
    public NonCopyingByteArrayOutputStream(int size) {
        super(size);
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.buf, 0, this.count);
    }
}

