/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class GzipOutputFilter
implements OutputFilter {
    protected static final Log log = LogFactory.getLog(GzipOutputFilter.class);
    protected HttpOutputBuffer buffer;
    protected GZIPOutputStream compressionStream = null;
    protected final OutputStream fakeOutputStream = new FakeOutputStream();

    @Override
    public int doWrite(ByteBuffer chunk) throws IOException {
        if (this.compressionStream == null) {
            this.compressionStream = new GZIPOutputStream(this.fakeOutputStream, true);
        }
        int len = chunk.remaining();
        if (chunk.hasArray()) {
            this.compressionStream.write(chunk.array(), chunk.arrayOffset() + chunk.position(), len);
            chunk.position(chunk.position() + len);
        } else {
            byte[] bytes = new byte[len];
            chunk.get(bytes);
            this.compressionStream.write(bytes, 0, len);
        }
        return len;
    }

    @Override
    public long getBytesWritten() {
        return this.buffer.getBytesWritten();
    }

    @Override
    public void flush() throws IOException {
        block4: {
            if (this.compressionStream != null) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"Flushing the compression stream!");
                    }
                    this.compressionStream.flush();
                }
                catch (IOException e) {
                    if (!log.isDebugEnabled()) break block4;
                    log.debug((Object)"Ignored exception while flushing gzip filter", (Throwable)e);
                }
            }
        }
        this.buffer.flush();
    }

    @Override
    public void setResponse(Response response) {
    }

    @Override
    public void setBuffer(HttpOutputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void end() throws IOException {
        if (this.compressionStream == null) {
            this.compressionStream = new GZIPOutputStream(this.fakeOutputStream, true);
        }
        this.compressionStream.finish();
        this.compressionStream.close();
        this.buffer.end();
    }

    @Override
    public void recycle() {
        this.compressionStream = null;
    }

    protected class FakeOutputStream
    extends OutputStream {
        protected final ByteBuffer outputChunk = ByteBuffer.allocate(1);

        protected FakeOutputStream() {
        }

        @Override
        public void write(int b) throws IOException {
            this.outputChunk.put(0, (byte)(b & 0xFF));
            GzipOutputFilter.this.buffer.doWrite(this.outputChunk);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            GzipOutputFilter.this.buffer.doWrite(ByteBuffer.wrap(b, off, len));
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}

