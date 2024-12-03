/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.bytesource;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.common.BinaryFunctions;

public abstract class ByteSource {
    private final String fileName;

    public ByteSource(String fileName) {
        this.fileName = fileName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final InputStream getInputStream(long start) throws IOException {
        InputStream is = null;
        boolean succeeded = false;
        try {
            is = this.getInputStream();
            BinaryFunctions.skipBytes(is, start);
            succeeded = true;
        }
        finally {
            if (!succeeded && is != null) {
                is.close();
            }
        }
        return is;
    }

    public abstract InputStream getInputStream() throws IOException;

    public byte[] getBlock(int start, int length) throws IOException {
        return this.getBlock(0xFFFFFFFFL & (long)start, length);
    }

    public abstract byte[] getBlock(long var1, int var3) throws IOException;

    public abstract byte[] getAll() throws IOException;

    public abstract long getLength() throws IOException;

    public abstract String getDescription();

    public final String getFileName() {
        return this.fileName;
    }
}

