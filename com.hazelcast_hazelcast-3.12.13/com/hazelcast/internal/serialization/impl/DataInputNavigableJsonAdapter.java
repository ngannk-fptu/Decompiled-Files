/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.com.fasterxml.jackson.core.JsonFactory;
import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.internal.json.JsonReducedValueParser;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.serialization.impl.NavigableJsonInputAdapter;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.query.impl.getters.JsonPathCursor;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class DataInputNavigableJsonAdapter
extends NavigableJsonInputAdapter {
    private final int initialOffset;
    private BufferObjectDataInput input;

    public DataInputNavigableJsonAdapter(BufferObjectDataInput input, int initialOffset) {
        this.input = input;
        this.input.position(initialOffset);
        this.initialOffset = initialOffset;
    }

    @Override
    public void position(int position) {
        this.input.position(position + this.initialOffset);
    }

    @Override
    public int position() {
        return this.input.position() - this.initialOffset;
    }

    @Override
    public void reset() {
        this.input.position(this.initialOffset);
    }

    @Override
    public boolean isAttributeName(JsonPathCursor cursor) {
        try {
            byte[] nameBytes = cursor.getCurrentAsUTF8();
            if (!this.isQuote()) {
                return false;
            }
            for (int i = 0; i < nameBytes.length; ++i) {
                if (nameBytes[i] == this.input.readByte()) continue;
                return false;
            }
            return this.isQuote();
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public JsonValue parseValue(JsonReducedValueParser parser, int offset) throws IOException {
        this.input.position(offset + this.initialOffset);
        return parser.parse(new UTF8Reader(this.input));
    }

    @Override
    public JsonParser createParser(JsonFactory factory) throws IOException {
        return factory.createParser(SerializationUtil.convertToInputStream(this.input, this.initialOffset));
    }

    private boolean isQuote() throws IOException {
        return this.input.readByte() == 34;
    }

    private static class UTF8Reader
    extends Reader {
        private final DataInput input;

        UTF8Reader(DataInput input) {
            this.input = input;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int i;
            block4: {
                if (off < 0 || off + len > cbuf.length) {
                    throw new IndexOutOfBoundsException();
                }
                i = 0;
                try {
                    for (i = 0; i < len; ++i) {
                        char c;
                        byte firstByte = this.input.readByte();
                        cbuf[off + i] = c = Bits.readUtf8Char(this.input, firstByte);
                    }
                }
                catch (EOFException e) {
                    if (i != 0) break block4;
                    return -1;
                }
            }
            return i;
        }

        @Override
        public void close() throws IOException {
        }
    }
}

