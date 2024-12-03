/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package org.apache.jackrabbit.spi.commons.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;
import org.slf4j.Logger;

public class Slf4jLogWriter
implements LogWriter {
    private final Logger log;

    public Slf4jLogWriter(Logger log) {
        this.log = log;
    }

    @Override
    public long systemTime() {
        return System.currentTimeMillis();
    }

    @Override
    public void enter(String methodName, Object[] args) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("ENTER(" + this.systemTime() + ") | " + methodName + "(" + this.formatArgs(args) + ")");
        }
    }

    @Override
    public void leave(String methodName, Object[] args, Object result) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("LEAVE(" + this.systemTime() + ") | " + methodName + "(" + this.formatArgs(args) + ") = " + this.formatResult(result));
        }
    }

    @Override
    public void error(String methodName, Object[] args, Exception e) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("ERROR(" + this.systemTime() + ") | " + methodName + "(" + this.formatArgs(args) + ") | " + this.formatException(e));
        }
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
        for (int k = 0; k < args.length; ++k) {
            b.append(separator);
            this.formatArg(args[k], b);
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

