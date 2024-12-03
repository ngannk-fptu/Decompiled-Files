/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.factory;

import java.io.PrintStream;
import java.io.PrintWriter;

public class FactoryException
extends RuntimeException {
    protected Throwable exception = null;

    public FactoryException() {
    }

    public FactoryException(String msg) {
        super(msg);
    }

    public FactoryException(Exception e) {
        this.exception = e;
    }

    public FactoryException(String msg, Throwable e) {
        super(msg + ": " + e);
        this.exception = e;
    }

    public Throwable getRootCause() {
        return this.exception;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace() {
        super.printStackTrace();
        if (this.exception != null) {
            PrintStream printStream = System.err;
            synchronized (printStream) {
                System.err.println("\nRoot cause:");
                this.exception.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (this.exception != null) {
            PrintStream printStream = s;
            synchronized (printStream) {
                s.println("\nRoot cause:");
                this.exception.printStackTrace(s);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (this.exception != null) {
            PrintWriter printWriter = s;
            synchronized (printWriter) {
                s.println("\nRoot cause:");
                this.exception.printStackTrace(s);
            }
        }
    }
}

