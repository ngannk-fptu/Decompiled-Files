/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.parser;

import java.nio.charset.StandardCharsets;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParserMemory;
import net.minidev.json.parser.ParseException;
import net.minidev.json.writer.JsonReaderI;

class JSONParserByteArray
extends JSONParserMemory {
    private byte[] in;

    public JSONParserByteArray(int permissiveMode) {
        super(permissiveMode);
    }

    public Object parse(byte[] in) throws ParseException {
        return this.parse(in, JSONValue.defaultReader.DEFAULT);
    }

    public <T> T parse(byte[] in, JsonReaderI<T> mapper) throws ParseException {
        this.base = mapper.base;
        this.in = in;
        this.len = in.length;
        return this.parse(mapper);
    }

    @Override
    protected void extractString(int beginIndex, int endIndex) {
        this.xs = new String(this.in, beginIndex, endIndex - beginIndex, StandardCharsets.UTF_8);
    }

    @Override
    protected void extractStringTrim(int start, int stop) {
        byte[] val = this.in;
        while (start < stop && val[start] <= 32) {
            ++start;
        }
        while (start < stop && val[stop - 1] <= 32) {
            --stop;
        }
        this.xs = new String(this.in, start, stop - start, StandardCharsets.UTF_8);
    }

    @Override
    protected int indexOf(char c, int pos) {
        for (int i = pos; i < this.len; ++i) {
            if (this.in[i] != (byte)c) continue;
            return i;
        }
        return -1;
    }

    @Override
    protected void read() {
        this.c = ++this.pos >= this.len ? (char)26 : (char)this.in[this.pos];
    }

    @Override
    protected void readS() {
        this.c = ++this.pos >= this.len ? (char)26 : (char)this.in[this.pos];
    }

    @Override
    protected void readNoEnd() throws ParseException {
        if (++this.pos >= this.len) {
            this.c = (char)26;
            throw new ParseException(this.pos - 1, 3, "EOF");
        }
        this.c = (char)this.in[this.pos];
    }
}

