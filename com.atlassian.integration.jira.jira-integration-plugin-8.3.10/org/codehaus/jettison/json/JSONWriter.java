/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.json;

import java.io.IOException;
import java.io.Writer;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JSONWriter {
    private static final int maxdepth = 20;
    private boolean comma = false;
    protected char mode = (char)105;
    private char[] stack = new char[20];
    private int top = 0;
    protected Writer writer;

    public JSONWriter(Writer w) {
        this.writer = w;
    }

    private JSONWriter append(String s) throws JSONException {
        if (s == null) {
            throw new JSONException("Null pointer");
        }
        if (this.mode == 'o' || this.mode == 'a') {
            try {
                if (this.comma && this.mode == 'a') {
                    this.writer.write(44);
                }
                this.writer.write(s);
            }
            catch (IOException e) {
                throw new JSONException(e);
            }
            if (this.mode == 'o') {
                this.mode = (char)107;
            }
            this.comma = true;
            return this;
        }
        throw new JSONException("Value out of sequence.");
    }

    public JSONWriter array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            this.push('a');
            this.append("[");
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }

    private JSONWriter end(char m, char c) throws JSONException {
        if (this.mode != m) {
            throw new JSONException(m == 'o' ? "Misplaced endObject." : "Misplaced endArray.");
        }
        this.pop(m);
        try {
            this.writer.write(c);
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
        this.comma = true;
        return this;
    }

    public JSONWriter endArray() throws JSONException {
        return this.end('a', ']');
    }

    public JSONWriter endObject() throws JSONException {
        return this.end('k', '}');
    }

    public JSONWriter key(String s) throws JSONException {
        if (s == null) {
            throw new JSONException("Null key.");
        }
        if (this.mode == 'k') {
            try {
                if (this.comma) {
                    this.writer.write(44);
                }
                this.writer.write(JSONObject.quote(s));
                this.writer.write(58);
                this.comma = false;
                this.mode = (char)111;
                return this;
            }
            catch (IOException e) {
                throw new JSONException(e);
            }
        }
        throw new JSONException("Misplaced key.");
    }

    public JSONWriter object() throws JSONException {
        if (this.mode == 'i') {
            this.mode = (char)111;
        }
        if (this.mode == 'o' || this.mode == 'a') {
            this.append("{");
            this.push('k');
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }

    private void pop(char c) throws JSONException {
        if (this.top <= 0 || this.stack[this.top - 1] != c) {
            throw new JSONException("Nesting error.");
        }
        --this.top;
        this.mode = (char)(this.top == 0 ? 100 : this.stack[this.top - 1]);
    }

    private void push(char c) throws JSONException {
        if (this.top >= 20) {
            throw new JSONException("Nesting too deep.");
        }
        this.stack[this.top] = c;
        this.mode = c;
        ++this.top;
    }

    public JSONWriter value(boolean b) throws JSONException {
        return this.append(b ? "true" : "false");
    }

    public JSONWriter value(double d) throws JSONException {
        return this.value(new Double(d));
    }

    public JSONWriter value(long l) throws JSONException {
        return this.append(Long.toString(l));
    }

    public JSONWriter value(Object o) throws JSONException {
        return this.append(JSONObject.valueToString(o, true));
    }
}

