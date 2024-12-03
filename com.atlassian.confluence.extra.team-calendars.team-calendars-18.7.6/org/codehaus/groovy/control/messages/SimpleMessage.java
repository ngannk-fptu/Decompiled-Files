/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.messages;

import java.io.PrintWriter;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.Message;

public class SimpleMessage
extends Message {
    protected String message;
    protected Object data;
    protected ProcessingUnit owner;

    public SimpleMessage(String message, ProcessingUnit source) {
        this(message, null, source);
    }

    public SimpleMessage(String message, Object data, ProcessingUnit source) {
        this.message = message;
        this.data = null;
        this.owner = source;
    }

    @Override
    public void write(PrintWriter writer, Janitor janitor) {
        if (this.owner instanceof SourceUnit) {
            String name = ((SourceUnit)this.owner).getName();
            writer.println("" + name + ": " + this.message);
        } else {
            writer.println(this.message);
        }
    }

    public String getMessage() {
        return this.message;
    }
}

