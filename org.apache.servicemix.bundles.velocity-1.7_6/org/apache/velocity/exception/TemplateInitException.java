/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.exception;

import org.apache.velocity.exception.ExtendedParseException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.parser.ParseException;

public class TemplateInitException
extends VelocityException
implements ExtendedParseException {
    private final String templateName;
    private final int col;
    private final int line;
    private static final long serialVersionUID = -4985224672336070621L;

    public TemplateInitException(String msg, String templateName, int col, int line) {
        super(msg);
        this.templateName = templateName;
        this.col = col;
        this.line = line;
    }

    public TemplateInitException(String msg, ParseException parseException, String templateName, int col, int line) {
        super(msg, parseException);
        this.templateName = templateName;
        this.col = col;
        this.line = line;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int getLineNumber() {
        return this.line;
    }

    public int getColumnNumber() {
        return this.col;
    }
}

