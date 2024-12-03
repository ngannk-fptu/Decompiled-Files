/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.io;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import org.apache.abdera.i18n.text.CodepointIterator;

public class PipeChannel
implements ReadableByteChannel,
WritableByteChannel,
Appendable,
Readable,
Closeable {
    protected String charset = Charset.defaultCharset().name();
    protected Pipe pipe;
    protected boolean flipped = false;

    public PipeChannel() {
        this.reset();
    }

    public PipeChannel(String charset) {
        this.charset = charset;
    }

    private void checkFlipped() {
        if (this.flipped) {
            throw new RuntimeException("PipeChannel is read only");
        }
    }

    private void checkNotFlipped() {
        if (!this.flipped) {
            throw new RuntimeException("PipeChannel is write only");
        }
    }

    public InputStream getInputStream() {
        this.checkNotFlipped();
        return new PipeChannelInputStream(this, Channels.newInputStream(this.pipe.source()));
    }

    public OutputStream getOutputStream() {
        this.checkFlipped();
        return new PipeChannelOutputStream(this, Channels.newOutputStream(this.pipe.sink()));
    }

    public Writer getWriter() {
        this.checkFlipped();
        return new PipeChannelWriter(this, Channels.newWriter((WritableByteChannel)this.pipe.sink(), this.charset));
    }

    public Writer getWriter(String charset) {
        this.checkFlipped();
        return new PipeChannelWriter(this, Channels.newWriter((WritableByteChannel)this.pipe.sink(), charset));
    }

    public Reader getReader(String charset) {
        this.checkNotFlipped();
        return new PipeChannelReader(this, Channels.newReader((ReadableByteChannel)this.pipe.source(), charset));
    }

    public Reader getReader() {
        this.checkNotFlipped();
        return new PipeChannelReader(this, Channels.newReader((ReadableByteChannel)this.pipe.source(), this.charset));
    }

    public CodepointIterator getIterator() {
        this.checkNotFlipped();
        return CodepointIterator.forReadableByteChannel(this.pipe.source(), this.charset);
    }

    public CodepointIterator getIterator(String charset) {
        this.checkNotFlipped();
        return CodepointIterator.forReadableByteChannel(this.pipe.source(), charset);
    }

    public int read(ByteBuffer dst) throws IOException {
        this.checkNotFlipped();
        return this.pipe.source().read(dst);
    }

    public int read(byte[] dst) throws IOException {
        this.checkNotFlipped();
        return this.pipe.source().read(ByteBuffer.wrap(dst));
    }

    public int read(byte[] dst, int offset, int length) throws IOException {
        this.checkNotFlipped();
        return this.pipe.source().read(ByteBuffer.wrap(dst, offset, length));
    }

    public boolean isOpen() {
        return this.pipe.sink().isOpen() || this.pipe.source().isOpen();
    }

    public int write(ByteBuffer src) throws IOException {
        this.checkFlipped();
        return this.pipe.sink().write(src);
    }

    public int write(byte[] src) throws IOException {
        this.checkFlipped();
        return this.write(ByteBuffer.wrap(src));
    }

    public int write(byte[] src, int offset, int len) throws IOException {
        this.checkFlipped();
        return this.write(ByteBuffer.wrap(src, offset, len));
    }

    public boolean isReadable() {
        return this.flipped;
    }

    public boolean isWritable() {
        return !this.flipped;
    }

    public void close() throws IOException {
        if (!this.flipped) {
            if (this.pipe.sink().isOpen()) {
                this.pipe.sink().close();
            }
            this.flipped = true;
        } else {
            if (this.pipe.source().isOpen()) {
                this.pipe.source().close();
            }
            this.reset();
        }
    }

    public void reset() {
        try {
            if (this.pipe != null) {
                if (this.pipe.sink().isOpen()) {
                    this.pipe.sink().close();
                }
                if (this.pipe.source().isOpen()) {
                    this.pipe.source().close();
                }
            }
            this.pipe = Pipe.open();
            this.flipped = false;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Appendable append(CharSequence csq) throws IOException {
        this.getWriter().append(csq);
        return this;
    }

    public Appendable append(char c) throws IOException {
        this.getWriter().append(c);
        return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        this.getWriter().append(csq, start, end);
        return this;
    }

    public Appendable append(CharSequence csq, String charset) throws IOException {
        this.getWriter(charset).append(csq);
        return this;
    }

    public Appendable append(char c, String charset) throws IOException {
        this.getWriter(charset).append(c);
        return this;
    }

    public Appendable append(CharSequence csq, int start, int end, String charset) throws IOException {
        this.getWriter(charset).append(csq, start, end);
        return this;
    }

    public int read(CharBuffer cb) throws IOException {
        return this.getReader().read(cb);
    }

    public int read(CharBuffer cb, String charset) throws IOException {
        return this.getReader(charset).read(cb);
    }

    private static class PipeChannelWriter
    extends FilterWriter {
        private final PipeChannel pipe;

        protected PipeChannelWriter(PipeChannel pipe, Writer in) {
            super(in);
            this.pipe = pipe;
        }

        public void close() throws IOException {
            this.pipe.close();
        }
    }

    private static class PipeChannelReader
    extends FilterReader {
        private final PipeChannel pipe;

        protected PipeChannelReader(PipeChannel pipe, Reader in) {
            super(in);
            this.pipe = pipe;
        }

        public void close() throws IOException {
            this.pipe.close();
        }
    }

    private static class PipeChannelOutputStream
    extends FilterOutputStream {
        private final PipeChannel pipe;

        protected PipeChannelOutputStream(PipeChannel pipe, OutputStream in) {
            super(in);
            this.pipe = pipe;
        }

        public void close() throws IOException {
            this.pipe.close();
        }
    }

    private static class PipeChannelInputStream
    extends FilterInputStream {
        private final PipeChannel pipe;

        protected PipeChannelInputStream(PipeChannel pipe, InputStream in) {
            super(in);
            this.pipe = pipe;
        }

        public void close() throws IOException {
            this.pipe.close();
        }
    }
}

