/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.Response;
import org.apache.coyote.http11.Constants;
import org.apache.coyote.http11.HeadersTooLargeException;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public class Http11OutputBuffer
implements HttpOutputBuffer {
    protected static final StringManager sm = StringManager.getManager(Http11OutputBuffer.class);
    protected final Response response;
    private volatile boolean ackSent = false;
    protected boolean responseFinished;
    protected final ByteBuffer headerBuffer;
    protected OutputFilter[] filterLibrary;
    protected OutputFilter[] activeFilters;
    protected int lastActiveFilter;
    protected HttpOutputBuffer outputStreamOutputBuffer;
    protected SocketWrapperBase<?> socketWrapper;
    protected long byteCount = 0L;

    protected Http11OutputBuffer(Response response, int headerBufferSize) {
        this.response = response;
        this.headerBuffer = ByteBuffer.allocate(headerBufferSize);
        this.filterLibrary = new OutputFilter[0];
        this.activeFilters = new OutputFilter[0];
        this.lastActiveFilter = -1;
        this.responseFinished = false;
        this.outputStreamOutputBuffer = new SocketOutputBuffer();
    }

    public void addFilter(OutputFilter filter) {
        OutputFilter[] newFilterLibrary = Arrays.copyOf(this.filterLibrary, this.filterLibrary.length + 1);
        newFilterLibrary[this.filterLibrary.length] = filter;
        this.filterLibrary = newFilterLibrary;
        this.activeFilters = new OutputFilter[this.filterLibrary.length];
    }

    public OutputFilter[] getFilters() {
        return this.filterLibrary;
    }

    public void addActiveFilter(OutputFilter filter) {
        if (this.lastActiveFilter == -1) {
            filter.setBuffer(this.outputStreamOutputBuffer);
        } else {
            for (int i = 0; i <= this.lastActiveFilter; ++i) {
                if (this.activeFilters[i] != filter) continue;
                return;
            }
            filter.setBuffer(this.activeFilters[this.lastActiveFilter]);
        }
        this.activeFilters[++this.lastActiveFilter] = filter;
        filter.setResponse(this.response);
    }

    @Override
    public int doWrite(ByteBuffer chunk) throws IOException {
        if (!this.response.isCommitted()) {
            this.response.action(ActionCode.COMMIT, null);
        }
        if (this.lastActiveFilter == -1) {
            return this.outputStreamOutputBuffer.doWrite(chunk);
        }
        return this.activeFilters[this.lastActiveFilter].doWrite(chunk);
    }

    @Override
    public long getBytesWritten() {
        if (this.lastActiveFilter == -1) {
            return this.outputStreamOutputBuffer.getBytesWritten();
        }
        return this.activeFilters[this.lastActiveFilter].getBytesWritten();
    }

    @Override
    public void flush() throws IOException {
        if (this.lastActiveFilter == -1) {
            this.outputStreamOutputBuffer.flush();
        } else {
            this.activeFilters[this.lastActiveFilter].flush();
        }
    }

    @Override
    public void end() throws IOException {
        if (this.responseFinished) {
            return;
        }
        if (this.lastActiveFilter == -1) {
            this.outputStreamOutputBuffer.end();
        } else {
            this.activeFilters[this.lastActiveFilter].end();
        }
        this.responseFinished = true;
    }

    void resetHeaderBuffer() {
        this.headerBuffer.position(0).limit(this.headerBuffer.capacity());
    }

    public void recycle() {
        this.nextRequest();
        this.socketWrapper = null;
    }

    public void nextRequest() {
        for (int i = 0; i <= this.lastActiveFilter; ++i) {
            this.activeFilters[i].recycle();
        }
        this.response.recycle();
        this.headerBuffer.position(0).limit(this.headerBuffer.capacity());
        this.lastActiveFilter = -1;
        this.ackSent = false;
        this.responseFinished = false;
        this.byteCount = 0L;
    }

    public void init(SocketWrapperBase<?> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    public void sendAck() throws IOException {
        if (!this.response.isCommitted() && !this.ackSent) {
            this.ackSent = true;
            this.socketWrapper.write(this.isBlocking(), Constants.ACK_BYTES, 0, Constants.ACK_BYTES.length);
            if (this.flushBuffer(true)) {
                throw new IOException(sm.getString("iob.failedwrite.ack"));
            }
        }
    }

    protected void commit() throws IOException {
        block5: {
            this.response.setCommitted(true);
            if (this.headerBuffer.position() > 0) {
                this.headerBuffer.flip();
                try {
                    SocketWrapperBase<?> socketWrapper = this.socketWrapper;
                    if (socketWrapper != null) {
                        socketWrapper.write(this.isBlocking(), this.headerBuffer);
                        break block5;
                    }
                    throw new CloseNowException(sm.getString("iob.failedwrite"));
                }
                finally {
                    this.headerBuffer.position(0).limit(this.headerBuffer.capacity());
                }
            }
        }
    }

    public void sendStatus() {
        this.write(Constants.HTTP_11_BYTES);
        this.headerBuffer.put((byte)32);
        int status = this.response.getStatus();
        switch (status) {
            case 200: {
                this.write(Constants._200_BYTES);
                break;
            }
            case 400: {
                this.write(Constants._400_BYTES);
                break;
            }
            case 404: {
                this.write(Constants._404_BYTES);
                break;
            }
            default: {
                this.write(status);
            }
        }
        this.headerBuffer.put((byte)32);
        this.headerBuffer.put((byte)13).put((byte)10);
    }

    public void sendHeader(MessageBytes name, MessageBytes value) {
        this.write(name);
        this.headerBuffer.put((byte)58).put((byte)32);
        this.write(value);
        this.headerBuffer.put((byte)13).put((byte)10);
    }

    public void endHeaders() {
        this.headerBuffer.put((byte)13).put((byte)10);
    }

    private void write(MessageBytes mb) {
        if (mb.getType() != 2) {
            mb.toBytes();
            ByteChunk bc = mb.getByteChunk();
            byte[] buffer = bc.getBuffer();
            for (int i = bc.getOffset(); i < bc.getLength(); ++i) {
                if ((buffer[i] <= -1 || buffer[i] > 31 || buffer[i] == 9) && buffer[i] != 127) continue;
                buffer[i] = 32;
            }
        }
        this.write(mb.getByteChunk());
    }

    private void write(ByteChunk bc) {
        int length = bc.getLength();
        this.checkLengthBeforeWrite(length);
        this.headerBuffer.put(bc.getBytes(), bc.getStart(), length);
    }

    public void write(byte[] b) {
        this.checkLengthBeforeWrite(b.length);
        this.headerBuffer.put(b);
    }

    private void write(int value) {
        String s = Integer.toString(value);
        int len = s.length();
        this.checkLengthBeforeWrite(len);
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            this.headerBuffer.put((byte)c);
        }
    }

    private void checkLengthBeforeWrite(int length) {
        if (this.headerBuffer.position() + length + 4 > this.headerBuffer.capacity()) {
            throw new HeadersTooLargeException(sm.getString("iob.responseheadertoolarge.error"));
        }
    }

    protected boolean flushBuffer(boolean block) throws IOException {
        return this.socketWrapper.flush(block);
    }

    protected final boolean isBlocking() {
        return this.response.getWriteListener() == null;
    }

    protected final boolean isReady() {
        boolean result;
        boolean bl = result = !this.hasDataToWrite();
        if (!result) {
            this.socketWrapper.registerWriteInterest();
        }
        return result;
    }

    public boolean hasDataToWrite() {
        return this.socketWrapper.hasDataToWrite();
    }

    public void registerWriteInterest() {
        this.socketWrapper.registerWriteInterest();
    }

    boolean isChunking() {
        for (int i = 0; i < this.lastActiveFilter; ++i) {
            if (this.activeFilters[i] != this.filterLibrary[1]) continue;
            return true;
        }
        return false;
    }

    protected class SocketOutputBuffer
    implements HttpOutputBuffer {
        protected SocketOutputBuffer() {
        }

        @Override
        public int doWrite(ByteBuffer chunk) throws IOException {
            try {
                int len = chunk.remaining();
                SocketWrapperBase<?> socketWrapper = Http11OutputBuffer.this.socketWrapper;
                if (socketWrapper == null) {
                    throw new CloseNowException(sm.getString("iob.failedwrite"));
                }
                socketWrapper.write(Http11OutputBuffer.this.isBlocking(), chunk);
                Http11OutputBuffer.this.byteCount += (long)(len -= chunk.remaining());
                return len;
            }
            catch (IOException ioe) {
                Http11OutputBuffer.this.response.action(ActionCode.CLOSE_NOW, ioe);
                throw ioe;
            }
        }

        @Override
        public long getBytesWritten() {
            return Http11OutputBuffer.this.byteCount;
        }

        @Override
        public void end() throws IOException {
            Http11OutputBuffer.this.socketWrapper.flush(true);
        }

        @Override
        public void flush() throws IOException {
            Http11OutputBuffer.this.socketWrapper.flush(Http11OutputBuffer.this.isBlocking());
        }
    }
}

