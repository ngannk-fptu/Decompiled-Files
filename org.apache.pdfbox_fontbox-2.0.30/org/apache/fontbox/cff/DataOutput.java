/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataOutput {
    private ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
    private String outputEncoding = null;

    public DataOutput() {
        this("ISO-8859-1");
    }

    public DataOutput(String encoding) {
        this.outputEncoding = encoding;
    }

    public byte[] getBytes() {
        return this.outputBuffer.toByteArray();
    }

    public void write(int value) {
        this.outputBuffer.write(value);
    }

    public void write(byte[] buffer) {
        this.outputBuffer.write(buffer, 0, buffer.length);
    }

    public void write(byte[] buffer, int offset, int length) {
        this.outputBuffer.write(buffer, offset, length);
    }

    public void print(String string) throws IOException {
        this.write(string.getBytes(this.outputEncoding));
    }

    public void println(String string) throws IOException {
        this.write(string.getBytes(this.outputEncoding));
        this.write(10);
    }

    public void println() {
        this.write(10);
    }
}

