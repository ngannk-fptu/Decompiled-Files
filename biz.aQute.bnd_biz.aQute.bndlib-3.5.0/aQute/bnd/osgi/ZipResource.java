/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;
import aQute.lib.zip.ZipUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResource
implements Resource {
    private ByteBuffer buffer;
    private final ZipFile zip;
    private final ZipEntry entry;
    private long lastModified;
    private long size;
    private String extra;

    ZipResource(ZipFile zip, ZipEntry entry) {
        this.zip = zip;
        this.entry = entry;
        this.lastModified = -11L;
        this.size = entry.getSize();
        byte[] data = entry.getExtra();
        if (data != null) {
            this.extra = new String(data, StandardCharsets.UTF_8);
        }
    }

    @Override
    public ByteBuffer buffer() throws Exception {
        return this.getBuffer().duplicate();
    }

    private ByteBuffer getBuffer() throws Exception {
        if (this.buffer != null) {
            return this.buffer;
        }
        if (this.size == -1L) {
            this.buffer = ByteBuffer.wrap(IO.read(this.zip.getInputStream(this.entry)));
            return this.buffer;
        }
        ByteBuffer bb = IO.copy(this.zip.getInputStream(this.entry), ByteBuffer.allocate((int)this.size));
        bb.flip();
        this.buffer = bb;
        return this.buffer;
    }

    @Override
    public InputStream openInputStream() throws Exception {
        return IO.stream(this.buffer());
    }

    public String toString() {
        return ":" + this.zip.getName() + "(" + this.entry.getName() + "):";
    }

    @Override
    public void write(OutputStream out) throws Exception {
        if (this.buffer != null) {
            IO.copy(this.buffer(), out);
        } else {
            IO.copy(this.zip.getInputStream(this.entry), out);
        }
    }

    @Override
    public long lastModified() {
        if (this.lastModified != -11L) {
            return this.lastModified;
        }
        this.lastModified = ZipUtil.getModifiedTime(this.entry);
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
    public long size() throws Exception {
        if (this.size >= 0L) {
            return this.size;
        }
        this.size = this.getBuffer().limit();
        return this.size;
    }

    @Override
    public void close() throws IOException {
    }
}

