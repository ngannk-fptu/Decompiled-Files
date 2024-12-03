/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.PotentiallySecondary;
import java.io.PrintStream;
import java.io.PrintWriter;

public class PotentiallySecondaryError
extends Error
implements PotentiallySecondary {
    static final String NESTED_MSG = ">>>>>>>>>> NESTED THROWABLE >>>>>>>>";
    Throwable nested;

    public PotentiallySecondaryError(String string, Throwable throwable) {
        super(string);
        this.nested = throwable;
    }

    public PotentiallySecondaryError(Throwable throwable) {
        this("", throwable);
    }

    public PotentiallySecondaryError(String string) {
        this(string, null);
    }

    public PotentiallySecondaryError() {
        this("", null);
    }

    @Override
    public Throwable getNestedThrowable() {
        return this.nested;
    }

    @Override
    public void printStackTrace(PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (this.nested != null) {
            printWriter.println(NESTED_MSG);
            this.nested.printStackTrace(printWriter);
        }
    }

    @Override
    public void printStackTrace(PrintStream printStream) {
        super.printStackTrace(printStream);
        if (this.nested != null) {
            printStream.println("NESTED_MSG");
            this.nested.printStackTrace(printStream);
        }
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
}

