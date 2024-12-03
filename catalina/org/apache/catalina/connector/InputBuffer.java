/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.BadRequestException
 *  org.apache.coyote.Constants
 *  org.apache.coyote.Request
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.ByteChunk$ByteInputChannel
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.net.ApplicationBufferHandler
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ReadListener;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.security.SecurityUtil;
import org.apache.coyote.ActionCode;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.Constants;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

public class InputBuffer
extends Reader
implements ByteChunk.ByteInputChannel,
ApplicationBufferHandler {
    protected static final StringManager sm = StringManager.getManager(InputBuffer.class);
    private static final Log log = LogFactory.getLog(InputBuffer.class);
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public final int INITIAL_STATE = 0;
    public final int CHAR_STATE = 1;
    public final int BYTE_STATE = 2;
    private static final Map<Charset, SynchronizedStack<B2CConverter>> encoders = new ConcurrentHashMap<Charset, SynchronizedStack<B2CConverter>>();
    private ByteBuffer bb;
    private CharBuffer cb;
    private int state = 0;
    private boolean closed = false;
    protected B2CConverter conv;
    private org.apache.coyote.Request coyoteRequest;
    private int markPos = -1;
    private int readLimit;
    private final int size;

    public InputBuffer() {
        this(8192);
    }

    public InputBuffer(int size) {
        this.size = size;
        this.bb = ByteBuffer.allocate(size);
        this.clear(this.bb);
        this.cb = CharBuffer.allocate(size);
        this.clear(this.cb);
        this.readLimit = size;
    }

    public void setRequest(org.apache.coyote.Request coyoteRequest) {
        this.coyoteRequest = coyoteRequest;
    }

    public void recycle() {
        this.state = 0;
        if (this.cb.capacity() > this.size) {
            this.cb = CharBuffer.allocate(this.size);
            this.clear(this.cb);
        } else {
            this.clear(this.cb);
        }
        this.readLimit = this.size;
        this.markPos = -1;
        this.clear(this.bb);
        this.closed = false;
        if (this.conv != null) {
            this.conv.recycle();
            encoders.get(this.conv.getCharset()).push((Object)this.conv);
            this.conv = null;
        }
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
    }

    public int available() {
        int available = this.availableInThisBuffer();
        if (available == 0) {
            this.coyoteRequest.action(ActionCode.AVAILABLE, (Object)(this.coyoteRequest.getReadListener() != null ? 1 : 0));
            available = this.coyoteRequest.getAvailable() > 0 ? 1 : 0;
        }
        return available;
    }

    private int availableInThisBuffer() {
        int available = 0;
        if (this.state == 2) {
            available = this.bb.remaining();
        } else if (this.state == 1) {
            available = this.cb.remaining();
        }
        return available;
    }

    public void setReadListener(ReadListener listener) {
        this.coyoteRequest.setReadListener(listener);
    }

    public boolean isFinished() {
        int available = 0;
        if (this.state == 2) {
            available = this.bb.remaining();
        } else if (this.state == 1) {
            available = this.cb.remaining();
        }
        if (available > 0) {
            return false;
        }
        return this.coyoteRequest.isFinished();
    }

    public boolean isReady() {
        if (this.coyoteRequest.getReadListener() == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("inputBuffer.requiresNonBlocking"));
            }
            return false;
        }
        if (this.isFinished()) {
            if (!this.coyoteRequest.isRequestThread()) {
                this.coyoteRequest.action(ActionCode.DISPATCH_READ, null);
                this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
            }
            return false;
        }
        if (this.availableInThisBuffer() > 0) {
            return true;
        }
        return this.coyoteRequest.isReady();
    }

    boolean isBlocking() {
        return this.coyoteRequest.getReadListener() == null;
    }

    public int realReadBytes() throws IOException {
        if (this.closed) {
            return -1;
        }
        if (this.state == 0) {
            this.state = 2;
        }
        try {
            return this.coyoteRequest.doRead((ApplicationBufferHandler)this);
        }
        catch (BadRequestException bre) {
            this.handleReadException((Exception)((Object)bre));
            throw bre;
        }
        catch (IOException ioe) {
            this.handleReadException(ioe);
            throw new ClientAbortException(ioe);
        }
    }

    private void handleReadException(Exception e) throws IOException {
        this.coyoteRequest.setErrorException(e);
        Request request = (Request)this.coyoteRequest.getNote(1);
        Response response = request.getResponse();
        request.setAttribute("javax.servlet.error.exception", e);
        response.sendError(400);
    }

    public int readByte() throws IOException {
        this.throwIfClosed();
        if (this.checkByteBufferEof()) {
            return -1;
        }
        return this.bb.get() & 0xFF;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        this.throwIfClosed();
        if (this.checkByteBufferEof()) {
            return -1;
        }
        int n = Math.min(len, this.bb.remaining());
        this.bb.get(b, off, n);
        return n;
    }

    public int read(ByteBuffer to) throws IOException {
        this.throwIfClosed();
        if (this.checkByteBufferEof()) {
            return -1;
        }
        int n = Math.min(to.remaining(), this.bb.remaining());
        int orgLimit = this.bb.limit();
        this.bb.limit(this.bb.position() + n);
        to.put(this.bb);
        this.bb.limit(orgLimit);
        to.limit(to.position()).position(to.position() - n);
        return n;
    }

    public int realReadChars() throws IOException {
        int nRead;
        this.checkConverter();
        boolean eof = false;
        if (this.bb.remaining() <= 0 && (nRead = this.realReadBytes()) < 0) {
            eof = true;
        }
        if (this.markPos == -1) {
            this.clear(this.cb);
        } else {
            this.makeSpace(this.bb.remaining());
            if (this.cb.capacity() - this.cb.limit() == 0 && this.bb.remaining() != 0) {
                this.clear(this.cb);
                this.markPos = -1;
            }
        }
        this.state = 1;
        this.conv.convert(this.bb, this.cb, (ByteChunk.ByteInputChannel)this, eof);
        if (this.cb.remaining() == 0 && eof) {
            return -1;
        }
        return this.cb.remaining();
    }

    @Override
    public int read() throws IOException {
        this.throwIfClosed();
        if (this.checkCharBufferEof()) {
            return -1;
        }
        return this.cb.get();
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        this.throwIfClosed();
        return this.read(cbuf, 0, cbuf.length);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        this.throwIfClosed();
        if (this.checkCharBufferEof()) {
            return -1;
        }
        int n = Math.min(len, this.cb.remaining());
        this.cb.get(cbuf, off, n);
        return n;
    }

    @Override
    public long skip(long n) throws IOException {
        this.throwIfClosed();
        if (n < 0L) {
            throw new IllegalArgumentException();
        }
        long nRead = 0L;
        while (nRead < n) {
            if ((long)this.cb.remaining() >= n) {
                this.cb.position(this.cb.position() + (int)n);
                nRead = n;
                continue;
            }
            nRead += (long)this.cb.remaining();
            this.cb.position(this.cb.limit());
            int nb = this.realReadChars();
            if (nb >= 0) continue;
            break;
        }
        return nRead;
    }

    @Override
    public boolean ready() throws IOException {
        this.throwIfClosed();
        if (this.state == 0) {
            this.state = 1;
        }
        return this.available() > 0;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.throwIfClosed();
        if (this.cb.remaining() <= 0) {
            this.clear(this.cb);
        } else if (this.cb.capacity() > 2 * this.size && this.cb.remaining() < this.cb.position()) {
            this.cb.compact();
            this.cb.flip();
        }
        this.readLimit = this.cb.position() + readAheadLimit + this.size;
        this.markPos = this.cb.position();
    }

    @Override
    public void reset() throws IOException {
        this.throwIfClosed();
        if (this.state == 1) {
            if (this.markPos < 0) {
                this.clear(this.cb);
                this.markPos = -1;
                IOException ioe = new IOException();
                this.coyoteRequest.setErrorException((Exception)ioe);
                throw ioe;
            }
            this.cb.position(this.markPos);
        } else {
            this.clear(this.bb);
        }
    }

    private void throwIfClosed() throws IOException {
        if (this.closed) {
            IOException ioe = new IOException(sm.getString("inputBuffer.streamClosed"));
            this.coyoteRequest.setErrorException((Exception)ioe);
            throw ioe;
        }
    }

    public void checkConverter() throws IOException {
        SynchronizedStack<B2CConverter> stack;
        if (this.conv != null) {
            return;
        }
        Charset charset = this.coyoteRequest.getCharset();
        if (charset == null) {
            charset = Constants.DEFAULT_BODY_CHARSET;
        }
        if ((stack = encoders.get(charset)) == null) {
            stack = new SynchronizedStack<B2CConverter>();
            encoders.putIfAbsent(charset, stack);
            stack = encoders.get(charset);
        }
        this.conv = (B2CConverter)stack.pop();
        if (this.conv == null) {
            this.conv = InputBuffer.createConverter(charset);
        }
    }

    private static B2CConverter createConverter(Charset charset) throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                return AccessController.doPrivileged(new PrivilegedCreateConverter(charset));
            }
            catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new IOException(e);
            }
        }
        return new B2CConverter(charset);
    }

    public void setByteBuffer(ByteBuffer buffer) {
        this.bb = buffer;
    }

    public ByteBuffer getByteBuffer() {
        return this.bb;
    }

    public void expand(int size) {
    }

    private boolean checkByteBufferEof() throws IOException {
        int n;
        return this.bb.remaining() == 0 && (n = this.realReadBytes()) < 0;
    }

    private boolean checkCharBufferEof() throws IOException {
        int n;
        return this.cb.remaining() == 0 && (n = this.realReadChars()) < 0;
    }

    private void clear(Buffer buffer) {
        buffer.rewind().limit(0);
    }

    private void makeSpace(int count) {
        int desiredSize = this.cb.limit() + count;
        if (desiredSize > this.readLimit) {
            desiredSize = this.readLimit;
        }
        if (desiredSize <= this.cb.capacity()) {
            return;
        }
        int newSize = 2 * this.cb.capacity();
        if (desiredSize >= newSize) {
            newSize = 2 * this.cb.capacity() + count;
        }
        if (newSize > this.readLimit) {
            newSize = this.readLimit;
        }
        CharBuffer tmp = CharBuffer.allocate(newSize);
        int oldPosition = this.cb.position();
        this.cb.position(0);
        tmp.put(this.cb);
        tmp.flip();
        tmp.position(oldPosition);
        this.cb = tmp;
        tmp = null;
    }

    private static class PrivilegedCreateConverter
    implements PrivilegedExceptionAction<B2CConverter> {
        private final Charset charset;

        PrivilegedCreateConverter(Charset charset) {
            this.charset = charset;
        }

        @Override
        public B2CConverter run() throws IOException {
            return new B2CConverter(this.charset);
        }
    }
}

