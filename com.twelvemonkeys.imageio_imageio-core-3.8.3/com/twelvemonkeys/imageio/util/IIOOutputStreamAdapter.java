/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.stream.ImageOutputStream;

class IIOOutputStreamAdapter
extends OutputStream {
    private ImageOutputStream output;

    public IIOOutputStreamAdapter(ImageOutputStream imageOutputStream) {
        Validate.notNull((Object)imageOutputStream, (String)"stream == null");
        this.output = imageOutputStream;
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        this.assertOpen();
        this.output.write(byArray);
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.assertOpen();
        this.output.write(byArray, n, n2);
    }

    @Override
    public void write(int n) throws IOException {
        this.assertOpen();
        this.output.write(n);
    }

    @Override
    public void flush() throws IOException {
        this.assertOpen();
    }

    private void assertOpen() throws IOException {
        if (this.output == null) {
            throw new IOException("stream already closed");
        }
    }

    @Override
    public void close() throws IOException {
        this.output = null;
    }
}

