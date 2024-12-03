/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.Locator;

public class CSSParseException
extends CSSException {
    private String uri;
    private int lineNumber;
    private int columnNumber;

    public CSSParseException(String string, Locator locator) {
        super(string);
        this.code = (short)2;
        this.uri = locator.getURI();
        this.lineNumber = locator.getLineNumber();
        this.columnNumber = locator.getColumnNumber();
    }

    public CSSParseException(String string, Locator locator, Exception exception) {
        super((short)2, string, exception);
        this.uri = locator.getURI();
        this.lineNumber = locator.getLineNumber();
        this.columnNumber = locator.getColumnNumber();
    }

    public CSSParseException(String string, String string2, int n, int n2) {
        super(string);
        this.code = (short)2;
        this.uri = string2;
        this.lineNumber = n;
        this.columnNumber = n2;
    }

    public CSSParseException(String string, String string2, int n, int n2, Exception exception) {
        super((short)2, string, exception);
        this.uri = string2;
        this.lineNumber = n;
        this.columnNumber = n2;
    }

    public String getURI() {
        return this.uri;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }
}

