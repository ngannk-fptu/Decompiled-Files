/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import org.xhtmlrenderer.css.parser.Token;

public class CSSParseException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Token _found;
    private final Token[] _expected;
    private int _line;
    private final String _genericMessage;
    private boolean _callerNotified;

    public CSSParseException(String message, int line) {
        this._found = null;
        this._expected = null;
        this._line = line;
        this._genericMessage = message;
    }

    public CSSParseException(Token found, Token expected, int line) {
        this._found = found;
        this._expected = new Token[]{expected};
        this._line = line;
        this._genericMessage = null;
    }

    public CSSParseException(Token found, Token[] expected, int line) {
        this._found = found;
        this._expected = expected == null ? new Token[]{} : (Token[])expected.clone();
        this._line = line;
        this._genericMessage = null;
    }

    @Override
    public String getMessage() {
        if (this._genericMessage != null) {
            return this._genericMessage + " at line " + (this._line + 1) + ".";
        }
        String found = this._found == null ? "end of file" : this._found.getExternalName();
        return "Found " + found + " where " + this.descr(this._expected) + " was expected at line " + (this._line + 1) + ".";
    }

    private String descr(Token[] tokens) {
        if (tokens.length == 1) {
            return tokens[0].getExternalName();
        }
        StringBuffer result = new StringBuffer();
        if (tokens.length > 2) {
            result.append("one of ");
        }
        for (int i = 0; i < tokens.length; ++i) {
            result.append(tokens[i].getExternalName());
            if (i < tokens.length - 2) {
                result.append(", ");
                continue;
            }
            if (i != tokens.length - 2) continue;
            if (tokens.length > 2) {
                result.append(", or ");
                continue;
            }
            result.append(" or ");
        }
        return result.toString();
    }

    public Token getFound() {
        return this._found;
    }

    public int getLine() {
        return this._line;
    }

    public void setLine(int i) {
        this._line = i;
    }

    public boolean isEOF() {
        return this._found == Token.TK_EOF;
    }

    public boolean isCallerNotified() {
        return this._callerNotified;
    }

    public void setCallerNotified(boolean callerNotified) {
        this._callerNotified = callerNotified;
    }
}

