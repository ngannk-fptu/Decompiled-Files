/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.bouncycastle.cert.jcajce.CertHelper;

class NamedCertHelper
extends CertHelper {
    private final String providerName;

    NamedCertHelper(String providerName) {
        this.providerName = providerName;
    }

    @Override
    protected CertificateFactory createCertificateFactory(String type) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(type, this.providerName);
    }
}

