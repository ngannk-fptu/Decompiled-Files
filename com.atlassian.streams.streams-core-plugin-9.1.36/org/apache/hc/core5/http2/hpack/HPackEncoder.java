/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http2.hpack.HPackEntry;
import org.apache.hc.core5.http2.hpack.HPackHeader;
import org.apache.hc.core5.http2.hpack.HPackRepresentation;
import org.apache.hc.core5.http2.hpack.Huffman;
import org.apache.hc.core5.http2.hpack.OutboundDynamicTable;
import org.apache.hc.core5.http2.hpack.StaticTable;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.ByteArrayBuffer;
import org.apache.hc.core5.util.LangUtils;

@Internal
public final class HPackEncoder {
    private final OutboundDynamicTable dynamicTable;
    private final ByteArrayBuffer huffmanBuf;
    private final CharsetEncoder charsetEncoder;
    private ByteBuffer tmpBuf;
    private int maxTableSize;

    HPackEncoder(OutboundDynamicTable dynamicTable, CharsetEncoder charsetEncoder) {
        this.dynamicTable = dynamicTable != null ? dynamicTable : new OutboundDynamicTable();
        this.huffmanBuf = new ByteArrayBuffer(128);
        this.charsetEncoder = charsetEncoder;
    }

    HPackEncoder(OutboundDynamicTable dynamicTable, Charset charset) {
        this(dynamicTable, charset != null && !StandardCharsets.US_ASCII.equals(charset) ? charset.newEncoder() : null);
    }

    public HPackEncoder(Charset charset) {
        this(new OutboundDynamicTable(), charset);
    }

    public HPackEncoder(CharsetEncoder charsetEncoder) {
        this(new OutboundDynamicTable(), charsetEncoder);
    }

    static void encodeInt(ByteArrayBuffer dst, int n, int i, int mask) {
        int value = i;
        int nbits = 255 >>> 8 - n;
        if (value < nbits) {
            dst.append(i | mask);
        } else {
            dst.append(nbits | mask);
            value -= nbits;
            while (value >= 128) {
                dst.append(value & 0x7F | 0x80);
                value >>>= 7;
            }
            dst.append(value);
        }
    }

    static void encodeHuffman(ByteArrayBuffer dst, ByteBuffer src) {
        Huffman.ENCODER.encode(dst, src);
    }

    void encodeString(ByteArrayBuffer dst, ByteBuffer src, boolean huffman) {
        int strLen = src.remaining();
        if (huffman) {
            this.huffmanBuf.clear();
            this.huffmanBuf.ensureCapacity(strLen);
            Huffman.ENCODER.encode(this.huffmanBuf, src);
            dst.ensureCapacity(this.huffmanBuf.length() + 8);
            HPackEncoder.encodeInt(dst, 7, this.huffmanBuf.length(), 128);
            dst.append(this.huffmanBuf.array(), 0, this.huffmanBuf.length());
        } else {
            dst.ensureCapacity(strLen + 8);
            HPackEncoder.encodeInt(dst, 7, strLen, 0);
            dst.append(src);
        }
    }

    private void clearState() {
        if (this.tmpBuf != null) {
            this.tmpBuf.clear();
        }
        if (this.charsetEncoder != null) {
            this.charsetEncoder.reset();
        }
    }

    private void expandCapacity(int capacity) {
        ByteBuffer previous = this.tmpBuf;
        this.tmpBuf = ByteBuffer.allocate(capacity);
        previous.flip();
        this.tmpBuf.put(previous);
    }

    private void ensureCapacity(int extra) {
        int requiredCapacity;
        if (this.tmpBuf == null) {
            this.tmpBuf = ByteBuffer.allocate(Math.max(256, extra));
        }
        if ((requiredCapacity = this.tmpBuf.remaining() + extra) > this.tmpBuf.capacity()) {
            this.expandCapacity(requiredCapacity);
        }
    }

    int encodeString(ByteArrayBuffer dst, CharSequence charSequence, int off, int len, boolean huffman) throws CharacterCodingException {
        this.clearState();
        if (this.charsetEncoder == null) {
            if (huffman) {
                this.huffmanBuf.clear();
                this.huffmanBuf.ensureCapacity(len);
                Huffman.ENCODER.encode(this.huffmanBuf, charSequence, off, len);
                dst.ensureCapacity(this.huffmanBuf.length() + 8);
                HPackEncoder.encodeInt(dst, 7, this.huffmanBuf.length(), 128);
                dst.append(this.huffmanBuf.array(), 0, this.huffmanBuf.length());
            } else {
                dst.ensureCapacity(len + 8);
                HPackEncoder.encodeInt(dst, 7, len, 0);
                for (int i = 0; i < len; ++i) {
                    dst.append(charSequence.charAt(off + i));
                }
            }
            return len;
        }
        if (charSequence.length() > 0) {
            CoderResult result;
            CharBuffer in = CharBuffer.wrap(charSequence, off, len);
            while (in.hasRemaining()) {
                this.ensureCapacity((int)((float)in.remaining() * this.charsetEncoder.averageBytesPerChar()) + 8);
                result = this.charsetEncoder.encode(in, this.tmpBuf, true);
                if (!result.isError()) continue;
                result.throwException();
            }
            this.ensureCapacity(8);
            result = this.charsetEncoder.flush(this.tmpBuf);
            if (result.isError()) {
                result.throwException();
            }
        }
        this.tmpBuf.flip();
        int binaryLen = this.tmpBuf.remaining();
        this.encodeString(dst, this.tmpBuf, huffman);
        return binaryLen;
    }

