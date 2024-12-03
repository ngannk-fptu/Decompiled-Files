/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.net.jsse.PEMFile
 */
package org.apache.catalina.tribes.membership.cloud;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.apache.catalina.tribes.membership.cloud.AbstractStreamProvider;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.jsse.PEMFile;

public class CertificateStreamProvider
extends AbstractStreamProvider {
    private static final Log log = LogFactory.getLog(CertificateStreamProvider.class);
    private final SSLSocketFactory factory;

    CertificateStreamProvider(String clientCertFile, String clientKeyFile, String clientKeyPassword, String clientKeyAlgo, String caCertFile) throws Exception {
        char[] password = clientKeyPassword != null ? clientKeyPassword.toCharArray() : new char[]{};
        KeyManager[] keyManagers = CertificateStreamProvider.configureClientCert(clientCertFile, clientKeyFile, password, clientKeyAlgo);
        TrustManager[] trustManagers = CertificateStreamProvider.configureCaCert(caCertFile);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagers, trustManagers, null);
        this.factory = context.getSocketFactory();
    }

    @Override
    protected SSLSocketFactory getSocketFactory() {
        return this.factory;
    }

    private static KeyManager[] configureClientCert(String clientCertFile, String clientKeyFile, char[] clientKeyPassword, String clientKeyAlgo) throws Exception {
        KeyManager[] keyManagerArray;
        FileInputStream certInputStream = new FileInputStream(clientCertFile);
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate)certFactory.generateCertificate(certInputStream);
            PEMFile pemFile = new PEMFile(clientKeyFile, new String(clientKeyPassword), clientKeyAlgo);
            PrivateKey privKey = pemFile.getPrivateKey();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);
            String alias = cert.getSubjectX500Principal().getName();
            keyStore.setKeyEntry(alias, privKey, clientKeyPassword, new Certificate[]{cert});
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, clientKeyPassword);
            keyManagerArray = keyManagerFactory.getKeyManagers();
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)certInputStream).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                log.error((Object)sm.getString("certificateStream.clientCertError", clientCertFile, clientKeyFile));
                throw e;
            }
        }
        ((InputStream)certInputStream).close();
        return keyManagerArray;
    }
}

