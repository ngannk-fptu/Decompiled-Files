/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import org.apache.bcel.verifier.exc.VerificationException;

public class ClassConstraintException
extends VerificationException {
    private static final long serialVersionUID = -4745598983569128296L;

    public ClassConstraintException() {
    }

    public ClassConstraintException(String message) {
        super(message);
    }

    public ClassConstraintException(String message, Throwable initCause) {
        super(message, initCause);
    }
}

