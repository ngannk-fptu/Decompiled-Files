/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

public interface SSLContext {
    public void init(KeyManager[] var1, TrustManager[] var2, SecureRandom var3) throws KeyManagementException;

    public void destroy();

    public SSLSessionContext getServerSessionContext();

    public SSLEngine createSSLEngine();

    public SSLServerSocketFactory getServerSocketFactory();

    public SSLParameters getSupportedSSLParameters();

    public X509Certificate[] getCertificateChain(String var1);

    public X509Certificate[] getAcceptedIssuers();
}

