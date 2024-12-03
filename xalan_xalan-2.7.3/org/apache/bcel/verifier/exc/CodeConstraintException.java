/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import org.apache.bcel.verifier.exc.VerificationException;

public abstract class CodeConstraintException
extends VerificationException {
    private static final long serialVersionUID = -7265388214714996640L;

    CodeConstraintException() {
    }

    CodeConstraintException(String message) {
        super(message);
    }
}

