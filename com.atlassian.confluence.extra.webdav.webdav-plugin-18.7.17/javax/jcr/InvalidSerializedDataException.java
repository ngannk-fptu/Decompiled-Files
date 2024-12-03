/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class InvalidSerializedDataException
extends RepositoryException {
    public InvalidSerializedDataException() {
    }

    public InvalidSerializedDataException(String message) {
        super(message);
    }

    public InvalidSerializedDataException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public InvalidSerializedDataException(Throwable rootCause) {
        super(rootCause);
    }
}

