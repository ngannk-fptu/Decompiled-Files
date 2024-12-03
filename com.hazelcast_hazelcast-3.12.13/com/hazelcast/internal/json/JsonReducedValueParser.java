/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.json;

import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonNumber;
import com.hazelcast.internal.json.JsonString;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.json.Location;
import com.hazelcast.internal.json.ParseException;
import java.io.IOException;
import java.io.Reader;

public class JsonReducedValueParser {
    private static final int DEFAULT_BUFFER_SIZE = 20;
    private Reader reader;
    private char[] buffer;
    private int bufferOffset;
    private int index;
    private int fill;
    private int line;
    private int lineOffset;
    private int current;
    private StringBuilder captureBuffer;
    private int captureStart;

    public JsonValue parse(Reader reader) throws IOException {
        return this.parse(reader, 20);
    }

    public JsonValue parse(Reader reader, int buffersize) throws IOException {
        if (reader == null) {
            throw new NullPointerException("reader is null");
        }
        if (buffersize <= 0) {
            throw new IllegalArgumentException("buffersize is zero or negative");
        }
        this.reader = reader;
        this.buffer = new char[buffersize];
        this.bufferOffset = 0;
        this.index = 0;
        this.fill = 0;
        this.line = 1;
        this.lineOffset = 0;
        this.current = 0;
        this.captureStart = -1;
        this.read();
        return this.readValue();
    }

    private JsonValue readValue() throws IOException {
        switch (this.current) {
            case 110: {
                return this.readNull();
            }
            case 116: {
                return this.readTrue();
            }
            case 102: {
                return this.readFalse();
            }
            case 34: {
                return this.readString();
            }
            case 45: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                return this.readNumber();
            }
        }
        throw this.expected("value");
    }

    private JsonValue readNull() throws IOException {
        this.read();
        this.readRequiredChar('u');
        this.readRequiredChar('l');
        this.readRequiredChar('l');
        return Json.NULL;
    }

    private JsonValue readTrue() throws IOException {
        this.read();
        this.readRequiredChar('r');
        this.readRequiredChar('u');
        this.readRequiredChar('e');
        return Json.TRUE;
    }

    private JsonValue readFalse() throws IOException {
        this.read();
        this.readRequiredChar('a');
        this.readRequiredChar('l');
        this.readRequiredChar('s');
        this.readRequiredChar('e');
        return Json.FALSE;
    }

    private void readRequiredChar(char ch) throws IOException {
        if (!this.readChar(ch)) {
            throw this.expected("'" + ch + "'");
        }
    }

    private JsonString readString() throws IOException {
        return new JsonString(this.readStringInternal());
    }

    private String readStringInternal() throws IOException {
        this.read();
        this.startCapture();
        while (this.current != 34) {
            if (this.current == 92) {
                this.pauseCapture();
                this.readEscape();
                this.startCapture();
                continue;
            }
            if (this.current < 32) {
                throw this.expected("valid string character");
            }
            this.read();
        }
        String string = this.endCapture();
        this.read();
        return string;
    }

    private void readEscape() throws IOException {
        this.read();
        switch (this.current) {
            case 34: 
            case 47: 
            case 92: {
                this.captureBuffer.append((char)this.current);
                break;
            }
            case 98: {
                this.captureBuffer.append('\b');
                break;
            }
            case 102: {
                this.captureBuffer.append('\f');
                break;
            }
            case 110: {
                this.captureBuffer.append('\n');
                break;
            }
            case 114: {
                this.captureBuffer.append('\r');
                break;
            }
            case 116: {
                this.captureBuffer.append('\t');
                break;
            }
            case 117: {
                char[] hexChars = new char[4];
                for (int i = 0; i < 4; ++i) {
                    this.read();
                    if (!this.isHexDigit()) {
                        throw this.expected("hexadecimal digit");
                    }
                    hexChars[i] = (char)this.current;
                }
                this.captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                break;
            }
            default: {
                throw this.expected("valid escape sequence");
            }
        }
        this.read();
    }

    private JsonNumber readNumber() throws IOException {
        this.startCapture();
        this.readChar('-');
        int firstDigit = this.current;
        if (!this.readDigit()) {
            throw this.expected("digit");
        }
        if (firstDigit != 48) {
            while (this.readDigit()) {
            }
        }
        this.readFraction();
        this.readExponent();
        return new JsonNumber(this.endCapture());
    }

    private boolean readFraction() throws IOException {
        if (!this.readChar('.')) {
            return false;
        }
        if (!this.readDigit()) {
            throw this.expected("digit");
        }
        while (this.readDigit()) {
        }
        return true;
    }

    private boolean readExponent() throws IOException {
        if (!this.readChar('e') && !this.readChar('E')) {
            return false;
        }
        if (!this.readChar('+')) {
            this.readChar('-');
        }
        if (!this.readDigit()) {
            throw this.expected("digit");
        }
        while (this.readDigit()) {
        }
        return true;
    }

    private boolean readChar(char ch) throws IOException {
        if (this.current != ch) {
            return false;
        }
        this.read();
        return true;
    }

    private boolean readDigit() throws IOException {
        if (!this.isDigit()) {
            return false;
        }
        this.read();
        return true;
    }

    private void read() throws IOException {
        if (this.index == this.fill) {
            if (this.captureStart != -1) {
                this.captureBuffer.append(this.buffer, this.captureStart, this.fill - this.captureStart);
                this.captureStart = 0;
            }
            this.bufferOffset += this.fill;
            this.fill = this.reader.read(this.buffer, 0, this.buffer.length);
            this.index = 0;
            if (this.fill == -1) {
                this.current = -1;
                ++this.index;
                return;
            }
        }
        if (this.current == 10) {
            ++this.line;
            this.lineOffset = this.bufferOffset + this.index;
        }
        this.current = this.buffer[this.index++];
    }

    private void startCapture() {
        if (this.captureBuffer == null) {
            this.captureBuffer = new StringBuilder();
        }
        this.captureStart = this.index - 1;
    }

    private void pauseCapture() {
        int end = this.current == -1 ? this.index : this.index - 1;
        this.captureBuffer.append(this.buffer, this.captureStart, end - this.captureStart);
        this.captureStart = -1;
    }

    private String endCapture() {
        int start = this.captureStart;
        int end = this.index - 1;
        this.captureStart = -1;
        if (this.captureBuffer.length() > 0) {
            this.captureBuffer.append(this.buffer, start, end - start);
            String captured = this.captureBuffer.toString();
            this.captureBuffer.setLength(0);
            return captured;
        }
        return new String(this.buffer, start, end - start);
    }

    Location getLocation() {
        int offset = this.bufferOffset + this.index - 1;
        int column = offset - this.lineOffset + 1;
        return new Location(offset, this.line, column);
    }

    private ParseException expected(String expected) {
        if (this.isEndOfText()) {
            return this.error("Unexpected end of input");
        }
        return this.error("Expected " + expected);
    }

    private ParseException error(String message) {
        return new ParseException(message, this.getLocation());
    }

    private boolean isDigit() {
        return this.current >= 48 && this.current <= 57;
    }

    private boolean isHexDigit() {
        return this.current >= 48 && this.current <= 57 || this.current >= 97 && this.current <= 102 || this.current >= 65 && this.current <= 70;
    }

    private boolean isEndOfText() {
        return this.current == -1;
    }
}

