/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import java.io.PrintStream;
import java.io.PrintWriter;

public class XmlBuilderException
extends RuntimeException {
    protected Throwable detail;

    public XmlBuilderException(String s) {
        super(s);
    }

    public XmlBuilderException(String s, Throwable thrwble) {
        super(s);
        this.detail = thrwble;
    }

    public Throwable getDetail() {
        return this.detail;
    }

    public String getMessage() {
        if (this.detail == null) {
            return super.getMessage();
        }
        return super.getMessage() + "; nested exception is: \n\t" + this.detail.getMessage();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream ps) {
        if (this.detail == null) {
            super.printStackTrace(ps);
        } else {
            PrintStream printStream = ps;
            synchronized (printStream) {
                ps.println(super.getMessage() + "; nested exception is:");
                this.detail.printStackTrace(ps);
            }
        }
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter pw) {
        if (this.detail == null) {
            super.printStackTrace(pw);
        } else {
            PrintWriter printWriter = pw;
            synchronized (printWriter) {
                pw.println(super.getMessage() + "; nested exception is:");
                this.detail.printStackTrace(pw);
            }
        }
    }
}

