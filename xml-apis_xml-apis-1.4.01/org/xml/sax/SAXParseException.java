/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAXParseException
extends SAXException {
    private String publicId;
    private String systemId;
    private int lineNumber;
    private int columnNumber;
    static final long serialVersionUID = -5651165872476709336L;

    public SAXParseException(String string, Locator locator) {
        super(string);
        if (locator != null) {
            this.init(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        } else {
            this.init(null, null, -1, -1);
        }
    }

    public SAXParseException(String string, Locator locator, Exception exception) {
        super(string, exception);
        if (locator != null) {
            this.init(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        } else {
            this.init(null, null, -1, -1);
        }
    }

    public SAXParseException(String string, String string2, String string3, int n, int n2) {
        super(string);
        this.init(string2, string3, n, n2);
    }

    public SAXParseException(String string, String string2, String string3, int n, int n2, Exception exception) {
        super(string, exception);
        this.init(string2, string3, n, n2);
    }

    private void init(String string, String string2, int n, int n2) {
        this.publicId = string;
        this.systemId = string2;
        this.lineNumber = n;
        this.columnNumber = n2;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }
}

