/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class InvalidLifecycleTransitionException
extends RepositoryException {
    public InvalidLifecycleTransitionException() {
    }

    public InvalidLifecycleTransitionException(String message) {
        super(message);
    }

    public InvalidLifecycleTransitionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public InvalidLifecycleTransitionException(Throwable rootCause) {
        super(rootCause);
    }
}

