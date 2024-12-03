/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.messages;

import java.io.PrintWriter;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.messages.Message;

public class ExceptionMessage
extends Message {
    protected boolean verbose = true;
    private Exception cause = null;
    ProcessingUnit owner = null;

    public ExceptionMessage(Exception cause, boolean v, ProcessingUnit owner) {
        this.verbose = v;
        this.cause = cause;
        this.owner = owner;
    }

    public Exception getCause() {
        return this.cause;
    }

    @Override
    public void write(PrintWriter output, Janitor janitor) {
        String description = "General error during " + this.owner.getPhaseDescription() + ": ";
        String message = this.cause.getMessage();
        if (message != null) {
            output.println(description + message);
        } else {
            output.println(description + this.cause);
        }
        output.println();
        this.cause.printStackTrace(output);
    }
}

