/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser;

import org.apache.velocity.exception.ExtendedParseException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Token;

public class TemplateParseException
extends ParseException
implements ExtendedParseException {
    private static final long serialVersionUID = -3146323135623083918L;
    private final String templateName;

    public TemplateParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal, String templateNameVal) {
        super(currentTokenVal, expectedTokenSequencesVal, tokenImageVal);
        this.templateName = templateNameVal;
    }

    public TemplateParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
        super(currentTokenVal, expectedTokenSequencesVal, tokenImageVal);
        this.templateName = "*unset*";
    }

    public TemplateParseException() {
        this.templateName = "*unset*";
    }

    public TemplateParseException(String message) {
        super(message);
        this.templateName = "*unset*";
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int getLineNumber() {
        if (this.currentToken != null && this.currentToken.next != null) {
            return this.currentToken.next.beginLine;
        }
        return -1;
    }

    public int getColumnNumber() {
        if (this.currentToken != null && this.currentToken.next != null) {
            return this.currentToken.next.beginColumn;
        }
        return -1;
    }

    public String getMessage() {
        if (!this.specialConstructor) {
            StringBuffer sb = new StringBuffer(super.getMessage());
            this.appendTemplateInfo(sb);
            return sb.toString();
        }
        int maxSize = 0;
        StringBuffer expected = new StringBuffer();
        for (int i = 0; i < this.expectedTokenSequences.length; ++i) {
            if (maxSize < this.expectedTokenSequences[i].length) {
                maxSize = this.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < this.expectedTokenSequences[i].length; ++j) {
                expected.append(this.tokenImage[this.expectedTokenSequences[i][j]]).append(" ");
            }
            if (this.expectedTokenSequences[i][this.expectedTokenSequences[i].length - 1] != 0) {
                expected.append("...");
            }
            expected.append(this.eol).append("    ");
        }
        StringBuffer retval = new StringBuffer("Encountered \"");
        Token tok = this.currentToken.next;
        for (int i = 0; i < maxSize; ++i) {
            if (i != 0) {
                retval.append(" ");
            }
            if (tok.kind == 0) {
                retval.append(this.tokenImage[0]);
                break;
            }
            retval.append(this.add_escapes(tok.image));
            tok = tok.next;
        }
        retval.append("\" at ");
        this.appendTemplateInfo(retval);
        if (this.expectedTokenSequences.length == 1) {
            retval.append("Was expecting:").append(this.eol).append("    ");
        } else {
            retval.append("Was expecting one of:").append(this.eol).append("    ");
        }
        retval.append(expected.toString());
        return retval.toString();
    }

    protected void appendTemplateInfo(StringBuffer sb) {
        sb.append(Log.formatFileString(this.getTemplateName(), this.getLineNumber(), this.getColumnNumber()));
        sb.append(this.eol);
    }
}

