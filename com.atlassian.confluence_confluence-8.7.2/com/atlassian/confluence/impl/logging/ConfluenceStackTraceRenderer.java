/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.servlet.ServletException
 *  org.apache.velocity.exception.MethodInvocationException
 */
package com.atlassian.confluence.impl.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import org.apache.velocity.exception.MethodInvocationException;

public final class ConfluenceStackTraceRenderer {
    private ConfluenceStackTraceRenderer() {
    }

    public static CharSequence renderStackTrace(Throwable t) {
        StringWriter writer = new StringWriter();
        PrintWriter stackTrace = new PrintWriter(writer);
        t.printStackTrace(stackTrace);
        while (t != null) {
            if ((t = ConfluenceStackTraceRenderer.getNonCauseUnderlying(t)) == null) continue;
            stackTrace.print("Caused by: ");
            t.printStackTrace(stackTrace);
        }
        return writer.getBuffer();
    }

    @Nullable
    public static Throwable getNonCauseUnderlying(Throwable t) {
        while (t != null && t.getCause() != null) {
            t = t.getCause();
        }
        if (t != null) {
            t = t instanceof InvocationTargetException ? ((InvocationTargetException)t).getTargetException() : (t instanceof MethodInvocationException ? ((MethodInvocationException)t).getWrappedThrowable() : (t instanceof ServletException ? ((ServletException)t).getRootCause() : (t instanceof SQLException ? ((SQLException)t).getNextException() : null)));
        }
        return t;
    }
}

