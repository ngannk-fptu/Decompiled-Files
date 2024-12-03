/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.abdera.i18n.text.io.RewindableInputStream;

public class PeekAheadInputStream
extends RewindableInputStream {
    public PeekAheadInputStream(InputStream in) {
        super(in);
    }

    public PeekAheadInputStream(InputStream in, int initialSize) {
        super(in, initialSize);
    }

    public int peek() throws IOException {
        int m = this.read();
        this.unread(m);
        return m;
    }

    public int peek(byte[] buf) throws IOException {
        return this.peek(buf, 0, buf.length);
    }

    public int peek(byte[] buf, int off, int len) throws IOException {
        int r = this.read(buf, off, len);
        this.unread(buf, off, r);
        return r;
    }
}

