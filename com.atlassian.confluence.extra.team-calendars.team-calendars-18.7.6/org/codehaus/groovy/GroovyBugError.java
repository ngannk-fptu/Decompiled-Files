/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy;

public class GroovyBugError
extends AssertionError {
    private String message;
    private final Exception exception;

    public GroovyBugError(String message) {
        this(message, null);
    }

    public GroovyBugError(Exception exception) {
        this(null, exception);
    }

    public GroovyBugError(String msg, Exception exception) {
        this.exception = exception;
        this.message = msg;
    }

    public String toString() {
        return this.getMessage();
    }

    public String getMessage() {
        if (this.message != null) {
            return "BUG! " + this.message;
        }
        return "BUG! UNCAUGHT EXCEPTION: " + this.exception.getMessage();
    }

    public Throwable getCause() {
        return this.exception;
    }

    public String getBugText() {
        if (this.message != null) {
            return this.message;
        }
        return this.exception.getMessage();
    }

    public void setBugText(String msg) {
        this.message = msg;
    }
}

