/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon;

public class DaemonInitException
extends Exception {
    private static final long serialVersionUID = 5665891535067213551L;

    public DaemonInitException(String message) {
        super(message);
    }

    public DaemonInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getMessageWithCause() {
        Throwable cause = this.getCause();
        return this.getMessage() + (cause == null ? "" : ": " + cause.getMessage());
    }
}

