/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class StackableException
extends Exception {
    private Exception parent;

    public StackableException() {
    }

    public StackableException(String message, Exception ex) {
        super(message);
        this.parent = ex;
    }

    public StackableException(String message) {
        super(message);
    }

    public Exception getParent() {
        return this.parent;
    }

    public void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);
        if (this.getParent() != null) {
            stream.println("ParentException: ");
            this.getParent().printStackTrace(stream);
        }
    }

    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (this.getParent() != null) {
            writer.println("ParentException: ");
            this.getParent().printStackTrace(writer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace() {
        PrintStream printStream = System.err;
        synchronized (printStream) {
            this.printStackTrace(System.err);
        }
    }
}

