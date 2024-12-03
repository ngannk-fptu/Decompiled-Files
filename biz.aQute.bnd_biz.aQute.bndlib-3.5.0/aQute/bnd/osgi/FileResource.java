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
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

public class FileResource
implements Resource {
    private static final int THRESHOLD = 65536;
    private static final ByteBuffer CLOSED = ByteBuffer.allocate(0);
    private ByteBuffer buffer;
    private final Path file;
    private String extra;
    private boolean deleteOnClose;
    private final long lastModified;
    private final long size;

    public FileResource(File file) throws IOException {
        this(file.toPath());
    }

    public FileResource(Path path) throws IOException {
        this(path, Files.readAttributes(path, BasicFileAttributes.class, new LinkOption[0]));
    }

    FileResource(Path path, BasicFileAttributes attrs) throws IOException {
        this.file = path.toAbsolutePath();
        this.lastModified = attrs.lastModifiedTime().toMillis();
        this.size = attrs.size();
    }

    public FileResource(Resource r) throws Exception {
        this.file = Files.createTempFile("fileresource", ".resource", new FileAttribute[0]);
        this.deleteOnClose(true);
        this.file.toFile().deleteOnExit();
        try (OutputStream out = IO.outputStream(this.file);){
            r.write(out);
        }
        this.lastModified = r.lastModified();
        this.size = Files.size(this.file);
    }

    @Override
    public ByteBuffer buffer() throws Exception {
        if (this.buffer != null) {
            return this.buffer.duplicate();
        }
        if (IO.isWindows() && this.size > 65536L) {
            return null;
        }
        this.buffer = IO.read(this.file);
        return this.buffer.duplicate();
    }

    @Override
    public InputStream openInputStream() throws Exception {
        if (this.buffer != null) {
            return IO.stream(this.buffer());
        }
        return IO.stream(this.file);
    }

    public String toString() {
        return this.file.toString();
    }

    @Override
    public void write(OutputStream out) throws Exception {
        if (this.buffer != null) {
            IO.copy(this.buffer(), out);
        } else {
            IO.copy(this.file, out);
        }
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
        return this.size;
    }

    @Override
    public void close() throws IOException {
        this.buffer = CLOSED;
        if (this.deleteOnClose) {
            IO.delete(this.file);
        }
    }

    public void deleteOnClose(boolean b) {
        this.deleteOnClose = b;
    }

    public File getFile() {
        return this.file.toFile();
    }
}

