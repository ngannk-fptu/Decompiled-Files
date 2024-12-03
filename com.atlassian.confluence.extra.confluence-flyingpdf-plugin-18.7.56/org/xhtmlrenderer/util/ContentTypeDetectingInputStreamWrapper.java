/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ContentTypeDetectingInputStreamWrapper
extends BufferedInputStream {
    protected static final int MAX_MAGIC_BYTES = 4;
    protected final byte[] MAGIC_BYTES;
    protected static final byte[] MAGIC_BYTES_PDF = "%PDF".getBytes();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContentTypeDetectingInputStreamWrapper(InputStream source) throws IOException {
        super(source);
        byte[] MAGIC_BYTES = new byte[4];
        this.mark(4);
        try {
            int bytesRead = this.read(MAGIC_BYTES);
            if (bytesRead < 4) {
                MAGIC_BYTES = bytesRead <= 0 ? new byte[]{} : Arrays.copyOf(MAGIC_BYTES, bytesRead);
            }
            this.MAGIC_BYTES = MAGIC_BYTES;
        }
        finally {
            this.reset();
        }
    }

    protected boolean streamStartsWithMagicBytes(byte[] bytes) {
        if (this.MAGIC_BYTES.length < bytes.length) {
            return false;
        }
        for (int i = 0; i < bytes.length; ++i) {
            if (this.MAGIC_BYTES[i] == bytes[i]) continue;
            return false;
        }
        return true;
    }

    public boolean isPdf() {
        return this.streamStartsWithMagicBytes(MAGIC_BYTES_PDF);
    }
}

