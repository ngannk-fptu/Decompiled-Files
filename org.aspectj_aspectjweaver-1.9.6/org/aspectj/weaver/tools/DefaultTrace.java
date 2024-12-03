/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.io.PrintStream;
import org.aspectj.weaver.tools.AbstractTrace;

public class DefaultTrace
extends AbstractTrace {
    private boolean traceEnabled = false;
    private PrintStream print = System.err;

    public DefaultTrace(Class clazz) {
        super(clazz);
    }

    @Override
    public boolean isTraceEnabled() {
        return this.traceEnabled;
    }

    @Override
    public void setTraceEnabled(boolean b) {
        this.traceEnabled = b;
    }

    @Override
    public void enter(String methodName, Object thiz, Object[] args) {
        if (this.traceEnabled) {
            this.println(this.formatMessage(">", this.tracedClass.getName(), methodName, thiz, args));
        }
    }

    @Override
    public void enter(String methodName, Object thiz) {
        if (this.traceEnabled) {
            this.println(this.formatMessage(">", this.tracedClass.getName(), methodName, thiz, null));
        }
    }

    @Override
    public void exit(String methodName, Object ret) {
        if (this.traceEnabled) {
            this.println(this.formatMessage("<", this.tracedClass.getName(), methodName, ret, null));
        }
    }

    @Override
    public void exit(String methodName) {
        if (this.traceEnabled) {
            this.println(this.formatMessage("<", this.tracedClass.getName(), methodName, null, null));
        }
    }

    @Override
    public void exit(String methodName, Throwable th) {
        if (this.traceEnabled) {
            this.println(this.formatMessage("<", this.tracedClass.getName(), methodName, th, null));
        }
    }

    @Override
    public void event(String methodName, Object thiz, Object[] args) {
        if (this.traceEnabled) {
            this.println(this.formatMessage("-", this.tracedClass.getName(), methodName, thiz, args));
        }
    }

    @Override
    public void event(String methodName) {
        if (this.traceEnabled) {
            this.println(this.formatMessage("-", this.tracedClass.getName(), methodName, null, null));
        }
    }

    @Override
    public void debug(String message) {
        this.println(this.formatMessage("?", message, null));
    }

    @Override
    public void info(String message) {
        this.println(this.formatMessage("I", message, null));
    }

    @Override
    public void warn(String message, Throwable th) {
        this.println(this.formatMessage("W", message, th));
        if (th != null) {
            th.printStackTrace();
        }
    }

    @Override
    public void error(String message, Throwable th) {
        this.println(this.formatMessage("E", message, th));
        if (th != null) {
            th.printStackTrace();
        }
    }

    @Override
    public void fatal(String message, Throwable th) {
        this.println(this.formatMessage("X", message, th));
        if (th != null) {
            th.printStackTrace();
        }
    }

    protected void println(String s) {
        this.print.println(s);
    }

    public void setPrintStream(PrintStream printStream) {
        this.print = printStream;
    }
}

