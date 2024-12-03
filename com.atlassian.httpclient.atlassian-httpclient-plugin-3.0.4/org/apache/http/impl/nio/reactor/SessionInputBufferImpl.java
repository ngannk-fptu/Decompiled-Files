/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.http.MessageConstraintException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.ExpandableBuffer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.CharsetUtils;

public class SessionInputBufferImpl
extends ExpandableBuffer
implements SessionInputBuffer {
    private final CharsetDecoder charDecoder;
    private final MessageConstraints constraints;
    private final int lineBufferSize;
    private CharBuffer charBuffer;

    public SessionInputBufferImpl(int bufferSize, int lineBufferSize, MessageConstraints constraints, CharsetDecoder charDecoder, ByteBufferAllocator allocator) {
        super(bufferSize, allocator != null ? allocator : HeapByteBufferAllocator.INSTANCE);
        this.lineBufferSize = Args.positive(lineBufferSize, "Line buffer size");
        this.constraints = constraints != null ? constraints : MessageConstraints.DEFAULT;
        this.charDecoder = charDecoder;
    }

    public SessionInputBufferImpl(int bufferSize, int lineBufferSize, CharsetDecoder charDecoder, ByteBufferAllocator allocator) {
        this(bufferSize, lineBufferSize, null, charDecoder, allocator);
    }

    @Deprecated
    public SessionInputBufferImpl(int bufferSize, int lineBufferSize, ByteBufferAllocator allocator, HttpParams params) {
        super(bufferSize, allocator);
        this.lineBufferSize = Args.positive(lineBufferSize, "Line buffer size");
        String charsetName = (String)params.getParameter("http.protocol.element-charset");
        Charset charset = CharsetUtils.lookup(charsetName);
        if (charset != null) {
            this.charDecoder = charset.newDecoder();
            CodingErrorAction a1 = (CodingErrorAction)params.getParameter("http.malformed.input.action");
            this.charDecoder.onMalformedInput(a1 != null ? a1 : CodingErrorAction.REPORT);
            CodingErrorAction a2 = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
            this.charDecoder.onUnmappableCharacter(a2 != null ? a2 : CodingErrorAction.REPORT);
        } else {
            this.charDecoder = null;
        }
        this.constraints = MessageConstraints.DEFAULT;
    }

    @Deprecated
    public SessionInputBufferImpl(int bufferSize, int lineBufferSize, HttpParams params) {
        this(bufferSize, lineBufferSize, HeapByteBufferAllocator.INSTANCE, params);
    }

    public SessionInputBufferImpl(int bufferSize, int lineBufferSize, Charset charset) {
        this(bufferSize, lineBufferSize, null, charset != null ? charset.newDecoder() : null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionInputBufferImpl(int bufferSize, int lineBufferSize, MessageConstraints constraints, Charset charset) {
        this(bufferSize, lineBufferSize, constraints, charset != null ? charset.newDecoder() : null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionInputBufferImpl(int bufferSize, int lineBufferSize) {
        this(bufferSize, lineBufferSize, null, null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionInputBufferImpl(int bufferSize) {
        this(bufferSize, 256, null, null, HeapByteBufferAllocator.INSTANCE);
    }

    @Override
    public int fill(ReadableByteChannel channel) throws IOException {
        Args.notNull(channel, "Channel");
        this.setInputMode();
        if (!this.buffer.hasRemaining()) {
            this.expand();
        }
        return channel.read(this.buffer);
    }

    @Override
    public int read() {
        this.setOutputMode();
        return this.buffer.get() & 0xFF;
    }

    @Override
    public int read(ByteBuffer dst, int maxLen) {
        if (dst == null) {
            return 0;
        }
        this.setOutputMode();
        int len = Math.min(dst.remaining(), maxLen);
        int chunk = Math.min(this.buffer.remaining(), len);
        if (this.buffer.remaining() > chunk) {
            int oldLimit = this.buffer.limit();
            int newLimit = this.buffer.position() + chunk;
            this.buffer.limit(newLimit);
            dst.put(this.buffer);
            this.buffer.limit(oldLimit);
            return len;
        }
        dst.put(this.buffer);
        return chunk;
    }

    @Override
    public int read(ByteBuffer dst) {
        if (dst == null) {
            return 0;
        }
        return this.read(dst, dst.remaining());
    }

    @Override
    public int read(WritableByteChannel dst, int maxLen) throws IOException {
        int bytesRead;
        if (dst == null) {
            return 0;
        }
        this.setOutputMode();
        if (this.buffer.remaining() > maxLen) {
            int oldLimit = this.buffer.limit();
            int newLimit = oldLimit - (this.buffer.remaining() - maxLen);
            this.buffer.limit(newLimit);
            bytesRead = dst.write(this.buffer);
            this.buffer.limit(oldLimit);
        } else {
            bytesRead = dst.write(this.buffer);
        }
        return bytesRead;
    }

    @Override
    public int read(WritableByteChannel dst) throws IOException {
        if (dst == null) {
            return 0;
        }
        this.setOutputMode();
        return dst.write(this.buffer);
    }

    @Override
    public boolean readLine(CharArrayBuffer lineBuffer, boolean endOfStream) throws CharacterCodingException {
        int currentLen;
        int maxLineLen;
        this.setOutputMode();
        int pos = -1;
        for (int i = this.buffer.position(); i < this.buffer.limit(); ++i) {
            byte b = this.buffer.get(i);
            if (b != 10) continue;
            pos = i + 1;
            break;
        }
        if ((maxLineLen = this.constraints.getMaxLineLength()) > 0 && (currentLen = (pos > 0 ? pos : this.buffer.limit()) - this.buffer.position()) >= maxLineLen) {
            throw new MessageConstraintException("Maximum line length limit exceeded");
        }
        if (pos == -1) {
            if (endOfStream && this.buffer.hasRemaining()) {
                pos = this.buffer.limit();
            } else {
                return false;
            }
        }
        int origLimit = this.buffer.limit();
        this.buffer.limit(pos);
        int requiredCapacity = this.buffer.limit() - this.buffer.position();
        lineBuffer.ensureCapacity(requiredCapacity);
        if (this.charDecoder == null) {
            if (this.buffer.hasArray()) {
                byte[] b = this.buffer.array();
                int off = this.buffer.position();
                int len = this.buffer.remaining();
                lineBuffer.append(b, off, len);
                this.buffer.position(off + len);
            } else {
                while (this.buffer.hasRemaining()) {
                    lineBuffer.append((char)(this.buffer.get() & 0xFF));
                }
            }
        } else {
            CoderResult result;
            if (this.charBuffer == null) {
                this.charBuffer = CharBuffer.allocate(this.lineBufferSize);
            }
            this.charDecoder.reset();
            do {
                if ((result = this.charDecoder.decode(this.buffer, this.charBuffer, true)).isError()) {
                    result.throwException();
                }
                if (!result.isOverflow()) continue;
                this.charBuffer.flip();
                lineBuffer.append(this.charBuffer.array(), this.charBuffer.position(), this.charBuffer.remaining());
                this.charBuffer.clear();
            } while (!result.isUnderflow());
            this.charDecoder.flush(this.charBuffer);
            this.charBuffer.flip();
            if (this.charBuffer.hasRemaining()) {
                lineBuffer.append(this.charBuffer.array(), this.charBuffer.position(), this.charBuffer.remaining());
            }
        }
        this.buffer.limit(origLimit);
        int len = lineBuffer.length();
        if (len > 0) {
            if (lineBuffer.charAt(len - 1) == '\n') {
                lineBuffer.setLength(--len);
            }
            if (len > 0 && lineBuffer.charAt(len - 1) == '\r') {
                lineBuffer.setLength(--len);
            }
        }
        return true;
    }

    @Override
    public String readLine(boolean endOfStream) throws CharacterCodingException {
        CharArrayBuffer tmpBuffer = new CharArrayBuffer(64);
        boolean found = this.readLine(tmpBuffer, endOfStream);
        return found ? tmpBuffer.toString() : null;
    }

    @Override
    public void clear() {
        super.clear();
    }
}

