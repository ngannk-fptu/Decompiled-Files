/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.DataInput;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamDataInput
extends DataInput
implements Closeable {
    private final InputStream is;

    public InputStreamDataInput(InputStream is) {
        this.is = is;
    }

    public byte readByte() throws IOException {
        int v = this.is.read();
        if (v == -1) {
            throw new EOFException();
        }
        return (byte)v;
    }

    public void readBytes(byte[] b, int offset, int len) throws IOException {
        while (len > 0) {
            int cnt = this.is.read(b, offset, len);
            if (cnt < 0) {
                throw new EOFException();
            }
            len -= cnt;
            offset += cnt;
        }
    }

    public void close() throws IOException {
        this.is.close();
    }
}

