/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix;

public class PKIXNameConstraintValidatorException
extends Exception {
    private Throwable cause;

    public PKIXNameConstraintValidatorException(String msg) {
        super(msg);
    }

    public PKIXNameConstraintValidatorException(String msg, Throwable e) {
        super(msg);
        this.cause = e;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