    int encodeString(ByteArrayBuffer dst, String s, boolean huffman) throws CharacterCodingException {
        return this.encodeString(dst, s, 0, s.length(), huffman);
    }

    void encodeLiteralHeader(ByteArrayBuffer dst, HPackEntry existing, Header header, HPackRepresentation representation, boolean useHuffman) throws CharacterCodingException {
        this.encodeLiteralHeader(dst, existing, header.getName(), header.getValue(), header.isSensitive(), representation, useHuffman);
    }

    void encodeLiteralHeader(ByteArrayBuffer dst, HPackEntry existing, String key, String value, boolean sensitive, HPackRepresentation representation, boolean useHuffman) throws CharacterCodingException {
        int nameLen;
        int index;
        int n;
        int mask;
        switch (representation) {
            case WITH_INDEXING: {
                mask = 64;
                n = 6;
                break;
            }
            case WITHOUT_INDEXING: {
                mask = 0;
                n = 4;
                break;
            }
            case NEVER_INDEXED: {
                mask = 16;
                n = 4;
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + (Object)((Object)representation));
            }
        }
        int n2 = index = existing != null ? existing.getIndex() : 0;
        if (index <= 0) {
            HPackEncoder.encodeInt(dst, n, 0, mask);
            nameLen = this.encodeString(dst, key, useHuffman);
        } else {
            HPackEncoder.encodeInt(dst, n, index, mask);
            nameLen = existing.getHeader().getNameLen();
        }
        int valueLen = this.encodeString(dst, value != null ? value : "", useHuffman);
        if (representation == HPackRepresentation.WITH_INDEXING) {
            this.dynamicTable.add(new HPackHeader(key, nameLen, value, valueLen, sensitive));
        }
    }

    void encodeIndex(ByteArrayBuffer dst, int index) {
        HPackEncoder.encodeInt(dst, 7, index, 128);
    }

    private int findFullMatch(List<HPackEntry> entries, String value) {
        if (entries == null || entries.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < entries.size(); ++i) {
            HPackEntry entry = entries.get(i);
            if (!LangUtils.equals(value, entry.getHeader().getValue())) continue;
            return entry.getIndex();
        }
        return 0;
    }

    void encodeHeader(ByteArrayBuffer dst, Header header, boolean noIndexing, boolean useHuffman) throws CharacterCodingException {
        this.encodeHeader(dst, header.getName(), header.getValue(), header.isSensitive(), noIndexing, useHuffman);
    }

    void encodeHeader(ByteArrayBuffer dst, String name, String value, boolean sensitive, boolean noIndexing, boolean useHuffman) throws CharacterCodingException {
        List<HPackEntry> dynamicEntries;
        HPackRepresentation representation = sensitive ? HPackRepresentation.NEVER_INDEXED : (noIndexing ? HPackRepresentation.WITHOUT_INDEXING : HPackRepresentation.WITH_INDEXING);
        List<HPackEntry> staticEntries = StaticTable.INSTANCE.getByName(name);
        if (representation == HPackRepresentation.WITH_INDEXING) {
            int staticIndex = this.findFullMatch(staticEntries, value);
            if (staticIndex > 0) {
                this.encodeIndex(dst, staticIndex);
                return;
            }
            dynamicEntries = this.dynamicTable.getByName(name);
            int dynamicIndex = this.findFullMatch(dynamicEntries, value);
            if (dynamicIndex > 0) {
                this.encodeIndex(dst, dynamicIndex);
                return;
            }
        }
        HPackEntry existing = null;
        if (staticEntries != null && !staticEntries.isEmpty()) {
            existing = staticEntries.get(0);
        } else {
            dynamicEntries = this.dynamicTable.getByName(name);
            if (dynamicEntries != null && !dynamicEntries.isEmpty()) {
                existing = dynamicEntries.get(0);
            }
        }
        this.encodeLiteralHeader(dst, existing, name, value, sensitive, representation, useHuffman);
    }

    void encodeHeaders(ByteArrayBuffer dst, List<? extends Header> headers, boolean noIndexing, boolean useHuffman) throws CharacterCodingException {
        for (int i = 0; i < headers.size(); ++i) {
            this.encodeHeader(dst, headers.get(i), noIndexing, useHuffman);
        }
    }

    public void encodeHeader(ByteArrayBuffer dst, Header header) throws CharacterCodingException {
        Args.notNull(dst, "ByteArrayBuffer");
        Args.notNull(header, "Header");
        this.encodeHeader(dst, header.getName(), header.getValue(), header.isSensitive());
    }

    public void encodeHeader(ByteArrayBuffer dst, String name, String value, boolean sensitive) throws CharacterCodingException {
        Args.notNull(dst, "ByteArrayBuffer");
        Args.notEmpty(name, "Header name");
        this.encodeHeader(dst, name, value, sensitive, false, true);
    }

    public void encodeHeaders(ByteArrayBuffer dst, List<? extends Header> headers, boolean useHuffman) throws CharacterCodingException {
        Args.notNull(dst, "ByteArrayBuffer");
        Args.notEmpty(headers, "Header list");
        this.encodeHeaders(dst, headers, false, useHuffman);
    }

    public int getMaxTableSize() {
        return this.maxTableSize;
    }

    public void setMaxTableSize(int maxTableSize) {
        Args.notNegative(maxTableSize, "Max table size");
        this.maxTableSize = maxTableSize;
        this.dynamicTable.setMaxSize(maxTableSize);
    }
}

