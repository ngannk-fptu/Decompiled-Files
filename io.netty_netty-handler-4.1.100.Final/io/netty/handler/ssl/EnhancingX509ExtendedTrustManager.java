/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.SuppressJava6Requirement
 */
package io.netty.handler.ssl;

import io.netty.util.internal.SuppressJava6Requirement;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
final class EnhancingX509ExtendedTrustManager
extends X509ExtendedTrustManager {
    private final X509ExtendedTrustManager wrapped;

    EnhancingX509ExtendedTrustManager(X509TrustManager wrapped) {
        this.wrapped = (X509ExtendedTrustManager)wrapped;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        this.wrapped.checkClientTrusted(chain, authType, socket);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        try {
            this.wrapped.checkServerTrusted(chain, authType, socket);
        }
        catch (CertificateException e) {
            EnhancingX509ExtendedTrustManager.throwEnhancedCertificateException(chain, e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        this.wrapped.checkClientTrusted(chain, authType, engine);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        try {
            this.wrapped.checkServerTrusted(chain, authType, engine);
        }
        catch (CertificateException e) {
            EnhancingX509ExtendedTrustManager.throwEnhancedCertificateException(chain, e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        this.wrapped.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            this.wrapped.checkServerTrusted(chain, authType);
        }
        catch (CertificateException e) {
            EnhancingX509ExtendedTrustManager.throwEnhancedCertificateException(chain, e);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.wrapped.getAcceptedIssuers();
    }

    private static void throwEnhancedCertificateException(X509Certificate[] chain, CertificateException e) throws CertificateException {
        String message = e.getMessage();
        if (message != null && e.getMessage().startsWith("No subject alternative DNS name matching")) {
            StringBuilder names = new StringBuilder(64);
            for (int i = 0; i < chain.length; ++i) {
                X509Certificate cert = chain[i];
                Collection<List<?>> collection = cert.getSubjectAlternativeNames();
                if (collection == null) continue;
                for (List<?> altNames : collection) {
                    if (altNames.size() < 2 || (Integer)altNames.get(0) != 2) continue;
                    names.append((String)altNames.get(1)).append(",");
                }
            }
            if (names.length() != 0) {
                names.setLength(names.length() - 1);
                throw new CertificateException(message + " Subject alternative DNS names in the certificate chain of " + chain.length + " certificate(s): " + names, e);
            }
        }
        throw e;
    }
}

