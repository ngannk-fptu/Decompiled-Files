/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

public class InvalidSyntaxException
extends Exception {
    static final long serialVersionUID = -4295194420816491875L;
    private final String filter;

    public InvalidSyntaxException(String msg, String filter) {
        super(InvalidSyntaxException.message(msg, filter));
        this.filter = filter;
    }

    public InvalidSyntaxException(String msg, String filter, Throwable cause) {
        super(InvalidSyntaxException.message(msg, filter), cause);
        this.filter = filter;
    }

    private static String message(String msg, String filter) {
        if (msg == null || filter == null || msg.indexOf(filter) >= 0) {
            return msg;
        }
        return msg + ": " + filter;
    }

    public String getFilter() {
        return this.filter;
    }

    @Override
    public Throwable getCause() {
        return super.getCause();
    }

    @Override
    public Throwable initCause(Throwable cause) {
        return super.initCause(cause);
    }
}

