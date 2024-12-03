/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.avro.file.SeekableInput;

public class SeekableByteArrayInput
extends ByteArrayInputStream
implements SeekableInput {
    public SeekableByteArrayInput(byte[] data) {
        super(data);
    }

    @Override
    public long length() throws IOException {
        return this.count;
    }

    @Override
    public void seek(long p) throws IOException {
        this.reset();
        this.skip(p);
    }

    @Override
    public long tell() throws IOException {
        return this.pos;
    }
}

