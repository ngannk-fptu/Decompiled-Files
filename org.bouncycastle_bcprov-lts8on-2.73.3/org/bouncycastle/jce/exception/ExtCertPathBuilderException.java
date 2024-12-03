/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.exception;

import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import org.bouncycastle.jce.exception.ExtException;

public class ExtCertPathBuilderException
extends CertPathBuilderException
implements ExtException {
    private Throwable cause;

    public ExtCertPathBuilderException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public ExtCertPathBuilderException(String msg, Throwable cause, CertPath certPath, int index) {
        super(msg, cause);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

