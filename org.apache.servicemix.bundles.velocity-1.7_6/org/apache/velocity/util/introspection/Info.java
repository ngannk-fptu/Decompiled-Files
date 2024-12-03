/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

public class Info {
    private int line;
    private int column;
    private String templateName;

    public Info(String source, int line, int column) {
        this.templateName = source;
        this.line = line;
        this.column = column;
    }

    public Info(Node node) {
        this(node.getTemplateName(), node.getLine(), node.getColumn());
    }

    private Info() {
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String toString() {
        return Log.formatFileString(this.getTemplateName(), this.getLine(), this.getColumn());
    }
}

