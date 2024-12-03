/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface TrustStrategy {
    public boolean isTrusted(X509Certificate[] var1, String var2) throws CertificateException;
}

