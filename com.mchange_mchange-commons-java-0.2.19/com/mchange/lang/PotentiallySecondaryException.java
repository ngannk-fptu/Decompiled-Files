/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.PotentiallySecondary;
import com.mchange.v2.lang.VersionUtils;
import java.io.PrintStream;
import java.io.PrintWriter;

public class PotentiallySecondaryException
extends Exception
implements PotentiallySecondary {
    static final String NESTED_MSG = ">>>>>>>>>> NESTED EXCEPTION >>>>>>>>";
    Throwable nested;

    public PotentiallySecondaryException(String string, Throwable throwable) {
        super(string, throwable);
        this.nested = throwable;
    }

    public PotentiallySecondaryException(Throwable throwable) {
        this("", throwable);
    }

    public PotentiallySecondaryException(String string) {
        this(string, null);
    }

    public PotentiallySecondaryException() {
        this("", null);
    }

    @Override
    public Throwable getNestedThrowable() {
        return this.nested;
    }

    private void setNested(Throwable throwable) {
        this.nested = throwable;
        if (VersionUtils.isAtLeastJavaVersion1_4()) {
            this.initCause(throwable);
        }
    }

    @Override
    public void printStackTrace(PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (!VersionUtils.isAtLeastJavaVersion1_4() && this.nested != null) {
            printWriter.println(NESTED_MSG);
            this.nested.printStackTrace(printWriter);
        }
    }

    @Override
    public void printStackTrace(PrintStream printStream) {
        super.printStackTrace(printStream);
        if (!VersionUtils.isAtLeastJavaVersion1_4() && this.nested != null) {
            printStream.println("NESTED_MSG");
            this.nested.printStackTrace(printStream);
        }
    }

    @Override
    public void printStackTrace() {
        if (VersionUtils.isAtLeastJavaVersion1_4()) {
            super.printStackTrace();
        } else {
            this.printStackTrace(System.err);
        }
    }
}

