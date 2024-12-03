/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.messages;

import java.io.PrintWriter;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.syntax.SyntaxException;

public class SyntaxErrorMessage
extends Message {
    protected SyntaxException cause;
    protected SourceUnit source;

    public SyntaxErrorMessage(SyntaxException cause, SourceUnit source) {
        this.cause = cause;
        this.source = source;
        cause.setSourceLocator(source.getName());
    }

    public SyntaxException getCause() {
        return this.cause;
    }

    @Override
    public void write(PrintWriter output, Janitor janitor) {
        String name = this.source.getName();
        int line = this.getCause().getStartLine();
        int column = this.getCause().getStartColumn();
        String sample = this.source.getSample(line, column, janitor);
        output.print(name + ": " + line + ": " + this.getCause().getMessage());
        if (sample != null) {
            output.println();
            output.print(sample);
            output.println();
        }
    }
}

