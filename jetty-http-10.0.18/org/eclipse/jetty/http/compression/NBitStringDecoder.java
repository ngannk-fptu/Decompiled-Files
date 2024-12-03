/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.CharsetStringBuilder$Iso88591StringBuilder
 */
package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.compression.EncodingException;
import org.eclipse.jetty.http.compression.HuffmanDecoder;
import org.eclipse.jetty.http.compression.NBitIntegerDecoder;
import org.eclipse.jetty.util.CharsetStringBuilder;

public class NBitStringDecoder {
    private final NBitIntegerDecoder _integerDecoder;
    private final HuffmanDecoder _huffmanBuilder;
    private final CharsetStringBuilder.Iso88591StringBuilder _builder;
    private boolean _huffman;
    private int _count;
    private int _length;
    private int _prefix;
    private State _state = State.PARSING;

    public NBitStringDecoder() {
        this._integerDecoder = new NBitIntegerDecoder();
        this._huffmanBuilder = new HuffmanDecoder();
        this._builder = new CharsetStringBuilder.Iso88591StringBuilder();
    }

    public void setPrefix(int prefix) {
        if (this._state != State.PARSING) {
            throw new IllegalStateException();
        }
        this._prefix = prefix;
    }

    public String decode(ByteBuffer buffer) throws EncodingException {
        block5: while (true) {
            switch (this._state) {
                case PARSING: {
                    byte firstByte = buffer.get(buffer.position());
                    this._huffman = (128 >>> 8 - this._prefix & firstByte) != 0;
                    this._state = State.LENGTH;
                    this._integerDecoder.setPrefix(this._prefix - 1);
                    continue block5;
                }
                case LENGTH: {
                    this._length = this._integerDecoder.decodeInt(buffer);
                    if (this._length < 0) {
                        return null;
                    }
                    this._state = State.VALUE;
                    this._huffmanBuilder.setLength(this._length);
                    continue block5;
                }
                case VALUE: {
                    String value;
                    String string = value = this._huffman ? this._huffmanBuilder.decode(buffer) : this.stringDecode(buffer);
                    if (value != null) {
                        this.reset();
                    }
                    return value;
                }
            }
            break;
        }
        throw new IllegalStateException(this._state.name());
    }

    private String stringDecode(ByteBuffer buffer) {
        while (this._count < this._length) {
            if (!buffer.hasRemaining()) {
                return null;
            }
            this._builder.append(buffer.get());
            ++this._count;
        }
        return this._builder.build();
    }

    public void reset() {
        this._state = State.PARSING;
        this._integerDecoder.reset();
        this._huffmanBuilder.reset();
        this._builder.reset();
        this._prefix = 0;
        this._count = 0;
        this._length = 0;
        this._huffman = false;
    }

    private static enum State {
        PARSING,
        LENGTH,
        VALUE;

    }
}

