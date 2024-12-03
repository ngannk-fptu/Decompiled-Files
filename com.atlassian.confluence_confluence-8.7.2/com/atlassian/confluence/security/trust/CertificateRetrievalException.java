/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

public class CertificateRetrievalException
extends Exception {
    public CertificateRetrievalException() {
    }

    public CertificateRetrievalException(String message) {
        super(message);
    }

    public CertificateRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateRetrievalException(Throwable cause) {
        super(cause);
    }
}

