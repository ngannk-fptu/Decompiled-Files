/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ssi;

public class ExpressionTokenizer {
    public static final int TOKEN_STRING = 0;
    public static final int TOKEN_AND = 1;
    public static final int TOKEN_OR = 2;
    public static final int TOKEN_NOT = 3;
    public static final int TOKEN_EQ = 4;
    public static final int TOKEN_NOT_EQ = 5;
    public static final int TOKEN_RBRACE = 6;
    public static final int TOKEN_LBRACE = 7;
    public static final int TOKEN_GE = 8;
    public static final int TOKEN_LE = 9;
    public static final int TOKEN_GT = 10;
    public static final int TOKEN_LT = 11;
    public static final int TOKEN_END = 12;
    private final char[] expr;
    private String tokenVal = null;
    private int index;
    private final int length;

    public ExpressionTokenizer(String expr) {
        this.expr = expr.trim().toCharArray();
        this.length = this.expr.length;
    }

    public boolean hasMoreTokens() {
        return this.index < this.length;
    }

    public int getIndex() {
        return this.index;
    }

    protected boolean isMetaChar(char c) {
        return Character.isWhitespace(c) || c == '(' || c == ')' || c == '!' || c == '<' || c == '>' || c == '|' || c == '&' || c == '=';
    }

    public int nextToken() {
        while (this.index < this.length && Character.isWhitespace(this.expr[this.index])) {
            ++this.index;
        }
        this.tokenVal = null;
        if (this.index == this.length) {
            return 12;
        }
        int start = this.index;
        char currentChar = this.expr[this.index];
        int nextChar = 0;
        ++this.index;
        if (this.index < this.length) {
            nextChar = this.expr[this.index];
        }
        switch (currentChar) {
            case '(': {
                return 7;
            }
            case ')': {
                return 6;
            }
            case '=': {
                return 4;
            }
            case '!': {
                if (nextChar == 61) {
                    ++this.index;
                    return 5;
                }
                return 3;
            }
            case '|': {
                if (nextChar != 124) break;
                ++this.index;
                return 2;
            }
            case '&': {
                if (nextChar != 38) break;
                ++this.index;
                return 1;
            }
            case '>': {
                if (nextChar == 61) {
                    ++this.index;
                    return 8;
                }
                return 10;
            }
            case '<': {
                if (nextChar == 61) {
                    ++this.index;
                    return 9;
                }
                return 11;
            }
        }
        int end = this.index;
        if (currentChar == '\"' || currentChar == '\'') {
            char endChar = currentChar;
            boolean escaped = false;
            ++start;
            while (this.index < this.length) {
                if (this.expr[this.index] == '\\' && !escaped) {
                    escaped = true;
                } else {
                    if (this.expr[this.index] == endChar && !escaped) break;
                    escaped = false;
                }
                ++this.index;
            }
            end = this.index++;
        } else if (currentChar == '/') {
            char endChar = currentChar;
            boolean escaped = false;
            while (this.index < this.length) {
                if (this.expr[this.index] == '\\' && !escaped) {
                    escaped = true;
                } else {
                    if (this.expr[this.index] == endChar && !escaped) break;
                    escaped = false;
                }
                ++this.index;
            }
            end = ++this.index;
        } else {
            while (this.index < this.length && !this.isMetaChar(this.expr[this.index])) {
                ++this.index;
            }
            end = this.index;
        }
        this.tokenVal = new String(this.expr, start, end - start);
        return 0;
    }

    public String getTokenValue() {
        return this.tokenVal;
    }
}

