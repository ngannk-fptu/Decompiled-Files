/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class EmbeddedResource
implements Resource {
    private final ByteBuffer buffer;
    private final long lastModified;
    private String extra;

    public EmbeddedResource(byte[] data, long lastModified) {
        this.buffer = ByteBuffer.wrap(data);
        this.lastModified = lastModified;
    }

    public EmbeddedResource(String pc, int lastModified) {
        this(pc.getBytes(StandardCharsets.UTF_8), (long)lastModified);
    }

    @Override
    public ByteBuffer buffer() {
        return this.buffer.duplicate();
    }

    @Override
    public InputStream openInputStream() {
        return IO.stream(this.buffer());
    }

    @Override
    public void write(OutputStream out) throws Exception {
        IO.copy(this.buffer(), out);
    }

    public String toString() {
        return ":" + this.size() + ":";
    }

    @Override
    public long lastModified() {
        return this.lastModified;
    }

    @Override
    public String getExtra() {
        return this.extra;
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public long size() {
        return this.buffer.limit();
    }

    @Override
    public void close() throws IOException {
    }
}

