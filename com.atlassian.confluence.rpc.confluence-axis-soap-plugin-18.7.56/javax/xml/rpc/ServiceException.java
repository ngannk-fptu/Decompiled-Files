/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc;

public class ServiceException
extends Exception {
    Throwable cause;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public ServiceException(Throwable cause) {
        super(cause == null ? null : cause.toString());
        this.cause = cause;
    }

    public Throwable getLinkedCause() {
        return this.cause;
    }
}

