/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface Nestable {
    public Throwable getCause();

    public String getMessage();

    public String getMessage(int var1);

    public String[] getMessages();

    public Throwable getThrowable(int var1);

    public int getThrowableCount();

    public Throwable[] getThrowables();

    public int indexOfThrowable(Class var1);

    public int indexOfThrowable(Class var1, int var2);

    public void printStackTrace(PrintWriter var1);

    public void printStackTrace(PrintStream var1);

    public void printPartialStackTrace(PrintWriter var1);
}

