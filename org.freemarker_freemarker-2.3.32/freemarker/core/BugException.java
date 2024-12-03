/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

public class BugException
extends RuntimeException {
    private static final String COMMON_MESSAGE = "A bug was detected in FreeMarker; please report it with stack-trace";

    public BugException() {
        this((Throwable)null);
    }

    public BugException(String message) {
        this(message, null);
    }

    public BugException(Throwable cause) {
        super(COMMON_MESSAGE, cause);
    }

    public BugException(String message, Throwable cause) {
        super("A bug was detected in FreeMarker; please report it with stack-trace: " + message, cause);
    }

    public BugException(int value) {
        this(String.valueOf(value));
    }
}

