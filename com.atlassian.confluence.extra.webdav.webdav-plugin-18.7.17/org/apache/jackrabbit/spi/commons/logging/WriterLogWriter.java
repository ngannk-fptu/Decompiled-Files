/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class WriterLogWriter
implements LogWriter {
    private final PrintWriter log;
    private final String category;

    public WriterLogWriter(Writer log, String category) {
        this.log = new PrintWriter(log);
        this.category = category;
    }

    @Override
    public long systemTime() {
        return System.currentTimeMillis();
    }

    @Override
    public void enter(String methodName, Object[] args) {
        this.print("ENTER(" + this.systemTime() + ") | " + methodName + "(" + this.formatArgs(args) + ")");
    }

    @Override
    public void leave(String methodName, Object[] args, Object result) {
        this.print("LEAVE(" + this.systemTime() + ") | " + methodName + "(" + this.formatArgs(args) + ") = " + this.formatResult(result));
    }

    @Override
    public void error(String methodName, Object[] args, Exception e) {
        this.print("ERROR(" + this.systemTime() + ") | " + methodName + "(" + this.formatArgs(args) + ") | " + this.formatException(e));
    }

    private void print(String msg) {
        this.log.print(this.category);
        this.log.print(": ");
        this.log.println(msg);
        this.log.flush();
    }

    private String formatArgs(Object[] args) {
        StringBuffer b = new StringBuffer();
        this.formatArgs(args, b);
        return b.toString();
    }

    private String formatResult(Object result) {
        StringBuffer b = new StringBuffer();
        this.formatArg(result, b);
        return b.toString();
    }

    private String formatException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private void formatArgs(Object[] args, StringBuffer b) {
        String separator = "";
        for (Object arg : args) {
            b.append(separator);
            this.formatArg(arg, b);
            separator = ", ";
        }
    }

    private void formatArg(Object arg, StringBuffer b) {
        if (arg instanceof Object[]) {
            b.append('[');
            this.formatArgs((Object[])arg, b);
            b.append(']');
        } else {
            b.append(arg);
        }
    }
}

