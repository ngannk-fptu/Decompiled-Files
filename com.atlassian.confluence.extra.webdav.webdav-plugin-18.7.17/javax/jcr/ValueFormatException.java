/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class ValueFormatException
extends RepositoryException {
    public ValueFormatException() {
    }

    public ValueFormatException(String message) {
        super(message);
    }

    public ValueFormatException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ValueFormatException(Throwable rootCause) {
        super(rootCause);
    }
}

