/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.logging.log4j.core.impl.ExtendedStackTraceElement
 *  org.apache.logging.log4j.core.impl.ThrowableProxy
 *  org.apache.logging.log4j.core.util.Throwables
 *  org.apache.logging.log4j.util.Strings
 */
package com.atlassian.logging.log4j;

import com.atlassian.logging.log4j.LogMessageUtil;
import com.atlassian.logging.log4j.NewLineSupport;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.core.impl.ExtendedStackTraceElement;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.util.Throwables;
import org.apache.logging.log4j.util.Strings;

public class StackTraceInfo {
    private final String lineIndent;
    private final String[] stackTrace;
    private final boolean stackTracePackagingExamined;

    public StackTraceInfo(@Nonnull Throwable throwable, @Nonnull String lineIndent) {
        this(throwable, lineIndent, true);
    }

    public StackTraceInfo(@Nonnull Throwable throwable, @Nonnull String lineIndent, boolean stackTracePackagingExamined) {
        this.lineIndent = lineIndent;
        this.stackTracePackagingExamined = stackTracePackagingExamined;
        this.stackTrace = this.buildThrowableStrRep(throwable);
    }

    public static String asString(@Nonnull Throwable throwable) {
        return new StackTraceInfo(throwable, "    ", true).asString();
    }

    public static List<String> asLines(@Nonnull Throwable throwable) {
        return new StackTraceInfo(throwable, "    ", true).asLines();
    }

    public String[] getThrowableStrRep() {
        return this.stackTrace;
    }

    public String asString() {
        return NewLineSupport.join(this.stackTrace);
    }

    public List<String> asLines() {
        return Arrays.asList(this.stackTrace);
    }

    private String[] buildThrowableStrRep(@Nonnull Throwable throwable) {
        try {
            if (this.stackTracePackagingExamined) {
                return this.buildPackagingRepresentation(throwable);
            }
            return this.buildNoPackagingRepresentation(throwable);
        }
        catch (RuntimeException rte) {
            throwable.addSuppressed(rte);
            return this.buildNoPackagingRepresentation(throwable);
        }
    }

    private String[] buildNoPackagingRepresentation(Throwable throwable) {
        return Throwables.toStringList((Throwable)throwable).toArray(Strings.EMPTY_ARRAY);
    }

    private String[] buildPackagingRepresentation(Throwable throwable) {
        ThrowableProxy throwableProxy = new ThrowableProxy(throwable);
        ArrayList<String> lines = new ArrayList<String>();
        String message = throwable.toString();
        lines.add(LogMessageUtil.appendLineIndent(message, this.lineIndent));
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        this.addStackTraceLines(lines, "\t", throwableProxy.getExtendedStackTrace(), stackTraceElements.length - 1);
        this.addSuppressedThrowableInformation(lines, "", throwableProxy, stackTraceElements);
        Throwable cause = throwable.getCause();
        if (cause != null) {
            this.addCausedByThrowableInformation(lines, "", cause, throwableProxy.getCauseProxy(), stackTraceElements);
        }
        return lines.toArray(new String[lines.size()]);
    }

    private void addCausedByThrowableInformation(List<String> lines, String tabs, Throwable cause, ThrowableProxy causeProxy, StackTraceElement[] parentStackTrace) {
        String message = cause.toString();
        StackTraceElement[] stackTraceElements = cause.getStackTrace();
        ExtendedStackTraceElement[] extendedStackTrace = causeProxy.getExtendedStackTrace();
        int endUniqueFrames = Math.min(this.getEndOfUniqueFrames(parentStackTrace, stackTraceElements), extendedStackTrace.length);
        lines.add(tabs + "Caused by: " + LogMessageUtil.appendLineIndent(message, this.lineIndent));
        this.addStackTraceLines(lines, tabs + "\t", extendedStackTrace, endUniqueFrames);
        int more = stackTraceElements.length - 1 - endUniqueFrames;
        if (more > 0) {
            lines.add(tabs + "\t... " + more + " more");
        }
        this.addSuppressedThrowableInformation(lines, tabs + "\t", causeProxy, stackTraceElements);
        if (cause.getCause() != null) {
            this.addCausedByThrowableInformation(lines, tabs, cause.getCause(), causeProxy.getCauseProxy(), stackTraceElements);
        }
    }

    private void addSuppressedThrowableInformation(List<String> lines, String tabs, ThrowableProxy throwableProxy, StackTraceElement[] parentStackTrace) {
        ThrowableProxy[] suppressedTs;
        for (ThrowableProxy suppressed : suppressedTs = throwableProxy.getSuppressedProxies()) {
            String message = suppressed.getThrowable().toString();
            StackTraceElement[] stackTraceElements = suppressed.getStackTrace();
            ExtendedStackTraceElement[] extendedStackTrace = suppressed.getExtendedStackTrace();
            int endUniqueFrames = Math.min(this.getEndOfUniqueFrames(parentStackTrace, stackTraceElements), extendedStackTrace.length);
            lines.add(tabs + "Suppressed: " + LogMessageUtil.appendLineIndent(message, this.lineIndent));
            this.addStackTraceLines(lines, tabs + "\t", extendedStackTrace, endUniqueFrames);
            int more = stackTraceElements.length - 1 - endUniqueFrames;
            if (more > 0) {
                lines.add(tabs + "\t... " + more + " more");
            }
            if (suppressed.getCauseProxy() == null) continue;
            this.addCausedByThrowableInformation(lines, tabs, suppressed.getThrowable().getCause(), suppressed.getCauseProxy(), stackTraceElements);
        }
    }

    private String fmt(ExtendedStackTraceElement element) {
        return String.valueOf(element.getStackTraceElement()) + " " + '[' + element.getLocation() + ':' + element.getVersion() + ']';
    }

    private void addStackTraceLines(List<String> lines, String tabs, ExtendedStackTraceElement[] extendedStackTrace, int endPos) {
        for (int i = 0; i <= endPos; ++i) {
            lines.add(tabs + "at " + this.fmt(extendedStackTrace[i]));
        }
    }

    private int getEndOfUniqueFrames(StackTraceElement[] parentElements, StackTraceElement[] causeElements) {
        int causeStart;
        int i = parentElements.length - 1;
        for (causeStart = causeElements.length - 1; causeStart >= 0 && i >= 0 && causeElements[causeStart].equals(parentElements[i]); --causeStart, --i) {
        }
        return causeStart;
    }

    public void printStackTrace(PrintWriter pw) {
        for (String line : this.getThrowableStrRep()) {
            pw.println(line);
        }
    }

    public void printStackTrace(PrintStream ps) {
        for (String line : this.getThrowableStrRep()) {
            ps.println(line);
        }
    }
}

