/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.sso;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class KeyCertificatePair {
    private final PrivateKey privateKey;
    private final X509Certificate certificate;

    public KeyCertificatePair(PrivateKey privateKey, X509Certificate certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public X509Certificate getCertificate() {
        return this.certificate;
    }
}

