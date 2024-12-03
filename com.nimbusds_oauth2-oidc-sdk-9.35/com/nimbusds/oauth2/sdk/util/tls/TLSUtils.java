/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util.tls;

import com.nimbusds.oauth2.sdk.util.tls.TLSVersion;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public final class TLSUtils {
    public static SSLSocketFactory createSSLSocketFactory(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        return TLSUtils.createSSLSocketFactory(trustStore, null, null, TLSVersion.TLS_1_3);
    }

    public static SSLSocketFactory createSSLSocketFactory(KeyStore trustStore, TLSVersion tlsVersion) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        return TLSUtils.createSSLSocketFactory(trustStore, null, null, tlsVersion);
    }

    public static SSLSocketFactory createSSLSocketFactory(KeyStore trustStore, KeyStore keyStore, char[] keyPw, TLSVersion tlsVersion) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        SSLContext sslContext = SSLContext.getInstance(tlsVersion.toString());
        TrustManager[] trustManagers = null;
        if (trustStore != null) {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
            tmf.init(trustStore);
            trustManagers = tmf.getTrustManagers();
        }
        KeyManager[] keyManagers = null;
        if (keyStore != null) {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
            kmf.init(keyStore, keyPw);
            keyManagers = kmf.getKeyManagers();
        }
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext.getSocketFactory();
    }

    private TLSUtils() {
    }
}

