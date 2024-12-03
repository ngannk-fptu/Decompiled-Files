/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.base.internal;

import com.google.common.base.Preconditions;

public class IndentedLinesBuilder
implements CharSequence,
Appendable {
    private static final String SPACES = "                        ";
    private static final int MAX_INDENT_LEN = 24;
    private final StringBuilder sb = new StringBuilder();
    private final int indentIncrementLen;
    private int indentLen;
    private String indent;

    public IndentedLinesBuilder(int indentIncrementLen) {
        this.indentIncrementLen = indentIncrementLen;
        this.indentLen = 0;
        this.indent = "";
    }

    public IndentedLinesBuilder(int indentIncrementLen, int initialIndentLen) {
        this.indentIncrementLen = indentIncrementLen;
        this.indentLen = initialIndentLen;
        Preconditions.checkState((0 <= this.indentLen && this.indentLen <= 24 ? 1 : 0) != 0);
        this.indent = SPACES.substring(0, this.indentLen);
    }

    public StringBuilder sb() {
        return this.sb;
    }

    public int getIndentIncrementLen() {
        return this.indentIncrementLen;
    }

    public int getCurrIndentLen() {
        return this.indentLen;
    }

    public void increaseIndent() {
        this.increaseIndent(1);
    }

    public void increaseIndent(int numStops) {
        this.indentLen += numStops * this.indentIncrementLen;
        Preconditions.checkState((0 <= this.indentLen && this.indentLen <= 24 ? 1 : 0) != 0);
        this.indent = SPACES.substring(0, this.indentLen);
    }

    public void decreaseIndent() {
        this.decreaseIndent(1);
    }

    public void decreaseIndent(int numStops) {
        this.indentLen -= numStops * this.indentIncrementLen;
        Preconditions.checkState((0 <= this.indentLen && this.indentLen <= 24 ? 1 : 0) != 0);
        this.indent = SPACES.substring(0, this.indentLen);
    }

    public void setIndentLen(int indentLen) {
        this.indentLen = indentLen;
        Preconditions.checkState((0 <= indentLen && indentLen <= 24 ? 1 : 0) != 0);
        this.indent = SPACES.substring(0, indentLen);
    }

    public void appendLine(Object ... parts) {
        if (parts.length > 0) {
            this.sb.append(this.indent);
        }
        this.appendParts(parts);
        this.sb.append('\n');
    }

    public IndentedLinesBuilder appendParts(Object ... parts) {
        for (Object part : parts) {
            this.sb.append(part);
        }
        return this;
    }

    public IndentedLinesBuilder appendLineStart(Object ... parts) {
        this.sb.append(this.indent);
        this.appendParts(parts);
        return this;
    }

    public IndentedLinesBuilder appendLineEnd(Object ... parts) {
        this.appendParts(parts);
        this.sb.append("\n");
        return this;
    }

    @Override
    public String toString() {
        return this.sb.toString();
    }

    @Override
    public int length() {
        return this.sb.length();
    }

    @Override
    public char charAt(int index) {
        return this.sb.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.sb.subSequence(start, end);
    }

    @Override
    public IndentedLinesBuilder append(CharSequence csq) {
        this.sb.append(csq);
        return this;
    }

    @Override
    public IndentedLinesBuilder append(CharSequence csq, int start, int end) {
        this.sb.append(csq, start, end);
        return this;
    }

    @Override
    public IndentedLinesBuilder append(char c) {
        this.sb.append(c);
        return this;
    }
}

