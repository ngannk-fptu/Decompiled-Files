/*
 * Decompiled with CFR 0.152.
 */
package groovy.text;

public class TemplateExecutionException
extends Exception {
    private int lineNumber;

    public TemplateExecutionException(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public TemplateExecutionException(int lineNumber, String message) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public TemplateExecutionException(int lineNumber, String message, Throwable cause) {
        super(message, cause);
        this.lineNumber = lineNumber;
    }

    public TemplateExecutionException(int lineNumber, Throwable cause) {
        super(cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }
}

