/*
 * Decompiled with CFR 0.152.
 */
package org.aopalliance.aop;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AspectException
extends RuntimeException {
    private String message;
    private String stackTrace;
    private Throwable t;

    public Throwable getCause() {
        return this.t;
    }

    public String toString() {
        return this.getMessage();
    }

    public String getMessage() {
        return this.message;
    }

    public void printStackTrace() {
        System.err.print(this.stackTrace);
    }

    public void printStackTrace(PrintStream printStream) {
        this.printStackTrace(new PrintWriter(printStream));
    }

    public void printStackTrace(PrintWriter printWriter) {
        printWriter.print(this.stackTrace);
    }

    public AspectException(String string) {
        super(string);
        this.message = string;
        this.stackTrace = string;
    }

    public AspectException(String string, Throwable throwable) {
        super(string + "; nested exception is " + throwable.getMessage());
        this.t = throwable;
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        this.stackTrace = stringWriter.toString();
    }
}

