/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom;

import java.io.PrintStream;
import java.io.PrintWriter;

public class DOMVisitorException
extends RuntimeException {
    private Exception cause_ = null;

    public DOMVisitorException(String s) {
        super(s);
    }

    public DOMVisitorException(Exception exception) {
        super(exception.getMessage());
        this.cause_ = exception;
    }

    public DOMVisitorException(String s, Exception exception) {
        super(s);
        this.cause_ = exception;
    }

    public Exception getException() {
        if (this.cause_ != null) {
            return this.cause_;
        }
        return this;
    }

    public Exception getCauseException() {
        return this.cause_;
    }

    public void printStackTrace() {
        this.printStackTrace(new PrintWriter(System.err, true));
    }

    public void printStackTrace(PrintStream printstream) {
        this.printStackTrace(new PrintWriter(printstream));
    }

    public void printStackTrace(PrintWriter printwriter) {
        if (printwriter == null) {
            printwriter = new PrintWriter(System.err, true);
        }
        super.printStackTrace(printwriter);
        if (this.cause_ != null) {
            printwriter.println();
            printwriter.println("StackTrace of Original Exception:");
            this.cause_.printStackTrace(printwriter);
        }
    }
}

