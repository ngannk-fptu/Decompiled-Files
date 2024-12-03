/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class MergeException
extends RepositoryException {
    public MergeException() {
    }

    public MergeException(String message) {
        super(message);
    }

    public MergeException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public MergeException(Throwable rootCause) {
        super(rootCause);
    }
}

