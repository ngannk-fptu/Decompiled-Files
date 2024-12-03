/*
 * Decompiled with CFR 0.152.
 */
package groovy.text;

public class TemplateParseException
extends RuntimeException {
    private int lineNumber;
    private int column;

    public TemplateParseException(int lineNumber, int column) {
        this.lineNumber = lineNumber;
        this.column = column;
    }

    public TemplateParseException(String message, int lineNumber, int column) {
        super(message);
        this.lineNumber = lineNumber;
        this.column = column;
    }

    public TemplateParseException(String message, Throwable cause, int lineNumber, int column) {
        super(message, cause);
        this.lineNumber = lineNumber;
        this.column = column;
    }

    public TemplateParseException(Throwable t, int lineNumber, int column) {
        super(t);
        this.lineNumber = lineNumber;
        this.column = column;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumn() {
        return this.column;
    }
}

