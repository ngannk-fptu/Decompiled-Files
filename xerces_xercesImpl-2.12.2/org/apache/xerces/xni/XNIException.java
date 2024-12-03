/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni;

public class XNIException
extends RuntimeException {
    static final long serialVersionUID = 9019819772686063775L;
    private Exception fException = this;

    public XNIException(String string) {
        super(string);
    }

    public XNIException(Exception exception) {
        super(exception.getMessage());
        this.fException = exception;
    }

    public XNIException(String string, Exception exception) {
        super(string);
        this.fException = exception;
    }

    public Exception getException() {
        return this.fException != this ? this.fException : null;
    }

    @Override
    public synchronized Throwable initCause(Throwable throwable) {
        if (this.fException != this) {
            throw new IllegalStateException();
        }
        if (throwable == this) {
            throw new IllegalArgumentException();
        }
        this.fException = (Exception)throwable;
        return this;
    }

    @Override
    public Throwable getCause() {
        return this.getException();
    }
}

