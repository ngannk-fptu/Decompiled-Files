/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import org.apache.bcel.verifier.exc.CodeConstraintException;

public class StructuralCodeConstraintException
extends CodeConstraintException {
    private static final long serialVersionUID = 5406842000007181420L;

    public StructuralCodeConstraintException() {
    }

    public StructuralCodeConstraintException(String message) {
        super(message);
    }
}

