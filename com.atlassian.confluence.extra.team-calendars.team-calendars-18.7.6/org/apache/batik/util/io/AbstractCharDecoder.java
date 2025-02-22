/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.util.io.CharDecoder;
import org.apache.batik.util.io.Messages;

public abstract class AbstractCharDecoder
implements CharDecoder {
    protected static final int BUFFER_SIZE = 8192;
    protected InputStream inputStream;
    protected byte[] buffer = new byte[8192];
    protected int position;
    protected int count;

    protected AbstractCharDecoder(InputStream is) {
        this.inputStream = is;
    }

    @Override
    public void dispose() throws IOException {
        this.inputStream.close();
        this.inputStream = null;
    }

    protected void fillBuffer() throws IOException {
        this.count = this.inputStream.read(this.buffer, 0, 8192);
        this.position = 0;
    }

    protected void charError(String encoding) throws IOException {
        throw new IOException(Messages.formatMessage("invalid.char", new Object[]{encoding}));
    }

    protected void endOfStreamError(String encoding) throws IOException {
        throw new IOException(Messages.formatMessage("end.of.stream", new Object[]{encoding}));
    }
}

