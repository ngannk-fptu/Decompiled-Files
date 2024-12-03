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

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null && this.cause != null) {
            return this.cause.getMessage();
        }
        return message;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
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

