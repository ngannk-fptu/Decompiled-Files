/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import groovy.lang.GroovyRuntimeException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.codehaus.groovy.GroovyExceptionInterface;
import org.codehaus.groovy.control.CompilationFailedException;

public class ErrorReporter {
    private Throwable base = null;
    private boolean debug = false;
    private Object output = null;

    public ErrorReporter(Throwable e) {
        this.base = e;
    }

    public ErrorReporter(Throwable e, boolean debug) {
        this.base = e;
        this.debug = debug;
    }

    public void write(PrintStream stream) {
        this.output = stream;
        this.dispatch(this.base, false);
        stream.flush();
    }

    public void write(PrintWriter writer) {
        this.output = writer;
        this.dispatch(this.base, false);
        writer.flush();
    }

    protected void dispatch(Throwable object, boolean child) {
        if (object instanceof CompilationFailedException) {
            this.report((CompilationFailedException)object, child);
        } else if (object instanceof GroovyExceptionInterface) {
            this.report((GroovyExceptionInterface)((Object)object), child);
        } else if (object instanceof GroovyRuntimeException) {
            this.report((GroovyRuntimeException)object, child);
        } else if (object instanceof Exception) {
            this.report((Exception)object, child);
        } else {
            this.report(object, child);
        }
    }

    protected void report(CompilationFailedException e, boolean child) {
        this.println(e.toString());
        this.stacktrace(e, false);
    }

    protected void report(GroovyExceptionInterface e, boolean child) {
        this.println(((Exception)((Object)e)).getMessage());
        this.stacktrace((Exception)((Object)e), false);
    }

    protected void report(Exception e, boolean child) {
        this.println(e.getMessage());
        this.stacktrace(e, false);
    }

    protected void report(Throwable e, boolean child) {
        this.println(">>> a serious error occurred: " + e.getMessage());
        this.stacktrace(e, true);
    }

    protected void println(String line) {
        if (this.output instanceof PrintStream) {
            ((PrintStream)this.output).println(line);
        } else {
            ((PrintWriter)this.output).println(line);
        }
    }

    protected void println(StringBuffer line) {
        if (this.output instanceof PrintStream) {
            ((PrintStream)this.output).println(line);
        } else {
            ((PrintWriter)this.output).println(line);
        }
    }

    protected void stacktrace(Throwable e, boolean always) {
        if (this.debug || always) {
            this.println(">>> stacktrace:");
            if (this.output instanceof PrintStream) {
                e.printStackTrace((PrintStream)this.output);
            } else {
                e.printStackTrace((PrintWriter)this.output);
            }
        }
    }
}

