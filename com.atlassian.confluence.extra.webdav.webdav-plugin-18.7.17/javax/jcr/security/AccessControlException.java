/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.security;

import javax.jcr.RepositoryException;

public class AccessControlException
extends RepositoryException {
    public AccessControlException() {
    }

    public AccessControlException(String message) {
        super(message);
    }

    public AccessControlException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public AccessControlException(Throwable rootCause) {
        super(rootCause);
    }
}

