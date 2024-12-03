/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public abstract class WriteResource
implements Resource {
    private ByteBuffer buffer;
    private String extra;

    @Override
    public ByteBuffer buffer() throws Exception {
        return this.getBuffer().duplicate();
    }

    private ByteBuffer getBuffer() throws Exception {
        if (this.buffer != null) {
            return this.buffer;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        this.write(out);
        this.buffer = ByteBuffer.wrap(out.toByteArray());
        return this.buffer;
    }

    @Override
    public InputStream openInputStream() throws Exception {
        return IO.stream(this.buffer());
    }

    @Override
    public abstract void write(OutputStream var1) throws Exception;

    @Override
    public abstract long lastModified();

    @Override
    public String getExtra() {
        return this.extra;
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public long size() throws Exception {
        return this.getBuffer().limit();
    }

    @Override
    public void close() throws IOException {
    }
}

