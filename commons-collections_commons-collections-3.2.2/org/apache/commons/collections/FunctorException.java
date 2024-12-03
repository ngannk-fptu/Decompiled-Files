/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.io.PrintStream;
import java.io.PrintWriter;

public class FunctorException
extends RuntimeException {
    private static final boolean JDK_SUPPORTS_NESTED;
    private final Throwable rootCause;
    static /* synthetic */ Class class$java$lang$Throwable;

    public FunctorException() {
        this.rootCause = null;
    }

    public FunctorException(String msg) {
        super(msg);
        this.rootCause = null;
    }

    public FunctorException(Throwable rootCause) {
        super(rootCause == null ? null : rootCause.getMessage());
        this.rootCause = rootCause;
    }

    public FunctorException(String msg, Throwable rootCause) {
        super(msg);
        this.rootCause = rootCause;
    }

    public Throwable getCause() {
        return this.rootCause;
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream out) {
        PrintStream printStream = out;
        synchronized (printStream) {
            PrintWriter pw = new PrintWriter(out, false);
            this.printStackTrace(pw);
            pw.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter out) {
        PrintWriter printWriter = out;
        synchronized (printWriter) {
            super.printStackTrace(out);
            if (this.rootCause != null && !JDK_SUPPORTS_NESTED) {
                out.print("Caused by: ");
                this.rootCause.printStackTrace(out);
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        boolean flag = false;
        try {
            (class$java$lang$Throwable == null ? (class$java$lang$Throwable = FunctorException.class$("java.lang.Throwable")) : class$java$lang$Throwable).getDeclaredMethod("getCause", new Class[0]);
            flag = true;
        }
        catch (NoSuchMethodException ex) {
            flag = false;
        }
        JDK_SUPPORTS_NESTED = flag;
    }
}

