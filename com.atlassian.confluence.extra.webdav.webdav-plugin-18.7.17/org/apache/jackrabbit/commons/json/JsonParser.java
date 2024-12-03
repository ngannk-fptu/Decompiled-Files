/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.json;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;
import org.apache.jackrabbit.commons.json.JsonHandler;

public class JsonParser {
    private static final String NULL = "null";
    private static final int EOF = -1;
    private static final int KEY_START = 1;
    private static final int VALUE_START = 2;
    private static final int VALUE = 4;
    private static final Integer OBJECT = new Integer(8);
    private static final Integer ARRAY = new Integer(32);
    private final JsonHandler handler;

    public JsonParser(JsonHandler jsonHandler) {
        this.handler = jsonHandler;
    }

    public void parse(String str) throws IOException {
        this.parse(new BufferedReader(new StringReader(str)));
    }

    public void parse(InputStream input, String charSetName) throws IOException {
        this.parse(new BufferedReader(new InputStreamReader(input, charSetName)));
    }

    public void parse(Reader reader) throws IOException {
        int state;
        StringBuffer value = new StringBuffer();
        Stack<Integer> complexVStack = new Stack<Integer>();
        int next = reader.read();
        if (next == 123) {
            this.handler.object();
            complexVStack.push(OBJECT);
            state = 1;
            next = JsonParser.readIgnoreWhitespace(reader);
        } else {
            throw new IOException("JSON object must start with a '{'");
        }
        while (next != -1) {
            switch (state) {
                case 1: {
                    if (next == 34) {
                        String key = JsonParser.nextString(reader, '\"');
                        next = JsonParser.readIgnoreWhitespace(reader);
                        if (next != 58) {
                            throw new IOException("Key-Value pairs must be separated by ':'");
                        }
                        this.handler.key(key);
                        state = 2;
                        next = JsonParser.readIgnoreWhitespace(reader);
                        break;
                    }
                    if (next == 125) {
                        state = 4;
                        break;
                    }
                    throw new IOException("Key must be in String format (double quotes)");
                }
                case 2: {
                    if (next == 91) {
                        this.handler.array();
                        complexVStack.push(ARRAY);
                        next = JsonParser.readIgnoreWhitespace(reader);
                        break;
                    }
                    if (next == 123) {
                        this.handler.object();
                        complexVStack.push(OBJECT);
                        state = 1;
                        next = JsonParser.readIgnoreWhitespace(reader);
                        break;
                    }
                    if (next == 34) {
                        this.handler.value(JsonParser.nextString(reader, '\"'));
                        next = JsonParser.readIgnoreWhitespace(reader);
                        if (next == 44 || next == 93 || next == 125) break;
                        throw new IOException("Invalid json format");
                    }
                    state = 4;
                    break;
                }
                case 4: {
                    if (next == 34) {
                        throw new IOException("Invalid json format");
                    }
                    if (next == 44) {
                        state = complexVStack.peek() == OBJECT ? 1 : 2;
                        value = this.resetValue(value);
                        next = JsonParser.readIgnoreWhitespace(reader);
                        break;
                    }
                    if (next == 93) {
                        if (complexVStack.pop() != ARRAY) {
                            throw new IOException("Invalid json format: Unexpected array termination.");
                        }
                        value = this.resetValue(value);
                        this.handler.endArray();
                        next = JsonParser.readIgnoreWhitespace(reader);
                        if (next == 44 || next == 125 || next == 93) break;
                        throw new IOException("Invalid json format");
                    }
                    if (next == 125) {
                        if (complexVStack.pop() != OBJECT) {
                            throw new IOException("Invalid json format: Unexpected object termination.");
                        }
                        value = this.resetValue(value);
                        this.handler.endObject();
                        next = JsonParser.readIgnoreWhitespace(reader);
                        if (next == 44 || next == 125 || next == 93 || next == -1) break;
                        throw new IOException("Invalid json format");
                    }
                    value.append((char)next);
                    next = reader.read();
                }
            }
        }
        if (value.length() != 0) {
            throw new IOException("Invalid json format");
        }
    }

    private static String nextString(Reader r, char quote) throws IOException {
        StringBuffer sb = new StringBuffer();
        block13: while (true) {
            int c = r.read();
            switch (c) {
                case -1: 
                case 10: 
                case 13: {
                    throw new IOException("Unterminated string");
                }
                case 92: {
                    c = r.read();
                    switch (c) {
                        case 98: {
                            sb.append('\b');
                            continue block13;
                        }
                        case 116: {
                            sb.append('\t');
                            continue block13;
                        }
                        case 110: {
                            sb.append('\n');
                            continue block13;
                        }
                        case 102: {
                            sb.append('\f');
                            continue block13;
                        }
                        case 114: {
                            sb.append('\r');
                            continue block13;
                        }
                        case 117: {
                            sb.append((char)Integer.parseInt(JsonParser.next(r, 4), 16));
                            continue block13;
                        }
                        case 120: {
                            sb.append((char)Integer.parseInt(JsonParser.next(r, 2), 16));
                            continue block13;
                        }
                    }
                    sb.append((char)c);
                    continue block13;
                }
            }
            if (c == quote) {
                return sb.toString();
            }
            sb.append((char)c);
        }
    }

    private static String next(Reader r, int n) throws IOException {
        StringBuffer b = new StringBuffer(n);
        while (n-- > 0) {
            int c = r.read();
            if (c < 0) {
                throw new EOFException();
            }
            b.append((char)c);
        }
        return b.toString();
    }

    private static int readIgnoreWhitespace(Reader reader) throws IOException {
        int next;
        while ((next = reader.read()) == 32 || next == 10 || next == 13 || next == 9) {
        }
        return next;
    }

    private StringBuffer resetValue(StringBuffer value) throws IOException {
        if (value != null && value.length() > 0) {
            String v = value.toString();
            if (NULL.equals(v)) {
                this.handler.value(null);
            } else if (v.equalsIgnoreCase("true")) {
                this.handler.value(true);
            } else if (v.equalsIgnoreCase("false")) {
                this.handler.value(false);
            } else if (v.indexOf(46) > -1) {
                double d = Double.parseDouble(v);
                this.handler.value(d);
            } else {
                long l = Long.parseLong(v);
                this.handler.value(l);
            }
        }
        return new StringBuffer();
    }
}

