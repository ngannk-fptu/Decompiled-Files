/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;

public class XhtmlParsingException
extends XhtmlException {
    private final int lineNumber;
    private final int columnNumber;
    private final String parserMessage;

    public XhtmlParsingException(int lineNumber, int columnNumber, String message, Throwable cause) {
        super("The XML content could not be parsed. There is a problem at line " + lineNumber + ", column " + columnNumber + ". Parser message: " + PlainTextToHtmlConverter.encodeHtmlEntities(message), cause);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.parserMessage = message;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public String getParserMessage() {
        return this.parserMessage;
    }
}

