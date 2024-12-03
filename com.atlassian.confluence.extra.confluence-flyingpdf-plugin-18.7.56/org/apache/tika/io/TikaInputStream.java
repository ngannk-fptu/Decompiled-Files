/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.LookaheadInputStream;
import org.apache.tika.io.TaggedInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.Metadata;

public class TikaInputStream
extends TaggedInputStream {
    private static final int MAX_CONSECUTIVE_EOFS = 1000;
    private static final int BLOB_SIZE_THRESHOLD = 0x100000;
    private Path path;
    private final TemporaryResources tmp;
    private long length;
    private long position = 0L;
    private long mark = -1L;
    private Object openContainer;
    private int consecutiveEOFs = 0;
    private byte[] skipBuffer;

    public static boolean isTikaInputStream(InputStream stream) {
        return stream instanceof TikaInputStream;
    }

    public static TikaInputStream get(InputStream stream, TemporaryResources tmp) {
        if (stream == null) {
            throw new NullPointerException("The Stream must not be null");
        }
        if (stream instanceof TikaInputStream) {
            return (TikaInputStream)stream;
        }
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        return new TikaInputStream(stream, tmp, -1L);
    }

    public static TikaInputStream get(InputStream stream) {
        return TikaInputStream.get(stream, new TemporaryResources());
    }

    public static TikaInputStream cast(InputStream stream) {
        if (stream instanceof TikaInputStream) {
            return (TikaInputStream)stream;
        }
        return null;
    }

    public static TikaInputStream get(byte[] data) {
        return TikaInputStream.get(data, new Metadata());
    }

    public static TikaInputStream get(byte[] data, Metadata metadata) {
        metadata.set("Content-Length", Integer.toString(data.length));
        return new TikaInputStream(new ByteArrayInputStream(data), new TemporaryResources(), data.length);
    }

    public static TikaInputStream get(Path path) throws IOException {
        return TikaInputStream.get(path, new Metadata());
    }

    public static TikaInputStream get(Path path, Metadata metadata) throws IOException {
        metadata.set("resourceName", path.getFileName().toString());
        metadata.set("Content-Length", Long.toString(Files.size(path)));
        return new TikaInputStream(path);
    }

    @Deprecated
    public static TikaInputStream get(File file) throws FileNotFoundException {
        return TikaInputStream.get(file, new Metadata());
    }

    @Deprecated
    public static TikaInputStream get(File file, Metadata metadata) throws FileNotFoundException {
        metadata.set("resourceName", file.getName());
        metadata.set("Content-Length", Long.toString(file.length()));
        return new TikaInputStream(file);
    }

    public static TikaInputStream get(Blob blob) throws SQLException {
        return TikaInputStream.get(blob, new Metadata());
    }

    public static TikaInputStream get(Blob blob, Metadata metadata) throws SQLException {
        long length = -1L;
        try {
            length = blob.length();
            metadata.set("Content-Length", Long.toString(length));
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        if (0L <= length && length <= 0x100000L) {
            return TikaInputStream.get(blob.getBytes(1L, (int)length), metadata);
        }
        return new TikaInputStream(new BufferedInputStream(blob.getBinaryStream()), new TemporaryResources(), length);
    }

    public static TikaInputStream get(URI uri) throws IOException {
        return TikaInputStream.get(uri, new Metadata());
    }

    public static TikaInputStream get(URI uri, Metadata metadata) throws IOException {
        Path path;
        if ("file".equalsIgnoreCase(uri.getScheme()) && Files.isRegularFile(path = Paths.get(uri), new LinkOption[0])) {
            return TikaInputStream.get(path, metadata);
        }
        return TikaInputStream.get(uri.toURL(), metadata);
    }

    public static TikaInputStream get(URL url) throws IOException {
        return TikaInputStream.get(url, new Metadata());
    }

    public static TikaInputStream get(URL url, Metadata metadata) throws IOException {
        int length;
        String encoding;
        String type;
        if ("file".equalsIgnoreCase(url.getProtocol())) {
            try {
                Path path = Paths.get(url.toURI());
                if (Files.isRegularFile(path, new LinkOption[0])) {
                    return TikaInputStream.get(path, metadata);
                }
            }
            catch (URISyntaxException path) {
                // empty catch block
            }
        }
        URLConnection connection = url.openConnection();
        String path = url.getPath();
        int slash = path.lastIndexOf(47);
        if (slash + 1 < path.length()) {
            metadata.set("resourceName", path.substring(slash + 1));
        }
        if ((type = connection.getContentType()) != null) {
            metadata.set("Content-Type", type);
        }
        if ((encoding = connection.getContentEncoding()) != null) {
            metadata.set("Content-Encoding", encoding);
        }
        if ((length = connection.getContentLength()) >= 0) {
            metadata.set("Content-Length", Integer.toString(length));
        }
        return new TikaInputStream(new BufferedInputStream(connection.getInputStream()), new TemporaryResources(), length);
    }

    private TikaInputStream(Path path) throws IOException {
        super(new BufferedInputStream(Files.newInputStream(path, new OpenOption[0])));
        this.path = path;
        this.tmp = new TemporaryResources();
        this.length = Files.size(path);
    }

    @Deprecated
    private TikaInputStream(File file) throws FileNotFoundException {
        super(new BufferedInputStream(new FileInputStream(file)));
        this.path = file.toPath();
        this.tmp = new TemporaryResources();
        this.length = file.length();
    }

    private TikaInputStream(InputStream stream, TemporaryResources tmp, long length) {
        super(stream);
        this.path = null;
        this.tmp = tmp;
        this.length = length;
    }

    public int peek(byte[] buffer) throws IOException {
        int n = 0;
        this.mark(buffer.length);
        int m = this.read(buffer);
        while (m != -1) {
            if ((n += m) < buffer.length) {
                m = this.read(buffer, n, buffer.length - n);
                continue;
            }
            m = -1;
        }
        this.reset();
        return n;
    }

    public Object getOpenContainer() {
        return this.openContainer;
    }

    public void setOpenContainer(Object container) {
        this.openContainer = container;
        if (container instanceof Closeable) {
            this.tmp.addResource((Closeable)container);
        }
    }

    public boolean hasFile() {
        return this.path != null;
    }

    public Path getPath() throws IOException {
        return this.getPath(-1);
    }

    public Path getPath(int maxBytes) throws IOException {
        if (this.path == null) {
            Path tmpFile;
            block18: {
                if (this.position > 0L) {
                    throw new IOException("Stream is already being read");
                }
                tmpFile = this.tmp.createTempFile();
                if (maxBytes > -1) {
                    try (LookaheadInputStream lookAhead = new LookaheadInputStream(this.in, maxBytes);){
                        Files.copy(lookAhead, tmpFile, StandardCopyOption.REPLACE_EXISTING);
                        if (Files.size(tmpFile) >= (long)maxBytes) {
                            Path path = null;
                            return path;
                        }
                        break block18;
                    }
                }
                Files.copy(this.in, tmpFile, StandardCopyOption.REPLACE_EXISTING);
            }
            this.path = tmpFile;
            InputStream newStream = Files.newInputStream(this.path, new OpenOption[0]);
            this.tmp.addResource(newStream);
            final InputStream oldStream = this.in;
            this.in = new BufferedInputStream(newStream){

                @Override
                public void close() throws IOException {
                    oldStream.close();
                }
            };
            this.length = Files.size(this.path);
        }
        return this.path;
    }

    public File getFile() throws IOException {
        return this.getPath().toFile();
    }

    public FileChannel getFileChannel() throws IOException {
        FileChannel channel = FileChannel.open(this.getPath(), new OpenOption[0]);
        this.tmp.addResource(channel);
        return channel;
    }

    public boolean hasLength() {
        return this.length != -1L;
    }

    public long getLength() throws IOException {
        if (this.length == -1L) {
            this.getPath();
        }
        return this.length;
    }

    public long getPosition() {
        return this.position;
    }

    @Override
    public long skip(long ln) throws IOException {
        if (this.skipBuffer == null) {
            this.skipBuffer = new byte[4096];
        }
        long n = IOUtils.skip(this.in, ln, this.skipBuffer);
        this.position += n;
        return n;
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
        this.mark = this.position;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.position = this.mark;
        this.mark = -1L;
        this.consecutiveEOFs = 0;
    }

    @Override
    public void close() throws IOException {
        this.path = null;
        this.mark = -1L;
        this.tmp.addResource(this.in);
        this.tmp.close();
    }

    @Override
    protected void afterRead(int n) throws IOException {
        if (n != -1) {
            this.position += (long)n;
        } else {
            ++this.consecutiveEOFs;
            if (this.consecutiveEOFs > 1000) {
                throw new IOException("Read too many -1 (EOFs); there could be an infinite loop.If you think your file is not corrupt, please open an issue on Tika's JIRA");
            }
        }
    }

    @Override
    public String toString() {
        String str = "TikaInputStream of ";
        str = this.hasFile() ? str + this.path.toString() : str + this.in.toString();
        if (this.openContainer != null) {
            str = str + " (in " + this.openContainer + ")";
        }
        return str;
    }
}

