/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net.jsse;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.tomcat.util.net.SSLContext;

class JSSESSLContext
implements SSLContext {
    private javax.net.ssl.SSLContext context;
    private KeyManager[] kms;
    private TrustManager[] tms;

    JSSESSLContext(String protocol) throws NoSuchAlgorithmException {
        this.context = javax.net.ssl.SSLContext.getInstance(protocol);
    }

    @Override
    public void init(KeyManager[] kms, TrustManager[] tms, SecureRandom sr) throws KeyManagementException {
        this.kms = kms;
        this.tms = tms;
        this.context.init(kms, tms, sr);
    }

    @Override
    public void destroy() {
    }

    @Override
    public SSLSessionContext getServerSessionContext() {
        return this.context.getServerSessionContext();
    }

    @Override
    public SSLEngine createSSLEngine() {
        return this.context.createSSLEngine();
    }

    @Override
    public SSLServerSocketFactory getServerSocketFactory() {
        return this.context.getServerSocketFactory();
    }

    @Override
    public SSLParameters getSupportedSSLParameters() {
        return this.context.getSupportedSSLParameters();
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] result = null;
        if (this.kms != null) {
            for (int i = 0; i < this.kms.length && result == null; ++i) {
                if (!(this.kms[i] instanceof X509KeyManager)) continue;
                result = ((X509KeyManager)this.kms[i]).getCertificateChain(alias);
            }
        }
        return result;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        HashSet<X509Certificate> certs = new HashSet<X509Certificate>();
        if (this.tms != null) {
            for (TrustManager tm : this.tms) {
                if (!(tm instanceof X509TrustManager)) continue;
                X509Certificate[] accepted = ((X509TrustManager)tm).getAcceptedIssuers();
                certs.addAll(Arrays.asList(accepted));
            }
        }
        return certs.toArray(new X509Certificate[0]);
    }
}

