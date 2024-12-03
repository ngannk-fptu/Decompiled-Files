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
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.http.nio.reactor.SessionOutputBuffer;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.ExpandableBuffer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.CharsetUtils;

public class SessionOutputBufferImpl
extends ExpandableBuffer
implements SessionOutputBuffer {
    private static final byte[] CRLF = new byte[]{13, 10};
    private final CharsetEncoder charEncoder;
    private final int lineBufferSize;
    private CharBuffer charBuffer;

    public SessionOutputBufferImpl(int bufferSize, int lineBufferSize, CharsetEncoder charEncoder, ByteBufferAllocator allocator) {
        super(bufferSize, allocator != null ? allocator : HeapByteBufferAllocator.INSTANCE);
        this.lineBufferSize = Args.positive(lineBufferSize, "Line buffer size");
        this.charEncoder = charEncoder;
    }

    @Deprecated
    public SessionOutputBufferImpl(int bufferSize, int lineBufferSize, ByteBufferAllocator allocator, HttpParams params) {
        super(bufferSize, allocator);
        this.lineBufferSize = Args.positive(lineBufferSize, "Line buffer size");
        String charsetName = (String)params.getParameter("http.protocol.element-charset");
        Charset charset = CharsetUtils.lookup(charsetName);
        if (charset != null) {
            this.charEncoder = charset.newEncoder();
            CodingErrorAction a1 = (CodingErrorAction)params.getParameter("http.malformed.input.action");
            this.charEncoder.onMalformedInput(a1 != null ? a1 : CodingErrorAction.REPORT);
            CodingErrorAction a2 = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
            this.charEncoder.onUnmappableCharacter(a2 != null ? a2 : CodingErrorAction.REPORT);
        } else {
            this.charEncoder = null;
        }
    }

    @Deprecated
    public SessionOutputBufferImpl(int bufferSize, int lineBufferSize, HttpParams params) {
        this(bufferSize, lineBufferSize, HeapByteBufferAllocator.INSTANCE, params);
    }

    public SessionOutputBufferImpl(int bufferSize) {
        this(bufferSize, 256, null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionOutputBufferImpl(int bufferSize, int lineBufferSize, Charset charset) {
        this(bufferSize, lineBufferSize, charset != null ? charset.newEncoder() : null, HeapByteBufferAllocator.INSTANCE);
    }

    public SessionOutputBufferImpl(int bufferSize, int lineBufferSize) {
        this(bufferSize, lineBufferSize, null, HeapByteBufferAllocator.INSTANCE);
    }

    public void reset(HttpParams params) {
        this.clear();
    }

    @Override
    public int flush(WritableByteChannel channel) throws IOException {
        Args.notNull(channel, "Channel");
        this.setOutputMode();
        return channel.write(this.buffer);
    }

    @Override
    public void write(ByteBuffer src) {
        if (src == null) {
            return;
        }
        this.setInputMode();
        int requiredCapacity = this.buffer.position() + src.remaining();
        this.ensureCapacity(requiredCapacity);
        this.buffer.put(src);
    }

    @Override
    public void write(ReadableByteChannel src) throws IOException {
        if (src == null) {
            return;
        }
        this.setInputMode();
        src.read(this.buffer);
    }

    private void write(byte[] b) {
        if (b == null) {
            return;
        }
        this.setInputMode();
        boolean off = false;
        int len = b.length;
        int requiredCapacity = this.buffer.position() + len;
        this.ensureCapacity(requiredCapacity);
        this.buffer.put(b, 0, len);
    }

    private void writeCRLF() {
        this.write(CRLF);
    }

    @Override
    public void writeLine(CharArrayBuffer lineBuffer) throws CharacterCodingException {
        if (lineBuffer == null) {
            return;
        }
        this.setInputMode();
        if (lineBuffer.length() > 0) {
            if (this.charEncoder == null) {
                int requiredCapacity = this.buffer.position() + lineBuffer.length();
                this.ensureCapacity(requiredCapacity);
                if (this.buffer.hasArray()) {
                    byte[] b = this.buffer.array();
                    int len = lineBuffer.length();
                    int off = this.buffer.position();
                    for (int i = 0; i < len; ++i) {
                        b[off + i] = (byte)lineBuffer.charAt(i);
                    }
                    this.buffer.position(off + len);
                } else {
                    for (int i = 0; i < lineBuffer.length(); ++i) {
                        this.buffer.put((byte)lineBuffer.charAt(i));
                    }
                }
            } else {
                int l;
                if (this.charBuffer == null) {
                    this.charBuffer = CharBuffer.allocate(this.lineBufferSize);
                }
                this.charEncoder.reset();
                int offset = 0;
                for (int remaining = lineBuffer.length(); remaining > 0; remaining -= l) {
                    l = this.charBuffer.remaining();
                    boolean eol = false;
                    if (remaining <= l) {
                        l = remaining;
                        eol = true;
                    }
                    this.charBuffer.put(lineBuffer.buffer(), offset, l);
                    this.charBuffer.flip();
                    boolean retry = true;
                    while (retry) {
                        CoderResult result = this.charEncoder.encode(this.charBuffer, this.buffer, eol);
                        if (result.isError()) {
                            result.throwException();
                        }
                        if (result.isOverflow()) {
                            this.expand();
                        }
                        retry = !result.isUnderflow();
                    }
                    this.charBuffer.compact();
                    offset += l;
                }
                boolean retry = true;
                while (retry) {
                    CoderResult result = this.charEncoder.flush(this.buffer);
                    if (result.isError()) {
                        result.throwException();
                    }
                    if (result.isOverflow()) {
                        this.expand();
                    }
                    retry = !result.isUnderflow();
                }
            }
        }
        this.writeCRLF();
    }

    @Override
    public void writeLine(String s) throws IOException {
        if (s == null) {
            return;
        }
        if (s.length() > 0) {
            CharArrayBuffer tmp = new CharArrayBuffer(s.length());
            tmp.append(s);
            this.writeLine(tmp);
        } else {
            this.write(CRLF);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }
}

