/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

public class SubImageOutputStream
extends ImageOutputStreamImpl {
    private final ImageOutputStream stream;
    private final long startPos;

    public SubImageOutputStream(ImageOutputStream imageOutputStream) throws IOException {
        this.stream = (ImageOutputStream)Validate.notNull((Object)imageOutputStream, (String)"stream");
        this.startPos = imageOutputStream.getStreamPosition();
    }

    @Override
    public void seek(long l) throws IOException {
        super.seek(l);
        this.stream.seek(this.startPos + l);
    }

    @Override
    public void write(int n) throws IOException {
        this.flushBits();
        this.stream.write(n);
        ++this.streamPos;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.flushBits();
        this.stream.write(byArray, n, n2);
        this.streamPos += (long)n2;
    }

    @Override
    public int read() throws IOException {
        this.bitOffset = 0;
        ++this.streamPos;
        return this.stream.read();
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        this.bitOffset = 0;
        int n3 = this.stream.read(byArray, n, n2);
        this.streamPos += (long)n3;
        return n3;
    }

    @Override
    public boolean isCached() {
        return this.stream.isCached();
    }

    @Override
    public boolean isCachedMemory() {
        return this.stream.isCachedMemory();
    }

    @Override
    public boolean isCachedFile() {
        return this.stream.isCachedFile();
    }
}

