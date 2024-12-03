/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Token;

public class CommonToken
extends Token {
    protected int line;
    protected String text = null;
    protected int col;

    public CommonToken() {
    }

    public CommonToken(int n, String string) {
        this.type = n;
        this.setText(string);
    }

    public CommonToken(String string) {
        this.text = string;
    }

    public int getLine() {
        return this.line;
    }

    public String getText() {
        return this.text;
    }

    public void setLine(int n) {
        this.line = n;
    }

    public void setText(String string) {
        this.text = string;
    }

    public String toString() {
        return "[\"" + this.getText() + "\",<" + this.type + ">,line=" + this.line + ",col=" + this.col + "]";
    }

    public int getColumn() {
        return this.col;
    }

    public void setColumn(int n) {
        this.col = n;
    }
}

