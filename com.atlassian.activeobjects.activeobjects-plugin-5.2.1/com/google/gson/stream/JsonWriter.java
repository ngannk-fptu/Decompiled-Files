/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.stream;

import com.google.gson.stream.JsonScope;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class JsonWriter
implements Closeable {
    private final Writer out;
    private final List<JsonScope> stack = new ArrayList<JsonScope>();
    private String indent;
    private String separator;
    private boolean lenient;
    private boolean htmlSafe;

    public JsonWriter(Writer out) {
        this.stack.add(JsonScope.EMPTY_DOCUMENT);
        this.separator = ":";
        if (out == null) {
            throw new NullPointerException("out == null");
        }
        this.out = out;
    }

    public void setIndent(String indent) {
        if (indent.length() == 0) {
            this.indent = null;
            this.separator = ":";
        } else {
            this.indent = indent;
            this.separator = ": ";
        }
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public void setHtmlSafe(boolean htmlSafe) {
        this.htmlSafe = htmlSafe;
    }

    public boolean isHtmlSafe() {
        return this.htmlSafe;
    }

    public JsonWriter beginArray() throws IOException {
        return this.open(JsonScope.EMPTY_ARRAY, "[");
    }

    public JsonWriter endArray() throws IOException {
        return this.close(JsonScope.EMPTY_ARRAY, JsonScope.NONEMPTY_ARRAY, "]");
    }

    public JsonWriter beginObject() throws IOException {
        return this.open(JsonScope.EMPTY_OBJECT, "{");
    }

    public JsonWriter endObject() throws IOException {
        return this.close(JsonScope.EMPTY_OBJECT, JsonScope.NONEMPTY_OBJECT, "}");
    }

    private JsonWriter open(JsonScope empty, String openBracket) throws IOException {
        this.beforeValue(true);
        this.stack.add(empty);
        this.out.write(openBracket);
        return this;
    }

    private JsonWriter close(JsonScope empty, JsonScope nonempty, String closeBracket) throws IOException {
        JsonScope context = this.peek();
        if (context != nonempty && context != empty) {
            throw new IllegalStateException("Nesting problem: " + this.stack);
        }
        this.stack.remove(this.stack.size() - 1);
        if (context == nonempty) {
            this.newline();
        }
        this.out.write(closeBracket);
        return this;
    }

    private JsonScope peek() {
        return this.stack.get(this.stack.size() - 1);
    }

    private void replaceTop(JsonScope topOfStack) {
        this.stack.set(this.stack.size() - 1, topOfStack);
    }

    public JsonWriter name(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        this.beforeName();
        this.string(name);
        return this;
    }

    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.beforeValue(false);
        this.string(value);
        return this;
    }

    public JsonWriter nullValue() throws IOException {
        this.beforeValue(false);
        this.out.write("null");
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        this.beforeValue(false);
        this.out.write(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        this.beforeValue(false);
        this.out.append(Double.toString(value));
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        this.beforeValue(false);
        this.out.write(Long.toString(value));
        return this;
    }

    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        String string = value.toString();
        if (!this.lenient && (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN"))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        this.beforeValue(false);
        this.out.append(string);
        return this;
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void close() throws IOException {
        this.out.close();
        if (this.peek() != JsonScope.NONEMPTY_DOCUMENT) {
            throw new IOException("Incomplete document");
        }
    }

    private void string(String value) throws IOException {
        this.out.write("\"");
        int length = value.length();
        block9: for (int i = 0; i < length; ++i) {
            char c = value.charAt(i);
            switch (c) {
                case '\"': 
                case '\\': {
                    this.out.write(92);
                    this.out.write(c);
                    continue block9;
                }
                case '\t': {
                    this.out.write("\\t");
                    continue block9;
                }
                case '\b': {
                    this.out.write("\\b");
                    continue block9;
                }
                case '\n': {
                    this.out.write("\\n");
                    continue block9;
                }
                case '\r': {
                    this.out.write("\\r");
                    continue block9;
                }
                case '\f': {
                    this.out.write("\\f");
                    continue block9;
                }
                case '&': 
                case '\'': 
                case '<': 
                case '=': 
                case '>': {
                    if (this.htmlSafe) {
                        this.out.write(String.format("\\u%04x", c));
                        continue block9;
                    }
                    this.out.write(c);
                    continue block9;
                }
                default: {
                    if (c <= '\u001f') {
                        this.out.write(String.format("\\u%04x", c));
                        continue block9;
                    }
                    this.out.write(c);
                }
            }
        }
        this.out.write("\"");
    }

    private void newline() throws IOException {
        if (this.indent == null) {
            return;
        }
        this.out.write("\n");
        for (int i = 1; i < this.stack.size(); ++i) {
            this.out.write(this.indent);
        }
    }

    private void beforeName() throws IOException {
        JsonScope context = this.peek();
        if (context == JsonScope.NONEMPTY_OBJECT) {
            this.out.write(44);
        } else if (context != JsonScope.EMPTY_OBJECT) {
            throw new IllegalStateException("Nesting problem: " + this.stack);
        }
        this.newline();
        this.replaceTop(JsonScope.DANGLING_NAME);
    }

    private void beforeValue(boolean root) throws IOException {
        switch (this.peek()) {
            case EMPTY_DOCUMENT: {
                if (!this.lenient && !root) {
                    throw new IllegalStateException("JSON must start with an array or an object.");
                }
                this.replaceTop(JsonScope.NONEMPTY_DOCUMENT);
                break;
            }
            case EMPTY_ARRAY: {
                this.replaceTop(JsonScope.NONEMPTY_ARRAY);
                this.newline();
                break;
            }
            case NONEMPTY_ARRAY: {
                this.out.append(',');
                this.newline();
                break;
            }
            case DANGLING_NAME: {
                this.out.append(this.separator);
                this.replaceTop(JsonScope.NONEMPTY_OBJECT);
                break;
            }
            case NONEMPTY_DOCUMENT: {
                throw new IllegalStateException("JSON must have only one top-level value.");
            }
            default: {
                throw new IllegalStateException("Nesting problem: " + this.stack);
            }
        }
    }
}

