/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni;

public class XNIException
extends RuntimeException {
    private static final long serialVersionUID = 7447489736019161121L;
    private Exception fException_ = this;

    public XNIException(String message) {
        super(message);
    }

    public XNIException(Exception exception) {
        super(exception.getMessage());
        this.fException_ = exception;
    }

    public XNIException(String message, Exception exception) {
        super(message);
        this.fException_ = exception;
    }

    public Exception getException() {
        return this.fException_ != this ? this.fException_ : null;
    }

    @Override
    public synchronized Throwable initCause(Throwable throwable) {
        if (this.fException_ != this) {
            throw new IllegalStateException();
        }
        if (throwable == this) {
            throw new IllegalArgumentException();
        }
        this.fException_ = (Exception)throwable;
        return this;
    }

    @Override
    public Throwable getCause() {
        return this.getException();
    }
}

