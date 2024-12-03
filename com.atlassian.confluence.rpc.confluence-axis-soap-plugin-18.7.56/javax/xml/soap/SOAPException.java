/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

public class SOAPException
extends Exception {
    private Throwable cause;

    public SOAPException() {
        this.cause = null;
    }

    public SOAPException(String reason) {
        super(reason);
        this.cause = null;
    }

    public SOAPException(String reason, Throwable cause) {
        super(reason);
        this.initCause(cause);
    }

    public SOAPException(Throwable cause) {
        super(cause.toString());
        this.initCause(cause);
    }

    public String getMessage() {
        String s = super.getMessage();
        if (s == null && this.cause != null) {
            return this.cause.getMessage();
        }
        return s;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public synchronized Throwable initCause(Throwable cause) {
        if (this.cause != null) {
            throw new IllegalStateException("Can't override cause");
        }
        if (cause == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.cause = cause;
        return this;
    }
}

