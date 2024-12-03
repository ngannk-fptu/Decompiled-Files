/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XNIException;

public class XMLParseException
extends XNIException {
    private static final long serialVersionUID = -1306660736099956209L;
    private String publicId_;
    private String literalSystemId_;
    private String expandedSystemId_;
    private String baseSystemId_;
    private int lineNumber_ = -1;
    private int columnNumber_ = -1;
    private int characterOffset_ = -1;

    public XMLParseException(XMLLocator locator, String message) {
        super(message);
        if (locator != null) {
            this.publicId_ = locator.getPublicId();
            this.literalSystemId_ = locator.getLiteralSystemId();
            this.expandedSystemId_ = locator.getExpandedSystemId();
            this.baseSystemId_ = locator.getBaseSystemId();
            this.lineNumber_ = locator.getLineNumber();
            this.columnNumber_ = locator.getColumnNumber();
            this.characterOffset_ = locator.getCharacterOffset();
        }
    }

    public XMLParseException(XMLLocator locator, String message, Exception exception) {
        super(message, exception);
        if (locator != null) {
            this.publicId_ = locator.getPublicId();
            this.literalSystemId_ = locator.getLiteralSystemId();
            this.expandedSystemId_ = locator.getExpandedSystemId();
            this.baseSystemId_ = locator.getBaseSystemId();
            this.lineNumber_ = locator.getLineNumber();
            this.columnNumber_ = locator.getColumnNumber();
            this.characterOffset_ = locator.getCharacterOffset();
        }
    }

    public String getPublicId() {
        return this.publicId_;
    }

    public String getExpandedSystemId() {
        return this.expandedSystemId_;
    }

    public String getLiteralSystemId() {
        return this.literalSystemId_;
    }

    public String getBaseSystemId() {
        return this.baseSystemId_;
    }

    public int getLineNumber() {
        return this.lineNumber_;
    }

    public int getColumnNumber() {
        return this.columnNumber_;
    }

    public int getCharacterOffset() {
        return this.characterOffset_;
    }

    @Override
    public String toString() {
        Exception exception;
        StringBuilder str = new StringBuilder();
        if (this.publicId_ != null) {
            str.append(this.publicId_);
        }
        str.append(':');
        if (this.literalSystemId_ != null) {
            str.append(this.literalSystemId_);
        }
        str.append(':');
        if (this.expandedSystemId_ != null) {
            str.append(this.expandedSystemId_);
        }
        str.append(':');
        if (this.baseSystemId_ != null) {
            str.append(this.baseSystemId_);
        }
        str.append(':');
        str.append(this.lineNumber_);
        str.append(':');
        str.append(this.columnNumber_);
        str.append(':');
        str.append(this.characterOffset_);
        str.append(':');
        String message = this.getMessage();
        if (message == null && (exception = this.getException()) != null) {
            message = exception.getMessage();
        }
        if (message != null) {
            str.append(message);
        }
        return str.toString();
    }
}

