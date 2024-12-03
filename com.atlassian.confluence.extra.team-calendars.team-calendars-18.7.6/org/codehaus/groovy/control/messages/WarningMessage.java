/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.messages;

import java.io.PrintWriter;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.LocatedMessage;
import org.codehaus.groovy.syntax.CSTNode;

public class WarningMessage
extends LocatedMessage {
    public static final int NONE = 0;
    public static final int LIKELY_ERRORS = 1;
    public static final int POSSIBLE_ERRORS = 2;
    public static final int PARANOIA = 3;
    private int importance;

    public static boolean isRelevant(int actual, int limit) {
        return actual <= limit;
    }

    public boolean isRelevant(int importance) {
        return WarningMessage.isRelevant(this.importance, importance);
    }

    public WarningMessage(int importance, String message, CSTNode context, SourceUnit owner) {
        super(message, context, owner);
        this.importance = importance;
    }

    public WarningMessage(int importance, String message, Object data, CSTNode context, SourceUnit owner) {
        super(message, data, context, owner);
        this.importance = importance;
    }

    @Override
    public void write(PrintWriter writer, Janitor janitor) {
        writer.print("warning: ");
        super.write(writer, janitor);
    }
}

