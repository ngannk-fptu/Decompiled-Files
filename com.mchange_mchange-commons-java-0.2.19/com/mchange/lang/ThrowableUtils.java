/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ThrowableUtils {
    public static String extractStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    public static boolean isChecked(Throwable throwable) {
        return throwable instanceof Exception && !(throwable instanceof RuntimeException);
    }

    public static boolean isUnchecked(Throwable throwable) {
        return !ThrowableUtils.isChecked(throwable);
    }

    private ThrowableUtils() {
    }
}

