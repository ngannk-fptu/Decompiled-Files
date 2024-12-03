/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.el;

public class ELException
extends Exception {
    private static final long serialVersionUID = 1L;

    public ELException() {
    }

    public ELException(String pMessage) {
        super(pMessage);
    }

    public ELException(Throwable pRootCause) {
        super(pRootCause);
    }

    public ELException(String pMessage, Throwable pRootCause) {
        super(pMessage, pRootCause);
    }

    public Throwable getRootCause() {
        return this.getCause();
    }
}

