/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

public class URLResource
implements Resource {
    private static final ByteBuffer CLOSED = ByteBuffer.allocate(0);
    private ByteBuffer buffer;
    private final URL url;
    private String extra;
    private long lastModified = -1L;
    private int size = -1;

    public URLResource(URL url) {
        this.url = url;
    }

    @Override
    public ByteBuffer buffer() throws Exception {
        return this.getBuffer().duplicate();
    }

    private ByteBuffer getBuffer() throws Exception {
        if (this.buffer != null) {
            return this.buffer;
        }
        if (this.url.getProtocol().equals("file")) {
            File file = new File(this.url.getPath());
            this.lastModified = file.lastModified();
            this.buffer = IO.read(file.toPath());
            return this.buffer;
        }
        URLConnection conn = this.openConnection();
        if (this.size == -1) {
            this.buffer = ByteBuffer.wrap(IO.read(conn.getInputStream()));
            return this.buffer;
        }
        ByteBuffer bb = IO.copy(conn.getInputStream(), ByteBuffer.allocate(this.size));
        bb.flip();
        this.buffer = bb;
        return this.buffer;
    }

    private URLConnection openConnection() throws Exception {
        URLConnection conn = this.url.openConnection();
        conn.connect();
        this.lastModified = conn.getLastModified();
        int length = conn.getContentLength();
        if (length != -1) {
            this.size = length;
        }
        return conn;
    }

    @Override
    public InputStream openInputStream() throws Exception {
        return IO.stream(this.buffer());
    }

    public String toString() {
        return ":" + this.url.toExternalForm() + ":";
    }

    @Override
    public void write(OutputStream out) throws Exception {
        if (this.buffer != null) {
            IO.copy(this.buffer(), out);
        } else {
            IO.copy(this.openConnection().getInputStream(), out);
        }
    }

    @Override
    public long lastModified() {
        if (this.lastModified >= 0L) {
            return this.lastModified;
        }
        try {
            this.getBuffer();
        }
        catch (Exception e) {
            this.lastModified = 0L;
        }
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
        if (this.size >= 0) {
            return this.size;
        }
        this.size = this.getBuffer().limit();
        return this.size;
    }

    @Override
    public void close() throws IOException {
        this.buffer = CLOSED;
    }
}

