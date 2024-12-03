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

public abstract class AbstractResource
implements Resource {
    private String extra;
    private ByteBuffer buffer;
    private final long lastModified;

    protected AbstractResource(long modified) {
        this.lastModified = modified;
    }

    @Override
    public String getExtra() {
        return this.extra;
    }

    @Override
    public long lastModified() {
        return this.lastModified;
    }

    @Override
    public InputStream openInputStream() throws Exception {
        return IO.stream(this.buffer());
    }

    private ByteBuffer getBuffer() throws Exception {
        if (this.buffer != null) {
            return this.buffer;
        }
        this.buffer = ByteBuffer.wrap(this.getBytes());
        return this.buffer;
    }

    @Override
    public ByteBuffer buffer() throws Exception {
        return this.getBuffer().duplicate();
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public void write(OutputStream out) throws Exception {
        IO.copy(this.buffer(), out);
    }

    protected abstract byte[] getBytes() throws Exception;

    @Override
    public long size() throws Exception {
        return this.getBuffer().limit();
    }

    @Override
    public void close() throws IOException {
    }
}

