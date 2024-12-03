/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http11;

public class HeadersTooLargeException
extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public HeadersTooLargeException() {
    }

    public HeadersTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeadersTooLargeException(String s) {
        super(s);
    }

    public HeadersTooLargeException(Throwable cause) {
        super(cause);
    }
}

