/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import net.minidev.json.parser.JSONParserReader;
import net.minidev.json.parser.ParseException;
import net.minidev.json.writer.JsonReaderI;

class JSONParserInputStream
extends JSONParserReader {
    public JSONParserInputStream(int permissiveMode) {
        super(permissiveMode);
    }

    public Object parse(InputStream in) throws ParseException, UnsupportedEncodingException {
        InputStreamReader i2 = new InputStreamReader(in, "utf8");
        return super.parse(i2);
    }

    public <T> T parse(InputStream in, JsonReaderI<T> mapper) throws ParseException, UnsupportedEncodingException {
        InputStreamReader i2 = new InputStreamReader(in, "utf8");
        return super.parse(i2, mapper);
    }
}

