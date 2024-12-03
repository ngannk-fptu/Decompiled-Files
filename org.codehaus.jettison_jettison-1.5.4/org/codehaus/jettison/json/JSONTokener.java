/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.json;

import java.math.BigDecimal;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JSONTokener {
    private static final String USE_BIGDECIMAL_JSONTOKENER_KEY = "jettison.json.jsontokener.use_bigdecimal";
    public static final boolean USE_BIGDECIMAL_JSONTOKENER = Boolean.getBoolean("jettison.json.jsontokener.use_bigdecimal");
    protected boolean useBigDecimal = USE_BIGDECIMAL_JSONTOKENER;
    private int myIndex = 0;
    private String mySource;
    private int threshold = -1;
    private int recursionDepth;

    public JSONTokener(String s) {
        this.mySource = s.trim();
    }

    public JSONTokener(String s, int threshold) {
        this.mySource = s.trim();
        this.threshold = threshold;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void back() {
        if (this.myIndex > 0) {
            --this.myIndex;
        }
    }

    public static int dehexchar(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 55;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 87;
        }
        return -1;
    }

    public boolean more() {
        return this.myIndex < this.mySource.length();
    }

    public char next() {
        if (this.more()) {
            char c = this.mySource.charAt(this.myIndex);
            ++this.myIndex;
            return c;
        }
        return '\u0000';
    }

    public char next(char c) throws JSONException {
        char n = this.next();
        if (n != c) {
            throw this.syntaxError("Expected '" + c + "' and instead saw '" + n + "'.");
        }
        return n;
    }

    public String next(int n) throws JSONException {
        int i = this.myIndex;
        int j = i + n;
        if (j >= this.mySource.length()) {
            throw this.syntaxError("Substring bounds error");
        }
        this.myIndex += n;
        return this.mySource.substring(i, j);
    }

    public char nextClean() throws JSONException {
        char c;
        block4: while (true) {
            if ((c = this.next()) == '/') {
                switch (this.next()) {
                    case '/': {
                        while ((c = this.next()) != '\n' && c != '\r' && c != '\u0000') {
                        }
                        continue block4;
                    }
                    case '*': {
                        do {
                            if ((c = this.next()) != '\u0000') continue;
                            throw this.syntaxError("Unclosed comment.");
                        } while (c != '*' || this.next() != '/');
                        continue block4;
                    }
                    default: {
                        if (!this.more()) {
                            throw this.syntaxError("The JSON text is malformed");
                        }
                        this.back();
                        return '/';
                    }
                }
            }
            if (c == '#') {
                while ((c = this.next()) != '\n' && c != '\r' && c != '\u0000') {
                }
                continue;
            }
            if (c == '\u0000' || c > ' ') break;
        }
        return c;
    }

    public String nextString(char quote) throws JSONException {
        StringBuilder sb = new StringBuilder();
        block13: while (true) {
            char c = this.next();
            switch (c) {
                case '\u0000': 
                case '\n': 
                case '\r': {
                    throw this.syntaxError("Unterminated string");
                }
                case '\\': {
                    c = this.next();
                    switch (c) {
                        case 'b': {
                            sb.append('\b');
                            continue block13;
                        }
                        case 't': {
                            sb.append('\t');
                            continue block13;
                        }
                        case 'n': {
                            sb.append('\n');
                            continue block13;
                        }
                        case 'f': {
                            sb.append('\f');
                            continue block13;
                        }
                        case 'r': {
                            sb.append('\r');
                            continue block13;
                        }
                        case 'u': {
                            sb.append((char)Integer.parseInt(this.next(4), 16));
                            continue block13;
                        }
                        case 'x': {
                            sb.append((char)Integer.parseInt(this.next(2), 16));
                            continue block13;
                        }
                    }
                    sb.append(c);
                    continue block13;
                }
            }
            if (c == quote) {
                return sb.toString();
            }
            sb.append(c);
        }
    }

    public String nextTo(char d) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c;
            if ((c = this.next()) == d || c == '\u0000' || c == '\n' || c == '\r') {
                if (c != '\u0000') {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }

    public String nextTo(String delimiters) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c;
            if (delimiters.indexOf(c = this.next()) >= 0 || c == '\u0000' || c == '\n' || c == '\r') {
                if (c != '\u0000') {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }

    public Object nextValue() throws JSONException {
        char c = this.nextClean();
        switch (c) {
            case '\"': 
            case '\'': {
                return this.nextString(c);
            }
            case '{': {
                this.back();
                return this.newJSONObject();
            }
            case '[': {
                this.back();
                return this.newJSONArray();
            }
        }
        StringBuilder sb = new StringBuilder();
        char b = c;
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = this.next();
        }
        this.back();
        String s = sb.toString().trim();
        if (s.equals("")) {
            throw this.syntaxError("Missing value.");
        }
        if (s.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (s.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("null")) {
            return JSONObject.EXPLICIT_NULL;
        }
        if (b >= '0' && b <= '9' || b == '.' || b == '-' || b == '+') {
            if (b == '0') {
                if (s.length() > 2 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                    try {
                        return Integer.parseInt(s.substring(2), 16);
                    }
                    catch (Exception exception) {
                    }
                } else {
                    try {
                        return Integer.parseInt(s, 8);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            try {
                return Integer.valueOf(s);
            }
            catch (Exception e) {
                try {
                    return Long.valueOf(s);
                }
                catch (Exception f) {
                    try {
                        if (this.useBigDecimal) {
                            return new BigDecimal(s);
                        }
                        return new Double(s);
                    }
                    catch (Exception g) {
                        return s;
                    }
                }
            }
        }
        return s;
    }

    protected JSONObject newJSONObject() throws JSONException {
        this.checkRecursionDepth();
        JSONObject object = new JSONObject(this);
        --this.recursionDepth;
        return object;
    }

    protected JSONArray newJSONArray() throws JSONException {
        this.checkRecursionDepth();
        JSONArray array = new JSONArray(this);
        --this.recursionDepth;
        return array;
    }

    private void checkRecursionDepth() throws JSONException {
        ++this.recursionDepth;
        if (this.recursionDepth > JSONObject.RECURSION_DEPTH_LIMIT) {
            throw new JSONException("JSONTokener has reached recursion depth limit of " + JSONObject.RECURSION_DEPTH_LIMIT);
        }
    }

    public char skipTo(char to) {
        char c;
        int index = this.myIndex;
        do {
            if ((c = this.next()) != '\u0000') continue;
            this.myIndex = index;
            return c;
        } while (c != to);
        this.back();
        return c;
    }

    public void skipPast(String to) {
        this.myIndex = this.mySource.indexOf(to, this.myIndex);
        this.myIndex = this.myIndex < 0 ? this.mySource.length() : (this.myIndex += to.length());
    }

    public JSONException syntaxError(String message) {
        return new JSONException(message + this.toString(), 0, this.myIndex);
    }

    public String toString() {
        return " at character " + this.myIndex + " of " + this.mySource;
    }
}

