/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

import javax.jcr.version.VersionException;

public class ActivityViolationException
extends VersionException {
    public ActivityViolationException() {
    }

    public ActivityViolationException(String message) {
        super(message);
    }

    public ActivityViolationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ActivityViolationException(Throwable rootCause) {
        super(rootCause);
    }
}

