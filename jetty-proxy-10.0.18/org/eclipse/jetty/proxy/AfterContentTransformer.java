/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.component.Destroyable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.proxy;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.proxy.AsyncMiddleManServlet;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.component.Destroyable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AfterContentTransformer
implements AsyncMiddleManServlet.ContentTransformer,
Destroyable {
    private static final Logger LOG = LoggerFactory.getLogger(AfterContentTransformer.class);
    private final List<ByteBuffer> sourceBuffers = new ArrayList<ByteBuffer>();
    private Path overflowDirectory = Paths.get(System.getProperty("java.io.tmpdir"), new String[0]);
    private String inputFilePrefix = "amms_adct_in_";
    private String outputFilePrefix = "amms_adct_out_";
    private long maxInputBufferSize;
    private long inputBufferSize;
    private FileChannel inputFile;
    private long maxOutputBufferSize = this.maxInputBufferSize = 0x100000L;
    private long outputBufferSize;
    private FileChannel outputFile;

    public Path getOverflowDirectory() {
        return this.overflowDirectory;
    }

    public void setOverflowDirectory(Path overflowDirectory) {
        this.overflowDirectory = overflowDirectory;
    }

    public String getInputFilePrefix() {
        return this.inputFilePrefix;
    }

    public void setInputFilePrefix(String inputFilePrefix) {
        this.inputFilePrefix = inputFilePrefix;
    }

    public long getMaxInputBufferSize() {
        return this.maxInputBufferSize;
    }

    public void setMaxInputBufferSize(long maxInputBufferSize) {
        this.maxInputBufferSize = maxInputBufferSize;
    }

    public String getOutputFilePrefix() {
        return this.outputFilePrefix;
    }

    public void setOutputFilePrefix(String outputFilePrefix) {
        this.outputFilePrefix = outputFilePrefix;
    }

    public long getMaxOutputBufferSize() {
        return this.maxOutputBufferSize;
    }

    public void setMaxOutputBufferSize(long maxOutputBufferSize) {
        this.maxOutputBufferSize = maxOutputBufferSize;
    }

    @Override
    public final void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) throws IOException {
        int remaining = input.remaining();
        if (remaining > 0) {
            this.inputBufferSize += (long)remaining;
            long max = this.getMaxInputBufferSize();
            if (max >= 0L && this.inputBufferSize > max) {
                this.overflow(input);
            } else {
                ByteBuffer copy = ByteBuffer.allocate(input.remaining());
                copy.put(input).flip();
                this.sourceBuffers.add(copy);
            }
        }
        if (finished) {
            Source source = new Source();
            Sink sink = new Sink();
            if (this.transform(source, sink)) {
                sink.drainTo(output);
            } else {
                source.drainTo(output);
            }
        }
    }

    public abstract boolean transform(Source var1, Sink var2) throws IOException;

    private void overflow(ByteBuffer input) throws IOException {
        if (this.inputFile == null) {
            Path path = Files.createTempFile(this.getOverflowDirectory(), this.getInputFilePrefix(), null, new FileAttribute[0]);
            this.inputFile = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE);
            int size = this.sourceBuffers.size();
            if (size > 0) {
                ByteBuffer[] buffers = this.sourceBuffers.toArray(new ByteBuffer[size]);
                this.sourceBuffers.clear();
                IO.write((GatheringByteChannel)this.inputFile, (ByteBuffer[])buffers, (int)0, (int)buffers.length);
            }
        }
        this.inputFile.write(input);
    }

    public void destroy() {
        this.close(this.inputFile);
        this.close(this.outputFile);
    }

    private void drain(FileChannel file, List<ByteBuffer> output) throws IOException {
        long size;
        long position = 0L;
        file.position(position);
        for (long length = file.size(); length > 0L; length -= size) {
            size = Math.min(0x40000000L, length);
            MappedByteBuffer buffer = file.map(FileChannel.MapMode.READ_ONLY, position, size);
            output.add(buffer);
            position += size;
        }
    }

    private void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException x) {
            LOG.trace("IGNORED", (Throwable)x);
        }
    }

    public class Source {
        private final InputStream stream;

        private Source() throws IOException {
            if (AfterContentTransformer.this.inputFile != null) {
                AfterContentTransformer.this.inputFile.force(true);
                this.stream = new ChannelInputStream();
            } else {
                this.stream = new MemoryInputStream();
            }
            this.stream.reset();
        }

        public InputStream getInputStream() {
            return this.stream;
        }

        private void drainTo(List<ByteBuffer> output) throws IOException {
            if (AfterContentTransformer.this.inputFile == null) {
                output.addAll(AfterContentTransformer.this.sourceBuffers);
                AfterContentTransformer.this.sourceBuffers.clear();
            } else {
                AfterContentTransformer.this.drain(AfterContentTransformer.this.inputFile, output);
            }
        }
    }

    public class Sink {
        private final List<ByteBuffer> sinkBuffers = new ArrayList<ByteBuffer>();
        private final OutputStream stream = new SinkOutputStream();

        public OutputStream getOutputStream() {
            return this.stream;
        }

        private void overflow(ByteBuffer output) throws IOException {
            if (AfterContentTransformer.this.outputFile == null) {
                Path path = Files.createTempFile(AfterContentTransformer.this.getOverflowDirectory(), AfterContentTransformer.this.getOutputFilePrefix(), null, new FileAttribute[0]);
                AfterContentTransformer.this.outputFile = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE);
                int size = this.sinkBuffers.size();
                if (size > 0) {
                    ByteBuffer[] buffers = this.sinkBuffers.toArray(new ByteBuffer[size]);
                    this.sinkBuffers.clear();
                    IO.write((GatheringByteChannel)AfterContentTransformer.this.outputFile, (ByteBuffer[])buffers, (int)0, (int)buffers.length);
                }
            }
            AfterContentTransformer.this.outputFile.write(output);
        }

        private void drainTo(List<ByteBuffer> output) throws IOException {
            if (AfterContentTransformer.this.outputFile == null) {
                output.addAll(this.sinkBuffers);
                this.sinkBuffers.clear();
            } else {
                AfterContentTransformer.this.outputFile.force(true);
                AfterContentTransformer.this.drain(AfterContentTransformer.this.outputFile, output);
            }
        }

        private class SinkOutputStream
        extends OutputStream {
            private SinkOutputStream() {
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                if (len <= 0) {
                    return;
                }
                AfterContentTransformer.this.outputBufferSize += (long)len;
                long max = AfterContentTransformer.this.getMaxOutputBufferSize();
                if (max >= 0L && AfterContentTransformer.this.outputBufferSize > max) {
                    Sink.this.overflow(ByteBuffer.wrap(b, off, len));
                } else {
                    byte[] copy = new byte[len];
                    System.arraycopy(b, off, copy, 0, len);
                    Sink.this.sinkBuffers.add(ByteBuffer.wrap(copy));
                }
            }

            @Override
            public void write(int b) throws IOException {
                this.write(new byte[]{(byte)b}, 0, 1);
            }
        }
    }

    private class MemoryInputStream
    extends InputStream {
        private final byte[] oneByte = new byte[1];
        private int index;
        private ByteBuffer slice;

        private MemoryInputStream() {
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            if (this.index == AfterContentTransformer.this.sourceBuffers.size()) {
                return -1;
            }
            if (this.slice == null) {
                this.slice = AfterContentTransformer.this.sourceBuffers.get(this.index).slice();
            }
            int size = Math.min(len, this.slice.remaining());
            this.slice.get(b, off, size);
            if (!this.slice.hasRemaining()) {
                ++this.index;
                this.slice = null;
            }
            return size;
        }

        @Override
        public int read() throws IOException {
            int read = this.read(this.oneByte, 0, 1);
            return read < 0 ? read : this.oneByte[0] & 0xFF;
        }

        @Override
        public void reset() throws IOException {
            this.index = 0;
            this.slice = null;
        }
    }

    private class ChannelInputStream
    extends InputStream {
        private final InputStream stream;

        private ChannelInputStream() {
            this.stream = Channels.newInputStream(AfterContentTransformer.this.inputFile);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.stream.read(b, off, len);
        }

        @Override
        public int read() throws IOException {
            return this.stream.read();
        }

        @Override
        public void reset() throws IOException {
            AfterContentTransformer.this.inputFile.position(0L);
        }
    }
}

