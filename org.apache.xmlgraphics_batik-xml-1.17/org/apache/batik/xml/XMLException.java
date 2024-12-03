/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.xml;

import java.io.PrintStream;
import java.io.PrintWriter;

public class XMLException
extends RuntimeException {
    protected Exception exception;

    public XMLException(String message) {
        super(message);
        this.exception = null;
    }

    public XMLException(Exception e) {
        this.exception = e;
    }

    public XMLException(String message, Exception e) {
        super(message);
        this.exception = e;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null && this.exception != null) {
            return this.exception.getMessage();
        }
        return message;
    }

    public Exception getException() {
        return this.exception;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace() {
        if (this.exception == null) {
            super.printStackTrace();
        } else {
            PrintStream printStream = System.err;
            synchronized (printStream) {
                System.err.println(this);
                super.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintStream s) {
        if (this.exception == null) {
            super.printStackTrace(s);
        } else {
            PrintStream printStream = s;
            synchronized (printStream) {
                s.println(this);
                super.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        if (this.exception == null) {
            super.printStackTrace(s);
        } else {
            PrintWriter printWriter = s;
            synchronized (printWriter) {
                s.println(this);
                super.printStackTrace(s);
            }
        }
    }
}

