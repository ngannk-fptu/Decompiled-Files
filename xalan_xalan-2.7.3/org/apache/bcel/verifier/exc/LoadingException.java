/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import org.apache.bcel.verifier.exc.VerifierConstraintViolatedException;

public class LoadingException
extends VerifierConstraintViolatedException {
    private static final long serialVersionUID = -7911901533049018823L;

    public LoadingException() {
    }

    public LoadingException(String message) {
        super(message);
    }
}

