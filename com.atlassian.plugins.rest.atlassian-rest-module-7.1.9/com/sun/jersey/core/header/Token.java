/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;

public class Token {
    protected String token;

    protected Token() {
    }

    public Token(String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }

    public Token(HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.token = reader.nextToken();
        if (reader.hasNext()) {
            throw new ParseException("Invalid token", reader.getIndex());
        }
    }

    public String getToken() {
        return this.token;
    }

    public final boolean isCompatible(String token) {
        if (this.token.equals("*")) {
            return true;
        }
        return this.token.equals(token);
    }
}

