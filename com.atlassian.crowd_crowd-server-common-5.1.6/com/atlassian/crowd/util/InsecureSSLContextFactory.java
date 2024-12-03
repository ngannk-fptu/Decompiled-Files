/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.crowd.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class InsecureSSLContextFactory
extends SSLSocketFactory {
    private SSLSocketFactory delegate;

    public InsecureSSLContextFactory() {
        try {
            InsecureX509TrustManager trustManager = new InsecureX509TrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            this.delegate = sslContext.getSocketFactory();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return this.delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
        try {
            return this.delegate.createSocket(arg0, arg1, arg2, arg3);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException {
        try {
            return this.delegate.createSocket(arg0, arg1);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        try {
            return this.delegate.createSocket(arg0, arg1);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
        try {
            return this.delegate.createSocket(arg0, arg1, arg2, arg3);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
        try {
            return this.delegate.createSocket(arg0, arg1, arg2, arg3);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressFBWarnings(value={"WEAK_TRUST_MANAGER"}, justification="Used only in tests")
    private static class InsecureX509TrustManager
    implements X509TrustManager {
        private InsecureX509TrustManager() {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }
    }
}

