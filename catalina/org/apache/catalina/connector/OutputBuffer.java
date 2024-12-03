/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.WriteListener
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.CloseNowException
 *  org.apache.coyote.Constants
 *  org.apache.coyote.Response
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.C2BConverter
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.WriteListener;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.Constants;
import org.apache.coyote.Response;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.C2BConverter;
import org.apache.tomcat.util.res.StringManager;

public class OutputBuffer
extends Writer {
    private static final StringManager sm = StringManager.getManager(OutputBuffer.class);
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private final Map<Charset, C2BConverter> encoders = new HashMap<Charset, C2BConverter>();
    private final int defaultBufferSize;
    private ByteBuffer bb;
    private final CharBuffer cb;
    private boolean initial = true;
    private long bytesWritten = 0L;
    private long charsWritten = 0L;
    private volatile boolean closed = false;
    private boolean doFlush = false;
    protected C2BConverter conv;
    private Response coyoteResponse;
    private volatile boolean suspended = false;

    public OutputBuffer(int size) {
        this.defaultBufferSize = size;
        this.bb = ByteBuffer.allocate(size);
        this.clear(this.bb);
        this.cb = CharBuffer.allocate(size);
        this.clear(this.cb);
    }

    public void setResponse(Response coyoteResponse) {
        this.coyoteResponse = coyoteResponse;
    }

    public boolean isSuspended() {
        return this.suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void recycle() {
        this.initial = true;
        this.bytesWritten = 0L;
        this.charsWritten = 0L;
        if (this.bb.capacity() > 16 * this.defaultBufferSize) {
            this.bb = ByteBuffer.allocate(this.defaultBufferSize);
        }
        this.clear(this.bb);
        this.clear(this.cb);
        this.closed = false;
        this.suspended = false;
        this.doFlush = false;
        if (this.conv != null) {
            this.conv.recycle();
            this.conv = null;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        if (this.suspended) {
            return;
        }
        if (this.cb.remaining() > 0) {
            this.flushCharBuffer();
        }
        if (!(this.coyoteResponse.isCommitted() || this.coyoteResponse.getContentLengthLong() != -1L || this.coyoteResponse.getRequest().method().equals("HEAD") || this.coyoteResponse.isCommitted())) {
            this.coyoteResponse.setContentLength((long)this.bb.remaining());
        }
        if (this.coyoteResponse.getStatus() == 101) {
            this.doFlush(true);
        } else {
            this.doFlush(false);
        }
        this.closed = true;
        Request req = (Request)this.coyoteResponse.getRequest().getNote(1);
        req.inputBuffer.close();
        this.coyoteResponse.action(ActionCode.CLOSE, null);
    }

    @Override
    public void flush() throws IOException {
        this.doFlush(true);
    }

    protected void doFlush(boolean realFlush) throws IOException {
        if (this.suspended) {
            return;
        }
        try {
            this.doFlush = true;
            if (this.initial) {
                this.coyoteResponse.sendHeaders();
                this.initial = false;
            }
            if (this.cb.remaining() > 0) {
                this.flushCharBuffer();
            }
            if (this.bb.remaining() > 0) {
                this.flushByteBuffer();
            }
        }
        finally {
            this.doFlush = false;
        }
        if (realFlush) {
            this.coyoteResponse.action(ActionCode.CLIENT_FLUSH, null);
            if (this.coyoteResponse.isExceptionPresent()) {
                throw new ClientAbortException(this.coyoteResponse.getErrorException());
            }
        }
    }

    public void realWriteBytes(ByteBuffer buf) throws IOException {
        if (this.closed) {
            return;
        }
        if (buf.remaining() > 0) {
            try {
                this.coyoteResponse.doWrite(buf);
            }
            catch (CloseNowException e) {
                this.closed = true;
                throw e;
            }
            catch (IOException e) {
                this.coyoteResponse.setErrorException((Exception)e);
                throw new ClientAbortException(e);
            }
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (this.suspended) {
            return;
        }
        this.writeBytes(b, off, len);
    }

    public void write(ByteBuffer from) throws IOException {
        if (this.suspended) {
            return;
        }
        this.writeBytes(from);
    }

    private void writeBytes(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            return;
        }
        this.append(b, off, len);
        this.bytesWritten += (long)len;
        if (this.doFlush) {
            this.flushByteBuffer();
        }
    }

    private void writeBytes(ByteBuffer from) throws IOException {
        if (this.closed) {
            return;
        }
        int remaining = from.remaining();
        this.append(from);
        this.bytesWritten += (long)remaining;
        if (this.doFlush) {
            this.flushByteBuffer();
        }
    }

    public void writeByte(int b) throws IOException {
        if (this.suspended) {
            return;
        }
        if (this.isFull(this.bb)) {
            this.flushByteBuffer();
        }
        this.transfer((byte)b, this.bb);
        ++this.bytesWritten;
    }

    public void realWriteChars(CharBuffer from) throws IOException {
        while (from.remaining() > 0) {
            this.conv.convert(from, this.bb);
            if (this.bb.remaining() == 0) break;
            if (from.remaining() > 0) {
                this.flushByteBuffer();
                continue;
            }
            if (!this.conv.isUndeflow() || this.bb.limit() <= this.bb.capacity() - 4) continue;
            this.flushByteBuffer();
        }
    }

    @Override
    public void write(int c) throws IOException {
        if (this.suspended) {
            return;
        }
        if (this.isFull(this.cb)) {
            this.flushCharBuffer();
        }
        this.transfer((char)c, this.cb);
        ++this.charsWritten;
    }

    @Override
    public void write(char[] c) throws IOException {
        if (this.suspended) {
            return;
        }
        this.write(c, 0, c.length);
    }

    @Override
    public void write(char[] c, int off, int len) throws IOException {
        if (this.suspended) {
            return;
        }
        this.append(c, off, len);
        this.charsWritten += (long)len;
    }

    @Override
    public void write(String s, int off, int len) throws IOException {
        if (this.suspended) {
            return;
        }
        if (s == null) {
            throw new NullPointerException(sm.getString("outputBuffer.writeNull"));
        }
        int sOff = off;
        int sEnd = off + len;
        while (sOff < sEnd) {
            int n;
            if ((sOff += (n = this.transfer(s, sOff, sEnd - sOff, this.cb))) >= sEnd || !this.isFull(this.cb)) continue;
            this.flushCharBuffer();
        }
        this.charsWritten += (long)len;
    }

    @Override
    public void write(String s) throws IOException {
        if (this.suspended) {
            return;
        }
        if (s == null) {
            s = "null";
        }
        this.write(s, 0, s.length());
    }

    public void checkConverter() throws IOException {
        if (this.conv != null) {
            return;
        }
        Charset charset = this.coyoteResponse.getCharset();
        if (charset == null) {
            if (this.coyoteResponse.getCharacterEncoding() != null) {
                charset = B2CConverter.getCharset((String)this.coyoteResponse.getCharacterEncoding());
            }
            charset = Constants.DEFAULT_BODY_CHARSET;
        }
        this.conv = this.encoders.get(charset);
        if (this.conv == null) {
            this.conv = OutputBuffer.createConverter(charset);
            this.encoders.put(charset, this.conv);
        }
    }

    private static C2BConverter createConverter(Charset charset) throws IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                return AccessController.doPrivileged(new PrivilegedCreateConverter(charset));
            }
            catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new IOException(ex);
            }
        }
        return new C2BConverter(charset);
    }

    public long getContentWritten() {
        return this.bytesWritten + this.charsWritten;
    }

    public boolean isNew() {
        return this.bytesWritten == 0L && this.charsWritten == 0L;
    }

    public void setBufferSize(int size) {
        if (size > this.bb.capacity()) {
            this.bb = ByteBuffer.allocate(size);
            this.clear(this.bb);
        }
    }

    public void reset() {
        this.reset(false);
    }

    public void reset(boolean resetWriterStreamFlags) {
        this.clear(this.bb);
        this.clear(this.cb);
        this.bytesWritten = 0L;
        this.charsWritten = 0L;
        if (resetWriterStreamFlags) {
            if (this.conv != null) {
                this.conv.recycle();
            }
            this.conv = null;
        }
        this.initial = true;
    }

    public int getBufferSize() {
        return this.bb.capacity();
    }

    public boolean isReady() {
        return this.coyoteResponse.isReady();
    }

    public void setWriteListener(WriteListener listener) {
        this.coyoteResponse.setWriteListener(listener);
    }

    public boolean isBlocking() {
        return this.coyoteResponse.getWriteListener() == null;
    }

    public void checkRegisterForWrite() {
        this.coyoteResponse.checkRegisterForWrite();
    }

    public void append(byte[] src, int off, int len) throws IOException {
        if (this.bb.remaining() == 0) {
            this.appendByteArray(src, off, len);
        } else {
            int n = this.transfer(src, off, len, this.bb);
            off += n;
            if ((len -= n) > 0 && this.isFull(this.bb)) {
                this.flushByteBuffer();
                this.appendByteArray(src, off, len);
            }
        }
    }

    public void append(char[] src, int off, int len) throws IOException {
        if (len <= this.cb.capacity() - this.cb.limit()) {
            this.transfer(src, off, len, this.cb);
            return;
        }
        if (len + this.cb.limit() < 2 * this.cb.capacity()) {
            int n = this.transfer(src, off, len, this.cb);
            this.flushCharBuffer();
            this.transfer(src, off + n, len - n, this.cb);
        } else {
            this.flushCharBuffer();
            this.realWriteChars(CharBuffer.wrap(src, off, len));
        }
    }

    public void append(ByteBuffer from) throws IOException {
        if (this.bb.remaining() == 0) {
            this.appendByteBuffer(from);
        } else {
            this.transfer(from, this.bb);
            if (from.hasRemaining() && this.isFull(this.bb)) {
                this.flushByteBuffer();
                this.appendByteBuffer(from);
            }
        }
    }

    private void appendByteArray(byte[] src, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        int limit = this.bb.capacity();
        while (len > limit) {
            this.realWriteBytes(ByteBuffer.wrap(src, off, limit));
            len -= limit;
            off += limit;
        }
        if (len > 0) {
            this.transfer(src, off, len, this.bb);
        }
    }

    private void appendByteBuffer(ByteBuffer from) throws IOException {
        if (from.remaining() == 0) {
            return;
        }
        int limit = this.bb.capacity();
        int fromLimit = from.limit();
        while (from.remaining() > limit) {
            from.limit(from.position() + limit);
            this.realWriteBytes(from.slice());
            from.position(from.limit());
            from.limit(fromLimit);
        }
        if (from.remaining() > 0) {
            this.transfer(from, this.bb);
        }
    }

    private void flushByteBuffer() throws IOException {
        this.realWriteBytes(this.bb.slice());
        this.clear(this.bb);
    }

    private void flushCharBuffer() throws IOException {
        this.realWriteChars(this.cb.slice());
        this.clear(this.cb);
    }

    private void transfer(byte b, ByteBuffer to) {
        this.toWriteMode(to);
        to.put(b);
        this.toReadMode(to);
    }

    private void transfer(char b, CharBuffer to) {
        this.toWriteMode(to);
        to.put(b);
        this.toReadMode(to);
    }

    private int transfer(byte[] buf, int off, int len, ByteBuffer to) {
        this.toWriteMode(to);
        int max = Math.min(len, to.remaining());
        if (max > 0) {
            to.put(buf, off, max);
        }
        this.toReadMode(to);
        return max;
    }

    private int transfer(char[] buf, int off, int len, CharBuffer to) {
        this.toWriteMode(to);
        int max = Math.min(len, to.remaining());
        if (max > 0) {
            to.put(buf, off, max);
        }
        this.toReadMode(to);
        return max;
    }

    private int transfer(String s, int off, int len, CharBuffer to) {
        this.toWriteMode(to);
        int max = Math.min(len, to.remaining());
        if (max > 0) {
            to.put(s, off, off + max);
        }
        this.toReadMode(to);
        return max;
    }

    private void transfer(ByteBuffer from, ByteBuffer to) {
        this.toWriteMode(to);
        int max = Math.min(from.remaining(), to.remaining());
        if (max > 0) {
            int fromLimit = from.limit();
            from.limit(from.position() + max);
            to.put(from);
            from.limit(fromLimit);
        }
        this.toReadMode(to);
    }

    private void clear(Buffer buffer) {
        buffer.rewind().limit(0);
    }

    private boolean isFull(Buffer buffer) {
        return buffer.limit() == buffer.capacity();
    }

    private void toReadMode(Buffer buffer) {
        buffer.limit(buffer.position()).reset();
    }

    private void toWriteMode(Buffer buffer) {
        buffer.mark().position(buffer.limit()).limit(buffer.capacity());
    }

    private static class PrivilegedCreateConverter
    implements PrivilegedExceptionAction<C2BConverter> {
        private final Charset charset;

        PrivilegedCreateConverter(Charset charset) {
            this.charset = charset;
        }

        @Override
        public C2BConverter run() throws IOException {
            return new C2BConverter(this.charset);
        }
    }
}

