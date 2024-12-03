/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedActionException;

public class ImagingException
extends RuntimeException {
    private Throwable cause = null;

    public ImagingException() {
    }

    public ImagingException(String message) {
        super(message);
    }

    public ImagingException(Throwable cause) {
        this.cause = cause;
    }

    public ImagingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Throwable getRootCause() {
        Throwable rootCause = this.cause;
        Throwable atop = this;
        while (rootCause != atop && rootCause != null) {
            try {
                atop = rootCause;
                Method getCause = rootCause.getClass().getMethod("getCause", null);
                rootCause = (Throwable)getCause.invoke((Object)rootCause, null);
            }
            catch (Exception e) {
                if (rootCause instanceof InvocationTargetException) {
                    rootCause = ((InvocationTargetException)rootCause).getTargetException();
                    continue;
                }
                if (rootCause instanceof PrivilegedActionException) {
                    rootCause = ((PrivilegedActionException)rootCause).getException();
                    continue;
                }
                rootCause = atop;
            }
            finally {
                if (rootCause != null) continue;
                rootCause = atop;
            }
        }
        return rootCause;
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream s) {
        PrintStream printStream = s;
        synchronized (printStream) {
            super.printStackTrace(s);
            boolean is14 = false;
            try {
                String version = System.getProperty("java.version");
                is14 = version.indexOf("1.4") >= 0;
            }
            catch (Exception e) {
                // empty catch block
            }
            if (!is14 && this.cause != null) {
                s.println("Caused by:");
                this.cause.printStackTrace(s);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter s) {
        PrintWriter printWriter = s;
        synchronized (printWriter) {
            super.printStackTrace(s);
            boolean is14 = false;
            try {
                String version = System.getProperty("java.version");
                is14 = version.indexOf("1.4") >= 0;
            }
            catch (Exception e) {
                // empty catch block
            }
            if (!is14 && this.cause != null) {
                s.println("Caused by:");
                this.cause.printStackTrace(s);
            }
        }
    }
}

