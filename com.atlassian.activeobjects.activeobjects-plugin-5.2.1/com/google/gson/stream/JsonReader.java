/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.stream;

import com.google.gson.stream.JsonScope;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class JsonReader
implements Closeable {
    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
    private final Reader in;
    private boolean lenient = false;
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;
    private final List<JsonScope> stack = new ArrayList<JsonScope>();
    private boolean hasToken;
    private JsonToken token;
    private String name;
    private String value;
    private boolean skipping;

    public JsonReader(Reader in) {
        this.push(JsonScope.EMPTY_DOCUMENT);
        this.skipping = false;
        if (in == null) {
            throw new NullPointerException("in == null");
        }
        this.in = in;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        this.expect(JsonToken.BEGIN_ARRAY);
    }

    public void endArray() throws IOException {
        this.expect(JsonToken.END_ARRAY);
    }

    public void beginObject() throws IOException {
        this.expect(JsonToken.BEGIN_OBJECT);
    }

    public void endObject() throws IOException {
        this.expect(JsonToken.END_OBJECT);
    }

    private void expect(JsonToken expected) throws IOException {
        this.quickPeek();
        if (this.token != expected) {
            throw new IllegalStateException("Expected " + (Object)((Object)expected) + " but was " + (Object)((Object)this.peek()));
        }
        this.advance();
    }

    public boolean hasNext() throws IOException {
        this.quickPeek();
        return this.token != JsonToken.END_OBJECT && this.token != JsonToken.END_ARRAY;
    }

    public JsonToken peek() throws IOException {
        this.quickPeek();
        if (this.token == null) {
            this.decodeLiteral();
        }
        return this.token;
    }

    private JsonToken quickPeek() throws IOException {
        if (this.hasToken) {
            return this.token;
        }
        switch (this.peekStack()) {
            case EMPTY_DOCUMENT: {
                if (this.lenient) {
                    this.consumeNonExecutePrefix();
                }
                this.replaceTop(JsonScope.NONEMPTY_DOCUMENT);
                JsonToken firstToken = this.nextValue();
                if (!this.lenient && firstToken != JsonToken.BEGIN_ARRAY && firstToken != JsonToken.BEGIN_OBJECT) {
                    this.syntaxError("Expected JSON document to start with '[' or '{'");
                }
                return firstToken;
            }
            case EMPTY_ARRAY: {
                return this.nextInArray(true);
            }
            case NONEMPTY_ARRAY: {
                return this.nextInArray(false);
            }
            case EMPTY_OBJECT: {
                return this.nextInObject(true);
            }
            case DANGLING_NAME: {
                return this.objectValue();
            }
            case NONEMPTY_OBJECT: {
                return this.nextInObject(false);
            }
            case NONEMPTY_DOCUMENT: {
                try {
                    JsonToken token = this.nextValue();
                    if (this.lenient) {
                        return token;
                    }
                    throw this.syntaxError("Expected EOF");
                }
                catch (EOFException e) {
                    this.hasToken = true;
                    this.token = JsonToken.END_DOCUMENT;
                    return this.token;
                }
            }
            case CLOSED: {
                throw new IllegalStateException("JsonReader is closed");
            }
        }
        throw new AssertionError();
    }

    private void consumeNonExecutePrefix() throws IOException {
        this.nextNonWhitespace();
        --this.pos;
        if (this.pos + NON_EXECUTE_PREFIX.length > this.limit && !this.fillBuffer(NON_EXECUTE_PREFIX.length)) {
            return;
        }
        for (int i = 0; i < NON_EXECUTE_PREFIX.length; ++i) {
            if (this.buffer[this.pos + i] == NON_EXECUTE_PREFIX[i]) continue;
            return;
        }
        this.pos += NON_EXECUTE_PREFIX.length;
    }

    private JsonToken advance() throws IOException {
        this.quickPeek();
        JsonToken result = this.token;
        this.hasToken = false;
        this.token = null;
        this.value = null;
        this.name = null;
        return result;
    }

    public String nextName() throws IOException {
        this.quickPeek();
        if (this.token != JsonToken.NAME) {
            throw new IllegalStateException("Expected a name but was " + (Object)((Object)this.peek()));
        }
        String result = this.name;
        this.advance();
        return result;
    }

    public String nextString() throws IOException {
        this.peek();
        if (this.value == null || this.token != JsonToken.STRING && this.token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected a string but was " + (Object)((Object)this.peek()));
        }
        String result = this.value;
        this.advance();
        return result;
    }

    public boolean nextBoolean() throws IOException {
        boolean result;
        this.quickPeek();
        if (this.value == null || this.token == JsonToken.STRING) {
            throw new IllegalStateException("Expected a boolean but was " + (Object)((Object)this.peek()));
        }
        if (this.value.equalsIgnoreCase("true")) {
            result = true;
        } else if (this.value.equalsIgnoreCase("false")) {
            result = false;
        } else {
            throw new IllegalStateException("Not a boolean: " + this.value);
        }
        this.advance();
        return result;
    }

    public void nextNull() throws IOException {
        this.quickPeek();
        if (this.value == null || this.token == JsonToken.STRING) {
            throw new IllegalStateException("Expected null but was " + (Object)((Object)this.peek()));
        }
        if (!this.value.equalsIgnoreCase("null")) {
            throw new IllegalStateException("Not a null: " + this.value);
        }
        this.advance();
    }

    public double nextDouble() throws IOException {
        this.quickPeek();
        if (this.value == null) {
            throw new IllegalStateException("Expected a double but was " + (Object)((Object)this.peek()));
        }
        double result = Double.parseDouble(this.value);
        if (result >= 1.0 && this.value.startsWith("0")) {
            throw new NumberFormatException("JSON forbids octal prefixes: " + this.value);
        }
        if (!this.lenient && (Double.isNaN(result) || Double.isInfinite(result))) {
            throw new NumberFormatException("JSON forbids NaN and infinities: " + this.value);
        }
        this.advance();
        return result;
    }

    public long nextLong() throws IOException {
        long result;
        block4: {
            this.quickPeek();
            if (this.value == null) {
                throw new IllegalStateException("Expected a long but was " + (Object)((Object)this.peek()));
            }
            try {
                result = Long.parseLong(this.value);
            }
            catch (NumberFormatException ignored) {
                double asDouble = Double.parseDouble(this.value);
                result = (long)asDouble;
                if ((double)result == asDouble) break block4;
                throw new NumberFormatException(this.value);
            }
        }
        if (result >= 1L && this.value.startsWith("0")) {
            throw new NumberFormatException("JSON forbids octal prefixes: " + this.value);
        }
        this.advance();
        return result;
    }

    public int nextInt() throws IOException {
        int result;
        block4: {
            this.quickPeek();
            if (this.value == null) {
                throw new IllegalStateException("Expected an int but was " + (Object)((Object)this.peek()));
            }
            try {
                result = Integer.parseInt(this.value);
            }
            catch (NumberFormatException ignored) {
                double asDouble = Double.parseDouble(this.value);
                result = (int)asDouble;
                if ((double)result == asDouble) break block4;
                throw new NumberFormatException(this.value);
            }
        }
        if ((long)result >= 1L && this.value.startsWith("0")) {
            throw new NumberFormatException("JSON forbids octal prefixes: " + this.value);
        }
        this.advance();
        return result;
    }

    public void close() throws IOException {
        this.hasToken = false;
        this.value = null;
        this.token = null;
        this.stack.clear();
        this.stack.add(JsonScope.CLOSED);
        this.in.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void skipValue() throws IOException {
        this.skipping = true;
        try {
            int count = 0;
            do {
                JsonToken token;
                if ((token = this.advance()) == JsonToken.BEGIN_ARRAY || token == JsonToken.BEGIN_OBJECT) {
                    ++count;
                    continue;
                }
                if (token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT) continue;
                --count;
            } while (count != 0);
        }
        finally {
            this.skipping = false;
        }
    }

    private JsonScope peekStack() {
        return this.stack.get(this.stack.size() - 1);
    }

    private JsonScope pop() {
        return this.stack.remove(this.stack.size() - 1);
    }

    private void push(JsonScope newTop) {
        this.stack.add(newTop);
    }

    private void replaceTop(JsonScope newTop) {
        this.stack.set(this.stack.size() - 1, newTop);
    }

    private JsonToken nextInArray(boolean firstElement) throws IOException {
        if (firstElement) {
            this.replaceTop(JsonScope.NONEMPTY_ARRAY);
        } else {
            switch (this.nextNonWhitespace()) {
                case 93: {
                    this.pop();
                    this.hasToken = true;
                    this.token = JsonToken.END_ARRAY;
                    return this.token;
                }
                case 59: {
                    this.checkLenient();
                }
                case 44: {
                    break;
                }
                default: {
                    throw this.syntaxError("Unterminated array");
                }
            }
        }
        switch (this.nextNonWhitespace()) {
            case 93: {
                if (firstElement) {
                    this.pop();
                    this.hasToken = true;
                    this.token = JsonToken.END_ARRAY;
                    return this.token;
                }
            }
            case 44: 
            case 59: {
                this.checkLenient();
                --this.pos;
                this.hasToken = true;
                this.value = "null";
                this.token = JsonToken.NULL;
                return this.token;
            }
        }
        --this.pos;
        return this.nextValue();
    }

    private JsonToken nextInObject(boolean firstElement) throws IOException {
        if (firstElement) {
            switch (this.nextNonWhitespace()) {
                case 125: {
                    this.pop();
                    this.hasToken = true;
                    this.token = JsonToken.END_OBJECT;
                    return this.token;
                }
            }
            --this.pos;
        } else {
            switch (this.nextNonWhitespace()) {
                case 125: {
                    this.pop();
                    this.hasToken = true;
                    this.token = JsonToken.END_OBJECT;
                    return this.token;
                }
                case 44: 
                case 59: {
                    break;
                }
                default: {
                    throw this.syntaxError("Unterminated object");
                }
            }
        }
        int quote = this.nextNonWhitespace();
        switch (quote) {
            case 39: {
                this.checkLenient();
            }
            case 34: {
                this.name = this.nextString((char)quote);
                break;
            }
            default: {
                this.checkLenient();
                --this.pos;
                this.name = this.nextLiteral();
                if (this.name.length() != 0) break;
                throw this.syntaxError("Expected name");
            }
        }
        this.replaceTop(JsonScope.DANGLING_NAME);
        this.hasToken = true;
        this.token = JsonToken.NAME;
        return this.token;
    }

    private JsonToken objectValue() throws IOException {
        switch (this.nextNonWhitespace()) {
            case 58: {
                break;
            }
            case 61: {
                this.checkLenient();
                if (this.pos >= this.limit && !this.fillBuffer(1) || this.buffer[this.pos] != '>') break;
                ++this.pos;
                break;
            }
            default: {
                throw this.syntaxError("Expected ':'");
            }
        }
        this.replaceTop(JsonScope.NONEMPTY_OBJECT);
        return this.nextValue();
    }

    private JsonToken nextValue() throws IOException {
        int c = this.nextNonWhitespace();
        switch (c) {
            case 123: {
                this.push(JsonScope.EMPTY_OBJECT);
                this.hasToken = true;
                this.token = JsonToken.BEGIN_OBJECT;
                return this.token;
            }
            case 91: {
                this.push(JsonScope.EMPTY_ARRAY);
                this.hasToken = true;
                this.token = JsonToken.BEGIN_ARRAY;
                return this.token;
            }
            case 39: {
                this.checkLenient();
            }
            case 34: {
                this.value = this.nextString((char)c);
                this.hasToken = true;
                this.token = JsonToken.STRING;
                return this.token;
            }
        }
        --this.pos;
        return this.readLiteral();
    }

    private boolean fillBuffer(int minimum) throws IOException {
        int total;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        while ((total = this.in.read(this.buffer, this.limit, this.buffer.length - this.limit)) != -1) {
            this.limit += total;
            if (this.limit < minimum) continue;
            return true;
        }
        return false;
    }

    private int nextNonWhitespace() throws IOException {
        block9: while (this.pos < this.limit || this.fillBuffer(1)) {
            char c = this.buffer[this.pos++];
            switch (c) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    continue block9;
                }
                case '/': {
                    if (this.pos == this.limit && !this.fillBuffer(1)) {
                        return c;
                    }
                    this.checkLenient();
                    char peek = this.buffer[this.pos];
                    switch (peek) {
                        case '*': {
                            ++this.pos;
                            if (!this.skipTo("*/")) {
                                throw this.syntaxError("Unterminated comment");
                            }
                            this.pos += 2;
                            continue block9;
                        }
                        case '/': {
                            ++this.pos;
                            this.skipToEndOfLine();
                            continue block9;
                        }
                    }
                    return c;
                }
                case '#': {
                    this.checkLenient();
                    this.skipToEndOfLine();
                    continue block9;
                }
            }
            return c;
        }
        throw new EOFException("End of input");
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw this.syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        char c;
        while ((this.pos < this.limit || this.fillBuffer(1)) && (c = this.buffer[this.pos++]) != '\r' && c != '\n') {
        }
    }

    private boolean skipTo(String toFind) throws IOException {
        while (this.pos + toFind.length() < this.limit || this.fillBuffer(toFind.length())) {
            block3: {
                for (int c = 0; c < toFind.length(); ++c) {
                    if (this.buffer[this.pos + c] == toFind.charAt(c)) {
                        continue;
                    }
                    break block3;
                }
                return true;
            }
            ++this.pos;
        }
        return false;
    }

    private String nextString(char quote) throws IOException {
        StringBuilder builder = null;
        do {
            int start = this.pos;
            while (this.pos < this.limit) {
                char c;
                if ((c = this.buffer[this.pos++]) == quote) {
                    if (this.skipping) {
                        return "skipped!";
                    }
                    if (builder == null) {
                        return new String(this.buffer, start, this.pos - start - 1);
                    }
                    builder.append(this.buffer, start, this.pos - start - 1);
                    return builder.toString();
                }
                if (c != '\\') continue;
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(this.buffer, start, this.pos - start - 1);
                builder.append(this.readEscapeCharacter());
                start = this.pos;
            }
            if (builder == null) {
                builder = new StringBuilder();
            }
            builder.append(this.buffer, start, this.pos - start);
        } while (this.fillBuffer(1));
        throw this.syntaxError("Unterminated string");
    }

    private String nextLiteral() throws IOException {
        StringBuilder builder = null;
        do {
            int start = this.pos;
            while (this.pos < this.limit) {
                char c = this.buffer[this.pos++];
                switch (c) {
                    case '#': 
                    case '/': 
                    case ';': 
                    case '=': 
                    case '\\': {
                        this.checkLenient();
                    }
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': 
                    case ',': 
                    case ':': 
                    case '[': 
                    case ']': 
                    case '{': 
                    case '}': {
                        --this.pos;
                        if (this.skipping) {
                            return "skipped!";
                        }
                        if (builder == null) {
                            return new String(this.buffer, start, this.pos - start);
                        }
                        builder.append(this.buffer, start, this.pos - start);
                        return builder.toString();
                    }
                }
            }
            if (builder == null) {
                builder = new StringBuilder();
            }
            builder.append(this.buffer, start, this.pos - start);
        } while (this.fillBuffer(1));
        return builder.toString();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " near " + this.getSnippet();
    }

    private char readEscapeCharacter() throws IOException {
        if (this.pos == this.limit && !this.fillBuffer(1)) {
            throw this.syntaxError("Unterminated escape sequence");
        }
        char escaped = this.buffer[this.pos++];
        switch (escaped) {
            case 'u': {
                if (this.pos + 4 > this.limit && !this.fillBuffer(4)) {
                    throw this.syntaxError("Unterminated escape sequence");
                }
                String hex = new String(this.buffer, this.pos, 4);
                this.pos += 4;
                return (char)Integer.parseInt(hex, 16);
            }
            case 't': {
                return '\t';
            }
            case 'b': {
                return '\b';
            }
            case 'n': {
                return '\n';
            }
            case 'r': {
                return '\r';
            }
            case 'f': {
                return '\f';
            }
        }
        return escaped;
    }

    private JsonToken readLiteral() throws IOException {
        String literal = this.nextLiteral();
        if (literal.length() == 0) {
            throw this.syntaxError("Expected literal value");
        }
        this.value = literal;
        this.hasToken = true;
        this.token = null;
        return null;
    }

    private void decodeLiteral() throws IOException {
        if (this.value.equalsIgnoreCase("null")) {
            this.token = JsonToken.NULL;
        } else if (this.value.equalsIgnoreCase("true") || this.value.equalsIgnoreCase("false")) {
            this.token = JsonToken.BOOLEAN;
        } else {
            try {
                Double.parseDouble(this.value);
                this.token = JsonToken.NUMBER;
            }
            catch (NumberFormatException ignored) {
                this.checkLenient();
                this.token = JsonToken.STRING;
            }
        }
    }

    private IOException syntaxError(String message) throws IOException {
        throw new MalformedJsonException(message + " near " + this.getSnippet());
    }

    private CharSequence getSnippet() {
        StringBuilder snippet = new StringBuilder();
        int beforePos = Math.min(this.pos, 20);
        snippet.append(this.buffer, this.pos - beforePos, beforePos);
        int afterPos = Math.min(this.limit - this.pos, 20);
        snippet.append(this.buffer, this.pos, afterPos);
        return snippet;
    }
}

