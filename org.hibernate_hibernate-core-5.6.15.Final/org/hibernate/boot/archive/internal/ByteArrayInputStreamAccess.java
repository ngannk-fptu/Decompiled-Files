/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.hibernate.boot.archive.spi.InputStreamAccess;

public class ByteArrayInputStreamAccess
implements InputStreamAccess {
    private final String name;
    private final byte[] bytes;

    public ByteArrayInputStreamAccess(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    @Override
    public String getStreamName() {
        return this.name;
    }

    @Override
    public InputStream accessInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }
}

