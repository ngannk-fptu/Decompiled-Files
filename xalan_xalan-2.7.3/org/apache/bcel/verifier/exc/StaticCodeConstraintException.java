/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import org.apache.bcel.verifier.exc.CodeConstraintException;

public abstract class StaticCodeConstraintException
extends CodeConstraintException {
    private static final long serialVersionUID = 3858523065007725128L;

    public StaticCodeConstraintException(String message) {
        super(message);
    }
}

