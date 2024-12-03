/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

abstract class CertHelper {
    CertHelper() {
    }

    public CertificateFactory getCertificateFactory(String type) throws NoSuchProviderException, CertificateException {
        return this.createCertificateFactory(type);
    }

    protected abstract CertificateFactory createCertificateFactory(String var1) throws CertificateException, NoSuchProviderException;
}

