/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.specific;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

class ExternalizableInput
extends InputStream {
    private final ObjectInput in;

    public ExternalizableInput(ObjectInput in) {
        this.in = in;
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.in.read(b);
    }

    @Override
    public int read(byte[] b, int offset, int len) throws IOException {
        return this.in.read(b, offset, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.in.skip(n);
    }
}

