/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertPathValidatorException;

class CRLNotFoundException
extends CertPathValidatorException {
    CRLNotFoundException(String message) {
        super(message);
    }

    public CRLNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

