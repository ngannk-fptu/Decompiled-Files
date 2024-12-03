/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.message.BasicHeader
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.ByteArrayBuffer
 */
package org.apache.hc.core5.http2.hpack;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http2.hpack.HPackException;
import org.apache.hc.core5.http2.hpack.HPackHeader;
import org.apache.hc.core5.http2.hpack.HPackRepresentation;
import org.apache.hc.core5.http2.hpack.HeaderListConstraintException;
import org.apache.hc.core5.http2.hpack.Huffman;
import org.apache.hc.core5.http2.hpack.InboundDynamicTable;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.ByteArrayBuffer;

@Internal
public final class HPackDecoder {
    private static final String UNEXPECTED_EOS = "Unexpected end of HPACK data";
    private static final String MAX_LIMIT_EXCEEDED = "Max integer exceeded";
    private final InboundDynamicTable dynamicTable;
    private final ByteArrayBuffer contentBuf;
    private final CharsetDecoder charsetDecoder;
    private CharBuffer tmpBuf;
    private int maxTableSize;
    private int maxListSize;

    HPackDecoder(InboundDynamicTable dynamicTable, CharsetDecoder charsetDecoder) {
        this.dynamicTable = dynamicTable != null ? dynamicTable : new InboundDynamicTable();
        this.contentBuf = new ByteArrayBuffer(256);
        this.charsetDecoder = charsetDecoder;
        this.maxTableSize = dynamicTable != null ? dynamicTable.getMaxSize() : Integer.MAX_VALUE;
        this.maxListSize = Integer.MAX_VALUE;
    }

    HPackDecoder(InboundDynamicTable dynamicTable, Charset charset) {
        this(dynamicTable, charset != null && !StandardCharsets.US_ASCII.equals(charset) ? charset.newDecoder() : null);
    }

    public HPackDecoder(Charset charset) {
        this(new InboundDynamicTable(), charset);
    }

    public HPackDecoder(CharsetDecoder charsetDecoder) {
        this(new InboundDynamicTable(), charsetDecoder);
    }

    static int readByte(ByteBuffer src) throws HPackException {
        if (!src.hasRemaining()) {
            throw new HPackException(UNEXPECTED_EOS);
        }
        return src.get() & 0xFF;
    }

    static int peekByte(ByteBuffer src) throws HPackException {
        if (!src.hasRemaining()) {
            throw new HPackException(UNEXPECTED_EOS);
        }
        int pos = src.position();
        int b = src.get() & 0xFF;
        src.position(pos);
        return b;
    }

    static int decodeInt(ByteBuffer src, int n) throws HPackException {
        int nbits = 255 >>> 8 - n;
        int value = HPackDecoder.readByte(src) & nbits;
        if (value < nbits) {
            return value;
        }
        for (int m = 0; m < 32; m += 7) {
            int b = HPackDecoder.readByte(src);
            if ((b & 0x80) != 0) {
                value += (b & 0x7F) << m;
                continue;
            }
            if (m == 28 && (b & 0xF8) != 0) break;
            return value += b << m;
        }
        throw new HPackException(MAX_LIMIT_EXCEEDED);
    }

    static void decodePlainString(ByteArrayBuffer buffer, ByteBuffer src) throws HPackException {
        int remaining;
        int strLen = HPackDecoder.decodeInt(src, 7);
        if (strLen > (remaining = src.remaining())) {
            throw new HPackException(UNEXPECTED_EOS);
        }
        int originalLimit = src.limit();
        src.limit(originalLimit - (remaining - strLen));
        buffer.append(src);
        src.limit(originalLimit);
    }

    static void decodeHuffman(ByteArrayBuffer buffer, ByteBuffer src) throws HPackException {
        int strLen = HPackDecoder.decodeInt(src, 7);
        if (strLen > src.remaining()) {
            throw new HPackException(UNEXPECTED_EOS);
        }
        int limit = src.limit();
        src.limit(src.position() + strLen);
        Huffman.DECODER.decode(buffer, src);
        src.limit(limit);
    }

    void decodeString(ByteArrayBuffer buffer, ByteBuffer src) throws HPackException {
        int firstByte = HPackDecoder.peekByte(src);
        if ((firstByte & 0x80) == 128) {
            HPackDecoder.decodeHuffman(buffer, src);
        } else {
            HPackDecoder.decodePlainString(buffer, src);
        }
    }

    private void clearState() {
        if (this.tmpBuf != null) {
            this.tmpBuf.clear();
        }
        if (this.charsetDecoder != null) {
            this.charsetDecoder.reset();
        }
        this.contentBuf.clear();
    }

    private void expandCapacity(int capacity) {
        CharBuffer previous = this.tmpBuf;
        this.tmpBuf = CharBuffer.allocate(capacity);
        previous.flip();
        this.tmpBuf.put(previous);
    }

    private void ensureCapacity(int extra) {
        int requiredCapacity;
        if (this.tmpBuf == null) {
            this.tmpBuf = CharBuffer.allocate(Math.max(256, extra));
        }
        if ((requiredCapacity = this.tmpBuf.remaining() + extra) > this.tmpBuf.capacity()) {
            this.expandCapacity(requiredCapacity);
        }
    }

