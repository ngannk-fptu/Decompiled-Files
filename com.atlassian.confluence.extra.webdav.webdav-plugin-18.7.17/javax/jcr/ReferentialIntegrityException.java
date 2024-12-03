/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class ReferentialIntegrityException
extends RepositoryException {
    public ReferentialIntegrityException() {
    }

    public ReferentialIntegrityException(String message) {
        super(message);
    }

    public ReferentialIntegrityException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ReferentialIntegrityException(Throwable rootCause) {
        super(rootCause);
    }
}

