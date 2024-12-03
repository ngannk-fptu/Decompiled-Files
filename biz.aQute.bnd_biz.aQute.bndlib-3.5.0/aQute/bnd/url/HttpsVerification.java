/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.url;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.url.DefaultURLConnectionHandler;
import aQute.lib.io.IO;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@BndPlugin(name="url.https.verification", parameters=Config.class)
public class HttpsVerification
extends DefaultURLConnectionHandler {
    private SSLSocketFactory factory;
    private HostnameVerifier verifier;
    private boolean verify = true;
    private String certificatesPath;
    private X509Certificate[] certificateChain;

    public HttpsVerification() {
    }

    public HttpsVerification(String certificates, boolean hostnameVerify, Reporter reporter) {
        this.certificatesPath = certificates;
        this.verify = hostnameVerify;
        this.setReporter(reporter);
    }

    public HttpsVerification(X509Certificate[] certificateChain, boolean b, Reporter hc) {
        this.certificateChain = certificateChain;
        this.verify = b;
        this.setReporter(hc);
    }

    private synchronized void init() throws NoSuchAlgorithmException, KeyManagementException, FileNotFoundException, CertificateException, IOException {
        if (this.factory == null) {
            List<X509Certificate> certificates = this.createCertificates(this.certificatesPath);
            X509Certificate[] trusted = certificates.toArray(new X509Certificate[0]);
            TrustManager[] trustAllCerts = new TrustManager[]{this.getTrustManager(trusted)};
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAllCerts, new SecureRandom());
            this.factory = context.getSocketFactory();
            this.verifier = new HostnameVerifier(){

                @Override
                public boolean verify(String string, SSLSession session) {
                    return HttpsVerification.this.verify;
                }
            };
        }
    }

    X509TrustManager getTrustManager(final X509Certificate[] trusted) {
        X509TrustManager tm = new X509TrustManager(){

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return trusted;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            }
        };
        return tm;
    }

    @Override
    public void handle(URLConnection connection) throws Exception {
        if (connection instanceof HttpsURLConnection && this.matches(connection)) {
            HttpsURLConnection https = (HttpsURLConnection)connection;
            this.init();
            https.setSSLSocketFactory(this.factory);
            https.setHostnameVerifier(this.verifier);
        }
    }

    @Override
    public void setProperties(Map<String, String> map) throws Exception {
        super.setProperties(map);
        this.certificatesPath = map.get("trusted");
    }

    List<X509Certificate> createCertificates(String paths) throws FileNotFoundException, CertificateException, IOException {
        ArrayList<X509Certificate> certificates;
        block15: {
            block14: {
                certificates = new ArrayList<X509Certificate>();
                if (paths == null) break block14;
                for (String path : paths.split("\\s*,\\s*")) {
                    File file = new File(path);
                    if (!file.isFile()) continue;
                    try (InputStream inStream = IO.stream(file);){
                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
                        certificates.add(cert);
                    }
                }
                break block15;
            }
            if (this.certificateChain == null) break block15;
            for (X509Certificate cert : this.certificateChain) {
                certificates.add(cert);
            }
        }
        return certificates;
    }

    public String toString() {
        return "HttpsVerification [verify=" + this.verify + ", certificatesPath=" + this.certificatesPath + "]";
    }

    static interface Config {
        public String trusted();
    }
}

