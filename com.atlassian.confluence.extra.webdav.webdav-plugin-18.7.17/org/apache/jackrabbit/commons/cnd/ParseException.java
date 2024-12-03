/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.cnd;

public class ParseException
extends Exception {
    private final int lineNumber;
    private final int colNumber;
    private final String systemId;

    public ParseException(int lineNumber, int colNumber, String systemId) {
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
        this.systemId = systemId;
    }

    public ParseException(String message, int lineNumber, int colNumber, String systemId) {
        super(message);
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
        this.systemId = systemId;
    }

    public ParseException(String message, Throwable rootCause, int lineNumber, int colNumber, String systemId) {
        super(message, rootCause);
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
        this.systemId = systemId;
    }

    public ParseException(Throwable rootCause, int lineNumber, int colNumber, String systemId) {
        super(rootCause);
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
        this.systemId = systemId;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        StringBuffer b = new StringBuffer(message == null ? "" : message);
        String delim = " (";
        if (this.systemId != null && !this.systemId.equals("")) {
            b.append(delim);
            b.append(this.systemId);
            delim = ", ";
        }
        if (this.lineNumber >= 0) {
            b.append(delim);
            b.append("line ");
            b.append(this.lineNumber);
            delim = ", ";
        }
        if (this.colNumber >= 0) {
            b.append(delim);
            b.append("col ");
            b.append(this.colNumber);
            delim = ", ";
        }
        if (delim.equals(", ")) {
            b.append(")");
        }
        return b.toString();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

