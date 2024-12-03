/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import org.apache.bcel.verifier.exc.VerifierConstraintViolatedException;

public abstract class VerificationException
extends VerifierConstraintViolatedException {
    private static final long serialVersionUID = 8012776320318623652L;

    VerificationException() {
    }

    VerificationException(String message) {
        super(message);
    }

    VerificationException(String message, Throwable initCause) {
        super(message, initCause);
    }
}

