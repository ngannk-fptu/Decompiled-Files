/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast;

import antlr.SemanticException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.hibernate.internal.build.AllowPrintStacktrace;

public class DetailedSemanticException
extends SemanticException {
    private Throwable cause;
    private boolean showCauseMessage = true;

    public DetailedSemanticException(String message) {
        super(message);
    }

    public DetailedSemanticException(String s, Throwable e) {
        super(s);
        this.cause = e;
    }

    public String toString() {
        if (this.cause == null || !this.showCauseMessage) {
            return super.toString();
        }
        return super.toString() + "\n[cause=" + this.cause.toString() + "]";
    }

    @AllowPrintStacktrace
    public void printStackTrace() {
        super.printStackTrace();
        if (this.cause != null) {
            this.cause.printStackTrace();
        }
    }

    @AllowPrintStacktrace
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (this.cause != null) {
            s.println("Cause:");
            this.cause.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter w) {
        super.printStackTrace(w);
        if (this.cause != null) {
            w.println("Cause:");
            this.cause.printStackTrace(w);
        }
    }
}

