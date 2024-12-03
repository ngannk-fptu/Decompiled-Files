/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

import javax.jcr.version.VersionException;

public class LabelExistsVersionException
extends VersionException {
    public LabelExistsVersionException() {
    }

    public LabelExistsVersionException(String message) {
        super(message);
    }

    public LabelExistsVersionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public LabelExistsVersionException(Throwable rootCause) {
        super(rootCause);
    }
}

