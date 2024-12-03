/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.velocity.exception.ExtendedParseException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.util.introspection.Info;

public class ParseErrorException
extends VelocityException {
    private static final long serialVersionUID = -6665197935086306472L;
    private int columnNumber = -1;
    private int lineNumber = -1;
    private String templateName = "*unset*";
    private String invalidSyntax;
    private String msg = null;
    private static final Pattern lexError = Pattern.compile("Lexical error.*TokenMgrError.*line (\\d+),.*column (\\d+)\\.(.*)");

    public ParseErrorException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ParseErrorException(ParseException pex, String templName) {
        super(pex.getMessage());
        if (templName != null) {
            this.templateName = templName;
        }
        if (pex instanceof ExtendedParseException) {
            ExtendedParseException xpex = (ExtendedParseException)((Object)pex);
            this.columnNumber = xpex.getColumnNumber();
            this.lineNumber = xpex.getLineNumber();
            this.templateName = xpex.getTemplateName();
        } else {
            Matcher match = lexError.matcher(pex.getMessage());
            if (match.matches()) {
                this.lineNumber = Integer.parseInt(match.group(1));
                this.columnNumber = Integer.parseInt(match.group(2));
                String restOfMsg = match.group(3);
                this.msg = "Lexical error, " + restOfMsg + " at " + Log.formatFileString(this.templateName, this.lineNumber, this.columnNumber);
            }
            if (pex.currentToken != null && pex.currentToken.next != null) {
                this.columnNumber = pex.currentToken.next.beginColumn;
                this.lineNumber = pex.currentToken.next.beginLine;
            }
        }
    }

    public ParseErrorException(VelocityException pex, String templName) {
        super(pex.getMessage());
        if (templName != null) {
            this.templateName = templName;
        }
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

    public String getMessage() {
        if (this.msg != null) {
            return this.msg;
        }
        return super.getMessage();
    }
}

