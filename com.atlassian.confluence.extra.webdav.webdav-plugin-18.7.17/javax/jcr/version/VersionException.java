/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

import javax.jcr.RepositoryException;

public class VersionException
extends RepositoryException {
    public VersionException() {
    }

    public VersionException(String message) {
        super(message);
    }

    public VersionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public VersionException(Throwable rootCause) {
        super(rootCause);
    }
}

