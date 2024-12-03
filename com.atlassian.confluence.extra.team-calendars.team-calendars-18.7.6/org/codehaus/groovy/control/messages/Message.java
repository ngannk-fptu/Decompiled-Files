/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.messages;

import java.io.PrintWriter;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;

public abstract class Message {
    public abstract void write(PrintWriter var1, Janitor var2);

    public final void write(PrintWriter writer) {
        this.write(writer, null);
    }

    public static Message create(String text, ProcessingUnit owner) {
        return new SimpleMessage(text, owner);
    }

    public static Message create(String text, Object data, ProcessingUnit owner) {
        return new SimpleMessage(text, data, owner);
    }

    public static Message create(SyntaxException error, SourceUnit owner) {
        return new SyntaxErrorMessage(error, owner);
    }
}

