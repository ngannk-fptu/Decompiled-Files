/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class LoginException
extends RepositoryException {
    public LoginException() {
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public LoginException(Throwable rootCause) {
        super(rootCause);
    }
}

