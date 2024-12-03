/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.Token;
import org.codehaus.groovy.antlr.SourceInfo;

public class GroovySourceToken
extends Token
implements SourceInfo {
    protected int line;
    protected String text = "";
    protected int col;
    protected int lineLast;
    protected int colLast;

    public GroovySourceToken(int t) {
        super(t);
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setLine(int l) {
        this.line = l;
    }

    @Override
    public void setText(String s) {
        this.text = s;
    }

    @Override
    public String toString() {
        return "[\"" + this.getText() + "\",<" + this.type + ">,line=" + this.line + ",col=" + this.col + ",lineLast=" + this.lineLast + ",colLast=" + this.colLast + "]";
    }

    @Override
    public int getColumn() {
        return this.col;
    }

    @Override
    public void setColumn(int c) {
        this.col = c;
    }

    @Override
    public int getLineLast() {
        return this.lineLast;
    }

    @Override
    public void setLineLast(int lineLast) {
        this.lineLast = lineLast;
    }

    @Override
    public int getColumnLast() {
        return this.colLast;
    }

    @Override
    public void setColumnLast(int colLast) {
        this.colLast = colLast;
    }
}

