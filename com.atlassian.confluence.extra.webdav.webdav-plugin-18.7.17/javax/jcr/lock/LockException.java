/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.lock;

import javax.jcr.RepositoryException;

public class LockException
extends RepositoryException {
    private final String failureNodePath;

    public LockException() {
        this.failureNodePath = null;
    }

    public LockException(String message) {
        super(message);
        this.failureNodePath = null;
    }

    public LockException(Throwable rootCause) {
        super(rootCause);
        this.failureNodePath = null;
    }

    public LockException(String message, Throwable rootCause) {
        super(message, rootCause);
        this.failureNodePath = null;
    }

    public LockException(String message, Throwable rootCause, String failureNodePath) {
        super(message, rootCause);
        this.failureNodePath = failureNodePath;
    }

    public String getFailureNodePath() {
        return this.failureNodePath;
    }
}

