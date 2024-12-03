/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.methods.multipart.PartSource;

public class ByteArrayPartSource
implements PartSource {
    private String fileName;
    private byte[] bytes;

    public ByteArrayPartSource(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    @Override
    public long getLength() {
        return this.bytes.length;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public InputStream createInputStream() throws IOException {
        return new ByteArrayInputStream(this.bytes);
    }
}

