/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.security.AccessControlException;

public class AccessDeniedException
extends AccessControlException {
    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public AccessDeniedException(Throwable rootCause) {
        super(rootCause);
    }
}

