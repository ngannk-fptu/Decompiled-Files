/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.parser;

import java.io.IOException;
import java.io.Reader;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParserStream;
import net.minidev.json.parser.ParseException;
import net.minidev.json.writer.JsonReaderI;

class JSONParserReader
extends JSONParserStream {
    private Reader in;

    public JSONParserReader(int permissiveMode) {
        super(permissiveMode);
    }

    public Object parse(Reader in) throws ParseException {
        return this.parse(in, JSONValue.defaultReader.DEFAULT);
    }

    public <T> T parse(Reader in, JsonReaderI<T> mapper) throws ParseException {
        this.base = mapper.base;
        this.in = in;
        return super.parse(mapper);
    }

    @Override
    protected void read() throws IOException {
        int i = this.in.read();
        this.c = (char)(i == -1 ? 26 : (char)i);
        ++this.pos;
    }

    @Override
    protected void readS() throws IOException {
        this.sb.append(this.c);
        int i = this.in.read();
        if (i == -1) {
            this.c = (char)26;
        } else {
            this.c = (char)i;
            ++this.pos;
        }
    }

    @Override
    protected void readNoEnd() throws ParseException, IOException {
        int i = this.in.read();
        if (i == -1) {
            throw new ParseException(this.pos - 1, 3, "EOF");
        }
        this.c = (char)i;
    }
}