    int decodeString(ByteBuffer src, StringBuilder buf) throws HPackException, CharacterCodingException {
        this.clearState();
        this.decodeString(this.contentBuf, src);
        int binaryLen = this.contentBuf.length();
        if (binaryLen == 0) {
            return 0;
        }
        if (this.charsetDecoder == null) {
            buf.ensureCapacity(binaryLen);
            for (int i = 0; i < binaryLen; ++i) {
                buf.append((char)(this.contentBuf.byteAt(i) & 0xFF));
            }
        } else {
            CoderResult result;
            ByteBuffer in = ByteBuffer.wrap(this.contentBuf.array(), 0, binaryLen);
            while (in.hasRemaining()) {
                this.ensureCapacity(in.remaining());
                result = this.charsetDecoder.decode(in, this.tmpBuf, true);
                if (!result.isError()) continue;
                result.throwException();
            }
            this.ensureCapacity(8);
            result = this.charsetDecoder.flush(this.tmpBuf);
            if (result.isError()) {
                result.throwException();
            }
            this.tmpBuf.flip();
            buf.append(this.tmpBuf);
        }
        return binaryLen;
    }

    HPackHeader decodeLiteralHeader(ByteBuffer src, HPackRepresentation representation) throws HPackException, CharacterCodingException {
        String name;
        int nameLen;
        StringBuilder buf;
        int n = representation == HPackRepresentation.WITH_INDEXING ? 6 : 4;
        int index = HPackDecoder.decodeInt(src, n);
        if (index == 0) {
            buf = new StringBuilder();
            nameLen = this.decodeString(src, buf);
            name = buf.toString();
        } else {
            HPackHeader existing = this.dynamicTable.getHeader(index);
            if (existing == null) {
                throw new HPackException("Invalid header index");
            }
            name = existing.getName();
            nameLen = existing.getNameLen();
        }
        buf = new StringBuilder();
        int valueLen = this.decodeString(src, buf);
        String value = buf.toString();
        HPackHeader header = new HPackHeader(name, nameLen, value, valueLen, representation == HPackRepresentation.NEVER_INDEXED);
        if (representation == HPackRepresentation.WITH_INDEXING) {
            this.dynamicTable.add(header);
        }
        return header;
    }

    HPackHeader decodeIndexedHeader(ByteBuffer src) throws HPackException {
        int index = HPackDecoder.decodeInt(src, 7);
        HPackHeader existing = this.dynamicTable.getHeader(index);
        if (existing == null) {
            throw new HPackException("Invalid header index");
        }
        return existing;
    }

    public Header decodeHeader(ByteBuffer src) throws HPackException {
        HPackHeader header = this.decodeHPackHeader(src);
        return header != null ? new BasicHeader(header.getName(), (Object)header.getValue(), header.isSensitive()) : null;
    }

    HPackHeader decodeHPackHeader(ByteBuffer src) throws HPackException {
        try {
            while (src.hasRemaining()) {
                int b = HPackDecoder.peekByte(src);
                if ((b & 0x80) == 128) {
                    return this.decodeIndexedHeader(src);
                }
                if ((b & 0xC0) == 64) {
                    return this.decodeLiteralHeader(src, HPackRepresentation.WITH_INDEXING);
                }
                if ((b & 0xF0) == 0) {
                    return this.decodeLiteralHeader(src, HPackRepresentation.WITHOUT_INDEXING);
                }
                if ((b & 0xF0) == 16) {
                    return this.decodeLiteralHeader(src, HPackRepresentation.NEVER_INDEXED);
                }
                if ((b & 0xE0) == 32) {
                    int maxSize = HPackDecoder.decodeInt(src, 5);
                    this.dynamicTable.setMaxSize(Math.min(this.maxTableSize, maxSize));
                    continue;
                }
                throw new HPackException("Unexpected header first byte: 0x" + Integer.toHexString(b));
            }
            return null;
        }
        catch (CharacterCodingException ex) {
            throw new HPackException(ex.getMessage(), ex);
        }
    }

    public List<Header> decodeHeaders(ByteBuffer src) throws HPackException {
        HPackHeader header;
        boolean enforceSizeLimit = this.maxListSize < Integer.MAX_VALUE;
        int listSize = 0;
        ArrayList<Header> list = new ArrayList<Header>();
        while (src.hasRemaining() && (header = this.decodeHPackHeader(src)) != null) {
            if (enforceSizeLimit && (listSize += header.getTotalSize()) >= this.maxListSize) {
                throw new HeaderListConstraintException("Maximum header list size exceeded");
            }
            list.add((Header)new BasicHeader(header.getName(), (Object)header.getValue(), header.isSensitive()));
        }
        return list;
    }

    public int getMaxTableSize() {
        return this.maxTableSize;
    }

    public void setMaxTableSize(int maxTableSize) {
        Args.notNegative((int)maxTableSize, (String)"Max table size");
        this.maxTableSize = maxTableSize;
        this.dynamicTable.setMaxSize(maxTableSize);
    }

    public int getMaxListSize() {
        return this.maxListSize;
    }

    public void setMaxListSize(int maxListSize) {
        Args.notNegative((int)maxListSize, (String)"Max list size");
        this.maxListSize = maxListSize;
    }
}

