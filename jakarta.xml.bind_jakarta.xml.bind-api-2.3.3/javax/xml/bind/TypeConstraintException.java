/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.bind;

import java.io.PrintStream;

public class TypeConstraintException
extends RuntimeException {
    private String errorCode;
    private volatile Throwable linkedException;
    static final long serialVersionUID = -3059799699420143848L;

    public TypeConstraintException(String message) {
        this(message, null, null);
    }

    public TypeConstraintException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    public TypeConstraintException(Throwable exception) {
        this(null, null, exception);
    }

    public TypeConstraintException(String message, Throwable exception) {
        this(message, null, exception);
    }

    public TypeConstraintException(String message, String errorCode, Throwable exception) {
        super(message);
        this.errorCode = errorCode;
        this.linkedException = exception;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public Throwable getLinkedException() {
        return this.linkedException;
    }

    public void setLinkedException(Throwable exception) {
        this.linkedException = exception;
    }

    @Override
    public String toString() {
        return this.linkedException == null ? super.toString() : super.toString() + "\n - with linked exception:\n[" + this.linkedException.toString() + "]";
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if (this.linkedException != null) {
            this.linkedException.printStackTrace(s);
            s.println("--------------- linked to ------------------");
        }
        super.printStackTrace(s);
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
}

