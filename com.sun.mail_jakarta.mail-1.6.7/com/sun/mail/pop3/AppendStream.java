/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.pop3;

import com.sun.mail.pop3.WritableSharedFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

class AppendStream
extends OutputStream {
    private final WritableSharedFile tf;
    private RandomAccessFile raf;
    private final long start;
    private long end;

    public AppendStream(WritableSharedFile tf) throws IOException {
        this.tf = tf;
        this.raf = tf.getWritableFile();
        this.start = this.raf.length();
        this.raf.seek(this.start);
    }

    @Override
    public void write(int b) throws IOException {
        this.raf.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.raf.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.raf.write(b, off, len);
    }

    @Override
    public synchronized void close() throws IOException {
        this.end = this.tf.updateLength();
        this.raf = null;
    }

    public synchronized InputStream getInputStream() throws IOException {
        return this.tf.newStream(this.start, this.end);
    }
}

