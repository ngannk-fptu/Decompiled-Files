/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;

public class XMLParseException
extends XNIException {
    static final long serialVersionUID = 1732959359448549967L;
    protected String fPublicId;
    protected String fLiteralSystemId;
    protected String fExpandedSystemId;
    protected String fBaseSystemId;
    protected int fLineNumber = -1;
    protected int fColumnNumber = -1;
    protected int fCharacterOffset = -1;

    public XMLParseException(XMLLocator xMLLocator, String string) {
        super(string);
        if (xMLLocator != null) {
            this.fPublicId = xMLLocator.getPublicId();
            this.fLiteralSystemId = xMLLocator.getLiteralSystemId();
            this.fExpandedSystemId = xMLLocator.getExpandedSystemId();
            this.fBaseSystemId = xMLLocator.getBaseSystemId();
            this.fLineNumber = xMLLocator.getLineNumber();
            this.fColumnNumber = xMLLocator.getColumnNumber();
            this.fCharacterOffset = xMLLocator.getCharacterOffset();
        }
    }

    public XMLParseException(XMLLocator xMLLocator, String string, Exception exception) {
        super(string, exception);
        if (xMLLocator != null) {
            this.fPublicId = xMLLocator.getPublicId();
            this.fLiteralSystemId = xMLLocator.getLiteralSystemId();
            this.fExpandedSystemId = xMLLocator.getExpandedSystemId();
            this.fBaseSystemId = xMLLocator.getBaseSystemId();
            this.fLineNumber = xMLLocator.getLineNumber();
            this.fColumnNumber = xMLLocator.getColumnNumber();
            this.fCharacterOffset = xMLLocator.getCharacterOffset();
        }
    }

    public String getPublicId() {
        return this.fPublicId;
    }

    public String getExpandedSystemId() {
        return this.fExpandedSystemId;
    }

    public String getLiteralSystemId() {
        return this.fLiteralSystemId;
    }

    public String getBaseSystemId() {
        return this.fBaseSystemId;
    }

    public int getLineNumber() {
        return this.fLineNumber;
    }

    public int getColumnNumber() {
        return this.fColumnNumber;
    }

    public int getCharacterOffset() {
        return this.fCharacterOffset;
    }

    @Override
    public String toString() {
        Exception exception;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.fPublicId != null) {
            stringBuffer.append(this.fPublicId);
        }
        stringBuffer.append(':');
        if (this.fLiteralSystemId != null) {
            stringBuffer.append(this.fLiteralSystemId);
        }
        stringBuffer.append(':');
        if (this.fExpandedSystemId != null) {
            stringBuffer.append(this.fExpandedSystemId);
        }
        stringBuffer.append(':');
        if (this.fBaseSystemId != null) {
            stringBuffer.append(this.fBaseSystemId);
        }
        stringBuffer.append(':');
        stringBuffer.append(this.fLineNumber);
        stringBuffer.append(':');
        stringBuffer.append(this.fColumnNumber);
        stringBuffer.append(':');
        stringBuffer.append(this.fCharacterOffset);
        stringBuffer.append(':');
        String string = this.getMessage();
        if (string == null && (exception = this.getException()) != null) {
            string = exception.getMessage();
        }
        if (string != null) {
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }
}

