/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import org.apache.bcel.verifier.exc.ClassConstraintException;

public class LocalVariableInfoInconsistentException
extends ClassConstraintException {
    private static final long serialVersionUID = -2833180480144304190L;

    public LocalVariableInfoInconsistentException() {
    }

    public LocalVariableInfoInconsistentException(String message) {
        super(message);
    }
}

