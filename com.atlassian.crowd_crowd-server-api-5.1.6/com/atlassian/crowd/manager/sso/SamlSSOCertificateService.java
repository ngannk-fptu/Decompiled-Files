/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.sso;

import java.security.cert.X509Certificate;
import java.util.Optional;

public interface SamlSSOCertificateService {
    public long generateCertificateAndPrivateKey();

    public Optional<X509Certificate> getCertificate(long var1);
}

