/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

public class JsonFormatException
extends RuntimeException {
    private final String text;
    private final int line;
    private final int column;

    public JsonFormatException(String text, int line, int column, String message) {
        super(message);
        this.text = text;
        this.line = line + 1;
        this.column = column + 1;
    }

    public String getErrorToken() {
        return this.text;
    }

    public int getErrorLine() {
        return this.line;
    }

    public int getErrorColumn() {
        return this.column;
    }

    @Override
    public String toString() {
        return "JsonFormatException{text=" + this.text + ", line=" + this.line + ", column=" + this.column + '}';
    }
}

