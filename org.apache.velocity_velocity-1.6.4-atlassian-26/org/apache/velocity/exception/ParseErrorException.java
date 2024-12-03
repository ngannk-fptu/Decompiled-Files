/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.exception;

import org.apache.velocity.exception.ExtendedParseException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.util.introspection.Info;

public class ParseErrorException
extends VelocityException {
    private static final long serialVersionUID = -6665197935086306472L;
    private int columnNumber = -1;
    private int lineNumber = -1;
    private String templateName = "*unset*";
    private String invalidSyntax;

    public ParseErrorException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ParseErrorException(ParseException pex) {
        super(pex.getMessage());
        if (pex instanceof ExtendedParseException) {
            ExtendedParseException xpex = (ExtendedParseException)((Object)pex);
            this.columnNumber = xpex.getColumnNumber();
            this.lineNumber = xpex.getLineNumber();
            this.templateName = xpex.getTemplateName();
        } else if (pex.currentToken != null && pex.currentToken.next != null) {
            this.columnNumber = pex.currentToken.next.beginColumn;
            this.lineNumber = pex.currentToken.next.beginLine;
        }
    }

    public ParseErrorException(VelocityException pex) {
        super(pex.getMessage());
        if (pex instanceof ExtendedParseException) {
            ExtendedParseException xpex = (ExtendedParseException)((Object)pex);
            this.columnNumber = xpex.getColumnNumber();
            this.lineNumber = xpex.getLineNumber();
            this.templateName = xpex.getTemplateName();
        } else if (pex.getWrappedThrowable() instanceof ParseException) {
            ParseException pex2 = (ParseException)pex.getWrappedThrowable();
            if (pex2.currentToken != null && pex2.currentToken.next != null) {
                this.columnNumber = pex2.currentToken.next.beginColumn;
                this.lineNumber = pex2.currentToken.next.beginLine;
            }
        }
    }

    public ParseErrorException(String exceptionMessage, Info info) {
        super(exceptionMessage);
        this.columnNumber = info.getColumn();
        this.lineNumber = info.getLine();
        this.templateName = info.getTemplateName();
    }

    public ParseErrorException(String exceptionMessage, Info info, String invalidSyntax) {
        super(exceptionMessage);
        this.columnNumber = info.getColumn();
        this.lineNumber = info.getLine();
        this.templateName = info.getTemplateName();
        this.invalidSyntax = invalidSyntax;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public String getInvalidSyntax() {
        return this.invalidSyntax;
    }
}

