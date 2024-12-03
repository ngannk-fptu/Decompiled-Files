/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.membership.cloud;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.catalina.tribes.membership.cloud.StreamProvider;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class AbstractStreamProvider
implements StreamProvider {
    private static final Log log = LogFactory.getLog(AbstractStreamProvider.class);
    protected static final StringManager sm = StringManager.getManager(AbstractStreamProvider.class);
    protected static final TrustManager[] INSECURE_TRUST_MANAGERS = new TrustManager[]{new X509TrustManager(){

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }};

    protected abstract SSLSocketFactory getSocketFactory();

    public URLConnection openConnection(String url, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        URLConnection connection;
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("%s opening connection: url [%s], headers [%s], connectTimeout [%s], readTimeout [%s]", this.getClass().getSimpleName(), url, headers, Integer.toString(connectTimeout), Integer.toString(readTimeout)));
        }
        try {
            connection = new URI(url).toURL().openConnection();
        }
        catch (IllegalArgumentException | URISyntaxException e) {
            throw new IOException(e);
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (connectTimeout < 0 || readTimeout < 0) {
            throw new IllegalArgumentException(String.format("Neither connectTimeout [%s] nor readTimeout [%s] can be less than 0 for URLConnection.", Integer.toString(connectTimeout), Integer.toString(readTimeout)));
        }
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        return connection;
    }

    @Override
    public InputStream openStream(String url, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        URLConnection connection = this.openConnection(url, headers, connectTimeout, readTimeout);
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection)connection).setSSLSocketFactory(this.getSocketFactory());
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("Using HttpsURLConnection with SSLSocketFactory [%s] for url [%s].", this.getSocketFactory(), url));
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)String.format("Using URLConnection for url [%s].", url));
        }
        return connection.getInputStream();
    }

    protected static TrustManager[] configureCaCert(String caCertFile) throws Exception {
        if (caCertFile != null) {
            TrustManager[] trustManagerArray;
            BufferedInputStream pemInputStream = new BufferedInputStream(new FileInputStream(caCertFile));
            try {
                CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                KeyStore trustStore = KeyStore.getInstance("JKS");
                trustStore.load(null);
                Collection<? extends Certificate> c = certFactory.generateCertificates(pemInputStream);
                for (Certificate trustManagerArray2 : c) {
                    X509Certificate cert = (X509Certificate)trustManagerArray2;
                    String alias = cert.getSubjectX500Principal().getName();
                    trustStore.setCertificateEntry(alias, cert);
                }
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);
                trustManagerArray = trustManagerFactory.getTrustManagers();
            }
            catch (Throwable throwable) {
                try {
                    try {
                        ((InputStream)pemInputStream).close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (FileNotFoundException fnfe) {
                    log.error((Object)sm.getString("abstractStream.fileNotFound", caCertFile));
                    throw fnfe;
                }
                catch (Exception e) {
                    log.error((Object)sm.getString("abstractStream.trustManagerError", caCertFile));
                    throw e;
                }
            }
            ((InputStream)pemInputStream).close();
            return trustManagerArray;
        }
        log.warn((Object)sm.getString("abstractStream.CACertUndefined"));
        return INSECURE_TRUST_MANAGERS;
    }
}

