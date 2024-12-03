/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;
import org.glassfish.json.JsonMessages;
import org.glassfish.json.api.BufferPool;

class JsonGeneratorImpl
implements JsonGenerator {
    private static final char[] INT_MIN_VALUE_CHARS = "-2147483648".toCharArray();
    private static final int[] INT_CHARS_SIZE_TABLE = new int[]{9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    private static final char[] DIGIT_TENS = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
    private static final char[] DIGIT_ONES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final BufferPool bufferPool;
    private final Writer writer;
    private Context currentContext = new Context(Scope.IN_NONE);
    private final Deque<Context> stack = new ArrayDeque<Context>();
    private final char[] buf;
    private int len = 0;

    JsonGeneratorImpl(Writer writer, BufferPool bufferPool) {
        this.writer = writer;
        this.bufferPool = bufferPool;
        this.buf = bufferPool.take();
    }

    JsonGeneratorImpl(OutputStream out, BufferPool bufferPool) {
        this(out, StandardCharsets.UTF_8, bufferPool);
    }

    JsonGeneratorImpl(OutputStream out, Charset encoding, BufferPool bufferPool) {
        this(new OutputStreamWriter(out, encoding), bufferPool);
    }

    @Override
    public void flush() {
        this.flushBuffer();
        try {
            this.writer.flush();
        }
        catch (IOException ioe) {
            throw new JsonException(JsonMessages.GENERATOR_FLUSH_IO_ERR(), ioe);
        }
    }

    @Override
    public JsonGenerator writeStartObject() {
        if (this.currentContext.scope == Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        if (this.currentContext.scope == Scope.IN_NONE && !this.currentContext.first) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_MULTIPLE_TEXT());
        }
        this.writeComma();
        this.writeChar('{');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_OBJECT);
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(String name) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeChar('{');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_OBJECT);
        return this;
    }

    private JsonGenerator writeName(String name) {
        this.writeComma();
        this.writeEscapedString(name);
        this.writeColon();
        return this;
    }

    @Override
    public JsonGenerator write(String name, String fieldValue) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeEscapedString(fieldValue);
        return this;
    }

    @Override
    public JsonGenerator write(String name, int value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeInt(value);
        return this;
    }

    @Override
    public JsonGenerator write(String name, long value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeString(String.valueOf(value));
        return this;
    }

    @Override
    public JsonGenerator write(String name, double value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new NumberFormatException(JsonMessages.GENERATOR_DOUBLE_INFINITE_NAN());
        }
        this.writeName(name);
        this.writeString(String.valueOf(value));
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigInteger value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeString(String.valueOf(value));
        return this;
    }

    @Override
    public JsonGenerator write(String name, BigDecimal value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeString(String.valueOf(value));
        return this;
    }

    @Override
    public JsonGenerator write(String name, boolean value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeString(value ? "true" : "false");
        return this;
    }

    @Override
    public JsonGenerator writeNull(String name) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeString("null");
        return this;
    }

    @Override
    public JsonGenerator write(JsonValue value) {
        this.checkContextForValue();
        switch (value.getValueType()) {
            case ARRAY: {
                JsonArray array = (JsonArray)value;
                this.writeStartArray();
                for (JsonValue child : array) {
                    this.write(child);
                }
                this.writeEnd();
                break;
            }
            case OBJECT: {
                JsonObject object = (JsonObject)value;
                this.writeStartObject();
                for (Map.Entry member : object.entrySet()) {
                    this.write((String)member.getKey(), (JsonValue)member.getValue());
                }
                this.writeEnd();
                break;
            }
            case STRING: {
                JsonString str = (JsonString)value;
                this.write(str.getString());
                break;
            }
            case NUMBER: {
                JsonNumber number = (JsonNumber)value;
                this.writeValue(number.toString());
                this.popFieldContext();
                break;
            }
            case TRUE: {
                this.write(true);
                break;
            }
            case FALSE: {
                this.write(false);
                break;
            }
            case NULL: {
                this.writeNull();
            }
        }
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() {
        if (this.currentContext.scope == Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        if (this.currentContext.scope == Scope.IN_NONE && !this.currentContext.first) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_MULTIPLE_TEXT());
        }
        this.writeComma();
        this.writeChar('[');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_ARRAY);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(String name) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.writeChar('[');
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_ARRAY);
        return this;
    }

    @Override
    public JsonGenerator write(String name, JsonValue value) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        switch (value.getValueType()) {
            case ARRAY: {
                JsonArray array = (JsonArray)value;
                this.writeStartArray(name);
                for (JsonValue child : array) {
                    this.write(child);
                }
                this.writeEnd();
                break;
            }
            case OBJECT: {
                JsonObject object = (JsonObject)value;
                this.writeStartObject(name);
                for (Map.Entry member : object.entrySet()) {
                    this.write((String)member.getKey(), (JsonValue)member.getValue());
                }
                this.writeEnd();
                break;
            }
            case STRING: {
                JsonString str = (JsonString)value;
                this.write(name, str.getString());
                break;
            }
            case NUMBER: {
                JsonNumber number = (JsonNumber)value;
                this.writeValue(name, number.toString());
                break;
            }
            case TRUE: {
                this.write(name, true);
                break;
            }
            case FALSE: {
                this.write(name, false);
                break;
            }
            case NULL: {
                this.writeNull(name);
            }
        }
        return this;
    }

    @Override
    public JsonGenerator write(String value) {
        this.checkContextForValue();
        this.writeComma();
        this.writeEscapedString(value);
        this.popFieldContext();
        return this;
    }

    @Override
    public JsonGenerator write(int value) {
        this.checkContextForValue();
        this.writeComma();
        this.writeInt(value);
        this.popFieldContext();
        return this;
    }

    @Override
    public JsonGenerator write(long value) {
        this.checkContextForValue();
        this.writeValue(String.valueOf(value));
        this.popFieldContext();
        return this;
    }

    @Override
    public JsonGenerator write(double value) {
        this.checkContextForValue();
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new NumberFormatException(JsonMessages.GENERATOR_DOUBLE_INFINITE_NAN());
        }
        this.writeValue(String.valueOf(value));
        this.popFieldContext();
        return this;
    }

    @Override
    public JsonGenerator write(BigInteger value) {
        this.checkContextForValue();
        this.writeValue(value.toString());
        this.popFieldContext();
        return this;
    }

    private void checkContextForValue() {
        if (!this.currentContext.first && this.currentContext.scope != Scope.IN_ARRAY && this.currentContext.scope != Scope.IN_FIELD || this.currentContext.first && this.currentContext.scope == Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
    }

    @Override
    public JsonGenerator write(BigDecimal value) {
        this.checkContextForValue();
        this.writeValue(value.toString());
        this.popFieldContext();
        return this;
    }

    private void popFieldContext() {
        if (this.currentContext.scope == Scope.IN_FIELD) {
            this.currentContext = this.stack.pop();
        }
    }

    @Override
    public JsonGenerator write(boolean value) {
        this.checkContextForValue();
        this.writeComma();
        this.writeString(value ? "true" : "false");
        this.popFieldContext();
        return this;
    }

    @Override
    public JsonGenerator writeNull() {
        this.checkContextForValue();
        this.writeComma();
        this.writeString("null");
        this.popFieldContext();
        return this;
    }

    private void writeValue(String value) {
        this.writeComma();
        this.writeString(value);
    }

    private void writeValue(String name, String value) {
        this.writeComma();
        this.writeEscapedString(name);
        this.writeColon();
        this.writeString(value);
    }

    @Override
    public JsonGenerator writeKey(String name) {
        if (this.currentContext.scope != Scope.IN_OBJECT) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD((Object)this.currentContext.scope));
        }
        this.writeName(name);
        this.stack.push(this.currentContext);
        this.currentContext = new Context(Scope.IN_FIELD);
        this.currentContext.first = false;
        return this;
    }

    @Override
    public JsonGenerator writeEnd() {
        if (this.currentContext.scope == Scope.IN_NONE) {
            throw new JsonGenerationException("writeEnd() cannot be called in no context");
        }
        this.writeChar(this.currentContext.scope == Scope.IN_ARRAY ? (char)']' : '}');
        this.currentContext = this.stack.pop();
        this.popFieldContext();
        return this;
    }

    protected void writeComma() {
        if (this.isCommaAllowed()) {
            this.writeChar(',');
        }
        this.currentContext.first = false;
    }

    boolean isCommaAllowed() {
        return !this.currentContext.first && this.currentContext.scope != Scope.IN_FIELD;
    }

    protected void writeColon() {
        this.writeChar(':');
    }

    @Override
    public void close() {
        if (this.currentContext.scope != Scope.IN_NONE || this.currentContext.first) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_INCOMPLETE_JSON());
        }
        this.flushBuffer();
        try {
            this.writer.close();
        }
        catch (IOException ioe) {
            throw new JsonException(JsonMessages.GENERATOR_CLOSE_IO_ERR(), ioe);
        }
        this.bufferPool.recycle(this.buf);
    }

    void writeEscapedString(String string) {
        this.writeChar('\"');
        int len = string.length();
        block8: for (int i = 0; i < len; ++i) {
            int begin = i;
            int end = i;
            char c = string.charAt(i);
            while (c >= ' ' && c <= '\u10ffff' && c != '\"' && c != '\\') {
                end = ++i;
                if (i >= len) break;
                c = string.charAt(i);
            }
            if (begin < end) {
                this.writeString(string, begin, end);
                if (i == len) break;
            }
            switch (c) {
                case '\"': 
                case '\\': {
                    this.writeChar('\\');
                    this.writeChar(c);
                    continue block8;
                }
                case '\b': {
                    this.writeChar('\\');
                    this.writeChar('b');
                    continue block8;
                }
                case '\f': {
                    this.writeChar('\\');
                    this.writeChar('f');
                    continue block8;
                }
                case '\n': {
                    this.writeChar('\\');
                    this.writeChar('n');
                    continue block8;
                }
                case '\r': {
                    this.writeChar('\\');
                    this.writeChar('r');
                    continue block8;
                }
                case '\t': {
                    this.writeChar('\\');
                    this.writeChar('t');
                    continue block8;
                }
                default: {
                    String hex = "000" + Integer.toHexString(c);
                    this.writeString("\\u" + hex.substring(hex.length() - 4));
                }
            }
        }
        this.writeChar('\"');
    }

    void writeString(String str, int begin, int end) {
        while (begin < end) {
            int no = Math.min(this.buf.length - this.len, end - begin);
            str.getChars(begin, begin + no, this.buf, this.len);
            begin += no;
            this.len += no;
            if (this.len < this.buf.length) continue;
            this.flushBuffer();
        }
    }

    void writeString(String str) {
        this.writeString(str, 0, str.length());
    }

    void writeChar(char c) {
        if (this.len >= this.buf.length) {
            this.flushBuffer();
        }
        this.buf[this.len++] = c;
    }

    void writeInt(int num) {
        int size;
        if (num == Integer.MIN_VALUE) {
            size = INT_MIN_VALUE_CHARS.length;
        } else {
            int n = size = num < 0 ? JsonGeneratorImpl.stringSize(-num) + 1 : JsonGeneratorImpl.stringSize(num);
        }
        if (this.len + size >= this.buf.length) {
            this.flushBuffer();
        }
        if (num == Integer.MIN_VALUE) {
            System.arraycopy(INT_MIN_VALUE_CHARS, 0, this.buf, this.len, size);
        } else {
            JsonGeneratorImpl.fillIntChars(num, this.buf, this.len + size);
        }
        this.len += size;
    }

    void flushBuffer() {
        try {
            if (this.len > 0) {
                this.writer.write(this.buf, 0, this.len);
                this.len = 0;
            }
        }
        catch (IOException ioe) {
            throw new JsonException(JsonMessages.GENERATOR_WRITE_IO_ERR(), ioe);
        }
    }

    private static int stringSize(int x) {
        int i = 0;
        while (x > INT_CHARS_SIZE_TABLE[i]) {
            ++i;
        }
        return i + 1;
    }

    private static void fillIntChars(int i, char[] buf, int index) {
        int r;
        int q;
        int charPos = index;
        int sign = 0;
        if (i < 0) {
            sign = 45;
            i = -i;
        }
        while (i >= 65536) {
            q = i / 100;
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf[--charPos] = DIGIT_ONES[r];
            buf[--charPos] = DIGIT_TENS[r];
        }
        do {
            q = i * 52429 >>> 19;
            r = i - ((q << 3) + (q << 1));
            buf[--charPos] = DIGITS[r];
        } while ((i = q) != 0);
        if (sign != 0) {
            buf[--charPos] = sign;
        }
    }

    private static class Context {
        boolean first = true;
        final Scope scope;

        Context(Scope scope) {
            this.scope = scope;
        }
    }

    private static enum Scope {
        IN_NONE,
        IN_OBJECT,
        IN_FIELD,
        IN_ARRAY;

    }
}

