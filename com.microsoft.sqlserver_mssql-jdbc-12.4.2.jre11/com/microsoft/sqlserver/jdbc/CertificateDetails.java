/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.security.Key;
import java.security.cert.X509Certificate;

class CertificateDetails {
    X509Certificate certificate;
    Key privateKey;

    CertificateDetails(X509Certificate certificate, Key privateKey) {
        this.certificate = certificate;
        this.privateKey = privateKey;
    }
}

