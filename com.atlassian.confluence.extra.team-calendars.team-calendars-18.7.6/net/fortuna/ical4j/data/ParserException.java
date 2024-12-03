/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.text.MessageFormat;

public class ParserException
extends Exception {
    private static final long serialVersionUID = 6116644246112002214L;
    private static final String ERROR_MESSAGE_PATTERN = "Error at line {0}:";
    private int lineNo;

    public ParserException(int lineNo) {
        this.lineNo = lineNo;
    }

    public ParserException(String message, int lineNo) {
        super(MessageFormat.format(ERROR_MESSAGE_PATTERN, lineNo) + message);
        this.lineNo = lineNo;
    }

    public ParserException(String message, int lineNo, Throwable cause) {
        super(MessageFormat.format(ERROR_MESSAGE_PATTERN, lineNo) + message, cause);
        this.lineNo = lineNo;
    }

    public final int getLineNo() {
        return this.lineNo;
    }
}

