/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.messages;

import java.io.PrintWriter;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.syntax.CSTNode;

public class LocatedMessage
extends SimpleMessage {
    protected CSTNode context;

    public LocatedMessage(String message, CSTNode context, SourceUnit source) {
        super(message, source);
        this.context = context;
    }

    public LocatedMessage(String message, Object data, CSTNode context, SourceUnit source) {
        super(message, data, source);
        this.context = context;
    }

    @Override
    public void write(PrintWriter writer, Janitor janitor) {
        if (this.owner instanceof SourceUnit) {
            int column;
            SourceUnit source = (SourceUnit)this.owner;
            String name = source.getName();
            int line = this.context.getStartLine();
            String sample = source.getSample(line, column = this.context.getStartColumn(), janitor);
            if (sample != null) {
                writer.println(source.getSample(line, column, janitor));
            }
            writer.println(name + ": " + line + ": " + this.message);
            writer.println("");
        } else {
            writer.println("<No Relevant Source>: " + this.message);
            writer.println("");
        }
    }
}

