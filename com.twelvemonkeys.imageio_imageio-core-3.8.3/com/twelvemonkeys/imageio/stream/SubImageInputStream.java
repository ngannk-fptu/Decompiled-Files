/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

public final class SubImageInputStream
extends ImageInputStreamImpl {
    private final ImageInputStream stream;
    private final long startPos;
    private final long length;

    public SubImageInputStream(ImageInputStream imageInputStream, long l) throws IOException {
        Validate.notNull((Object)imageInputStream, (String)"stream");
        Validate.isTrue((l >= 0L ? 1 : 0) != 0, (Object)l, (String)"length < 0: %d");
        this.stream = imageInputStream;
        this.startPos = imageInputStream.getStreamPosition();
        this.length = l;
    }

    @Override
    public int read() throws IOException {
        if (this.streamPos >= this.length) {
            return -1;
        }
        int n = this.stream.read();
        if (n >= 0) {
            ++this.streamPos;
        }
        return n;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this.streamPos >= this.length) {
            return -1;
        }
        int n3 = (int)Math.min((long)n2, this.length - this.streamPos);
        int n4 = this.stream.read(byArray, n, n3);
        if (n4 >= 0) {
            this.streamPos += (long)n4;
        }
        return n4;
    }

    @Override
    public long length() {
        try {
            long l = this.stream.length();
            return l < 0L ? -1L : Math.min(l - this.startPos, this.length);
        }
        catch (IOException iOException) {
            return -1L;
        }
    }

    @Override
    public void seek(long l) throws IOException {
        if (l < this.getFlushedPosition()) {
            throw new IndexOutOfBoundsException("pos < flushedPosition");
        }
        this.stream.seek(this.startPos + l);
        this.streamPos = l;
    }

    @Override
    protected void finalize() throws Throwable {
    }
}

