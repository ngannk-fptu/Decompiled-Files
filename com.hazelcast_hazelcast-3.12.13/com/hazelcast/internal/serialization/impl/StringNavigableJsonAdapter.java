/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.com.fasterxml.jackson.core.JsonFactory;
import com.hazelcast.com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.internal.json.JsonReducedValueParser;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.serialization.impl.NavigableJsonInputAdapter;
import com.hazelcast.query.impl.getters.JsonPathCursor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.StringReader;

public class StringNavigableJsonAdapter
extends NavigableJsonInputAdapter {
    private final int initialOffset;
    private String source;
    private int pos;

    public StringNavigableJsonAdapter(String source, int initialOffset) {
        this.initialOffset = initialOffset;
        this.source = source;
        this.pos = initialOffset;
    }

    @Override
    public void position(int position) {
        this.pos = this.initialOffset + position;
    }

    @Override
    public int position() {
        return this.pos - this.initialOffset;
    }

    @Override
    public void reset() {
        this.pos = this.initialOffset;
    }

    @Override
    public boolean isAttributeName(JsonPathCursor cursor) {
        if (this.source.length() < this.pos + cursor.getCurrent().length() + 2) {
            return false;
        }
        if (this.source.charAt(this.pos++) != '\"') {
            return false;
        }
        for (int i = 0; i < cursor.getCurrent().length() && this.pos < this.source.length(); ++i) {
            if (cursor.getCurrent().charAt(i) == this.source.charAt(this.pos++)) continue;
            return false;
        }
        return this.source.charAt(this.pos++) == '\"';
    }

    @Override
    @SuppressFBWarnings(value={"SR_NOT_CHECKED"})
    public JsonValue parseValue(JsonReducedValueParser parser, int offset) throws IOException {
        StringReader reader = new StringReader(this.source);
        if (reader.skip(this.initialOffset + offset) != (long)(this.initialOffset + offset)) {
            throw new IOException("There are not enough characters in this string");
        }
        return parser.parse(reader);
    }

    @Override
    public JsonParser createParser(JsonFactory factory) throws IOException {
        return factory.createParser(new StringReader(this.source));
    }
}

