/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.hibernate.engine.jdbc.BinaryStream;

public final class BinaryStreamImpl
extends ByteArrayInputStream
implements BinaryStream {
    private final int length;

    public BinaryStreamImpl(byte[] bytes) {
        super(bytes);
        this.length = bytes.length;
    }

    @Override
    public InputStream getInputStream() {
        return this;
    }

    @Override
    public byte[] getBytes() {
        return this.buf;
    }

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    public void release() {
        try {
            super.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

