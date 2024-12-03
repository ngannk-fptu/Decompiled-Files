/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.script;

public class InterpreterException
extends RuntimeException {
    private int line = -1;
    private int column = -1;
    private Exception embedded = null;

    public InterpreterException(String message, int lineno, int columnno) {
        super(message);
        this.line = lineno;
        this.column = columnno;
    }

    public InterpreterException(Exception exception, String message, int lineno, int columnno) {
        this(message, lineno, columnno);
        this.embedded = exception;
    }

    public int getLineNumber() {
        return this.line;
    }

    public int getColumnNumber() {
        return this.column;
    }

    public Exception getException() {
        return this.embedded;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null) {
            return msg;
        }
        if (this.embedded != null) {
            return this.embedded.getMessage();
        }
        return null;
    }
}

