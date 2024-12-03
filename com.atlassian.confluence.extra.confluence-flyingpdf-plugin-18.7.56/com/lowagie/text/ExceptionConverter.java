/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ExceptionConverter
extends RuntimeException {
    private static final long serialVersionUID = 8657630363395849399L;
    private Exception ex;
    private String prefix;

    public ExceptionConverter(Exception ex) {
        this.ex = ex;
        this.prefix = ex instanceof RuntimeException ? "" : "ExceptionConverter: ";
    }

    public static final RuntimeException convertException(Exception ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException)ex;
        }
        return new ExceptionConverter(ex);
    }

    public Exception getException() {
        return this.ex;
    }

    @Override
    public String getMessage() {
        return this.ex.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return this.ex.getLocalizedMessage();
    }

    @Override
    public String toString() {
        return this.prefix + this.ex;
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintStream s) {
        PrintStream printStream = s;
        synchronized (printStream) {
            s.print(this.prefix);
            this.ex.printStackTrace(s);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        PrintWriter printWriter = s;
        synchronized (printWriter) {
            s.print(this.prefix);
            this.ex.printStackTrace(s);
        }
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

