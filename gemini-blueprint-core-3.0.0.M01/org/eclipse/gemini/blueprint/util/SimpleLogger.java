/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.eclipse.gemini.blueprint.util;

import java.io.PrintStream;
import org.apache.commons.logging.Log;

class SimpleLogger
implements Log {
    private PrintStream out = System.out;
    private PrintStream err = System.err;

    SimpleLogger() {
    }

    public void debug(Object message) {
        this.out.println(message);
    }

    public void debug(Object message, Throwable th) {
        this.out.println(message);
        th.printStackTrace(this.out);
    }

    public void error(Object message) {
        this.err.println(message);
    }

    public void error(Object message, Throwable th) {
        this.err.println(message);
        th.printStackTrace(this.err);
    }

    public void fatal(Object message) {
        this.err.println(message);
    }

    public void fatal(Object message, Throwable th) {
        this.err.println(message);
        th.printStackTrace(this.err);
    }

    public void info(Object message) {
        this.out.println(message);
    }

    public void info(Object message, Throwable th) {
        this.out.println(message);
        th.printStackTrace(this.out);
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public boolean isFatalEnabled() {
        return true;
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void trace(Object message) {
        this.out.println(message);
    }

    public void trace(Object message, Throwable th) {
        this.out.println(message);
        th.printStackTrace(this.out);
    }

    public void warn(Object message) {
        this.out.println(message);
    }

    public void warn(Object message, Throwable th) {
        this.out.println(message);
        th.printStackTrace(this.out);
    }
}

