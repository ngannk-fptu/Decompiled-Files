/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.types;

import nonapi.io.github.classgraph.json.JSONUtils;
import nonapi.io.github.classgraph.types.ParseException;

public class Parser {
    private final String string;
    private int position;
    private final StringBuilder token = new StringBuilder();
    private Object state;
    private static final int SHOW_BEFORE = 80;
    private static final int SHOW_AFTER = 80;

    public Parser(String string) throws ParseException {
        if (string == null) {
            throw new ParseException(null, "Cannot parse null string");
        }
        this.string = string;
    }

    public String getPositionInfo() {
        int showStart = Math.max(0, this.position - 80);
        int showEnd = Math.min(this.string.length(), this.position + 80);
        return "before: \"" + JSONUtils.escapeJSONString(this.string.substring(showStart, this.position)) + "\"; after: \"" + JSONUtils.escapeJSONString(this.string.substring(this.position, showEnd)) + "\"; position: " + this.position + "; token: \"" + this.token + "\"";
    }

    public Object setState(Object state) {
        Object oldState = this.state;
        this.state = state;
        return oldState;
    }

    public Object getState() {
        return this.state;
    }

    public char getc() throws ParseException {
        if (this.position >= this.string.length()) {
            throw new ParseException(this, "Ran out of input while parsing");
        }
        return this.string.charAt(this.position++);
    }

    public void expect(char expectedChar) throws ParseException {
        char next = this.getc();
        if (next != expectedChar) {
            throw new ParseException(this, "Expected '" + expectedChar + "'; got '" + (char)next + "'");
        }
    }

    public char peek() {
        return this.position == this.string.length() ? (char)'\u0000' : this.string.charAt(this.position);
    }

    public void peekExpect(char expectedChar) throws ParseException {
        if (this.position == this.string.length()) {
            throw new ParseException(this, "Expected '" + expectedChar + "'; reached end of string");
        }
        char next = this.string.charAt(this.position);
        if (next != expectedChar) {
            throw new ParseException(this, "Expected '" + expectedChar + "'; got '" + next + "'");
        }
    }

    public boolean peekMatches(String strMatch) {
        return this.string.regionMatches(this.position, strMatch, 0, strMatch.length());
    }

    public void next() {
        ++this.position;
    }

    public void advance(int numChars) {
        if (this.position + numChars >= this.string.length()) {
            throw new IllegalArgumentException("Invalid skip distance");
        }
        this.position += numChars;
    }

    public boolean hasMore() {
        return this.position < this.string.length();
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        if (position < 0 || position >= this.string.length()) {
            throw new IllegalArgumentException("Invalid position");
        }
        this.position = position;
    }

    public CharSequence getSubsequence(int startPosition, int endPosition) {
        return this.string.subSequence(startPosition, endPosition);
    }

    public String getSubstring(int startPosition, int endPosition) {
        return this.string.substring(startPosition, endPosition);
    }

    public void appendToToken(String str) {
        this.token.append(str);
    }

    public void appendToToken(char c) {
        this.token.append(c);
    }

    public void skipWhitespace() {
        char c;
        while (this.position < this.string.length() && ((c = this.string.charAt(this.position)) == ' ' || c == '\n' || c == '\r' || c == '\t')) {
            ++this.position;
        }
    }

    public String currToken() {
        String tok = this.token.toString();
        this.token.setLength(0);
        return tok;
    }

    public String toString() {
        return this.getPositionInfo();
    }
}

