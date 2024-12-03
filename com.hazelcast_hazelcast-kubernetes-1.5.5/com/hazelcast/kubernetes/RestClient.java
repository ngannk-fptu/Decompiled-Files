/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 *  com.hazelcast.nio.IOUtil
 */
package com.hazelcast.kubernetes;

import com.hazelcast.kubernetes.KubernetesClientException;
import com.hazelcast.kubernetes.RestClientException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

final class RestClient {
    private static final ILogger LOGGER = Logger.getLogger(RestClient.class);
    private static final int HTTP_OK = 200;
    private final String url;
    private final List<Header> headers = new ArrayList<Header>();
    private String body;
    private String caCertificate;

    private RestClient(String url) {
        this.url = url;
    }

    static RestClient create(String url) {
        return new RestClient(url);
    }

    RestClient withHeader(String key, String value) {
        this.headers.add(new Header(key, value));
        return this;
    }

    RestClient withBody(String body) {
        this.body = body;
        return this;
    }

    RestClient withCaCertificates(String caCertificate) {
        this.caCertificate = caCertificate;
        return this;
    }

    String get() {
        return this.call("GET");
    }

    String post() {
        return this.call("POST");
    }

    private String call(String method) {
        HttpURLConnection connection = null;
        FilterOutputStream outputStream = null;
        try {
            URL urlToConnect = new URL(this.url);
            connection = (HttpURLConnection)urlToConnect.openConnection();
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection)connection).setSSLSocketFactory(this.buildSslSocketFactory());
            }
            connection.setRequestMethod(method);
            for (Header header : this.headers) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            if (this.body != null) {
                byte[] bodyData = this.body.getBytes("UTF-8");
                connection.setDoOutput(true);
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(bodyData.length));
                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(bodyData);
                ((DataOutputStream)outputStream).flush();
            }
            this.checkHttpOk(method, connection);
            String string = RestClient.read(connection.getInputStream());
            return string;
        }
        catch (IOException e) {
            throw new RestClientException("Failure in executing REST call", e);
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException e) {
                    LOGGER.finest("Error while closing HTTP output stream", (Throwable)e);
                }
            }
        }
    }

    private void checkHttpOk(String method, HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != 200) {
            String errorMessage;
            try {
                errorMessage = RestClient.read(connection.getErrorStream());
            }
            catch (Exception e) {
                throw new RestClientException(String.format("Failure executing: %s at: %s", method, this.url), connection.getResponseCode());
            }
            throw new RestClientException(String.format("Failure executing: %s at: %s. Message: %s", method, this.url, errorMessage), connection.getResponseCode());
        }
    }

    private static String read(InputStream stream) {
        if (stream == null) {
            return "";
        }
        Scanner scanner = new Scanner(stream, "UTF-8");
        scanner.useDelimiter("\\Z");
        return scanner.next();
    }

    private SSLSocketFactory buildSslSocketFactory() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            int i = 0;
            for (Certificate certificate : this.generateCertificates()) {
                String alias = String.format("ca-%d", i++);
                keyStore.setCertificateEntry(alias, certificate);
            }
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext sSLContext = SSLContext.getInstance("TLSv1.2");
            sSLContext.init(null, tmf.getTrustManagers(), null);
            return sSLContext.getSocketFactory();
        }
        catch (Exception e) {
            throw new KubernetesClientException("Failure in generating SSLSocketFactory", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<? extends Certificate> generateCertificates() throws IOException, CertificateException {
        Collection<? extends Certificate> collection;
        ByteArrayInputStream caInput = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = new ByteArrayInputStream(this.caCertificate.getBytes("UTF-8"));
            collection = cf.generateCertificates(caInput);
        }
        catch (Throwable throwable) {
            IOUtil.closeResource(caInput);
            throw throwable;
        }
        IOUtil.closeResource((Closeable)caInput);
        return collection;
    }

    private static final class Header {
        private final String key;
        private final String value;

        private Header(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private String getKey() {
            return this.key;
        }

        private String getValue() {
            return this.value;
        }
    }
}

