/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.aspectj.bridge.context.CompilationAndWeavingContext;

public class BCException
extends RuntimeException {
    Throwable thrown;

    public BCException() {
    }

    public BCException(String s) {
        super(s + "\n" + CompilationAndWeavingContext.getCurrentContext());
    }

    public BCException(String s, Throwable thrown) {
        this(s);
        this.thrown = thrown;
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        this.printStackTrace(new PrintWriter(s));
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (null != this.thrown) {
            s.print("Caused by: ");
            s.print(this.thrown.getClass().getName());
            String message = this.thrown.getMessage();
            if (null != message) {
                s.print(": ");
                s.print(message);
            }
            s.println();
            this.thrown.printStackTrace(s);
        }
    }
}

