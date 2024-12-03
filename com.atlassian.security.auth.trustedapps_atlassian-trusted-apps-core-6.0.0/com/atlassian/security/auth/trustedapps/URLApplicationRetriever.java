/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.Closeables
 *  com.google.common.io.Closer
 *  org.apache.http.HttpHost
 *  org.apache.http.StatusLine
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.InputStreamApplicationRetriever;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.ProxyHostSelector;
import com.atlassian.security.auth.trustedapps.ReaderApplicationRetriever;
import com.google.common.io.Closeables;
import com.google.common.io.Closer;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLApplicationRetriever
implements ApplicationRetriever {
    private static final Logger log = LoggerFactory.getLogger(URLApplicationRetriever.class);
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    public static final String CONNECTION_TIMEOUT_ENV_VAR = "http.connectionTimeout";
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final String SOCKET_TIMEOUT_ENV_VAR = "http.socketTimeout";
    private final String baseUrl;
    private final EncryptionProvider encryptionProvider;

    public URLApplicationRetriever(String baseUrl, EncryptionProvider encryptionProvider) {
        Null.not("baseUrl", baseUrl);
        Null.not("encryptionProvider", encryptionProvider);
        this.baseUrl = baseUrl;
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    public Application getApplication() throws ApplicationRetriever.RetrievalException {
        URI uri;
        String certUrl = this.baseUrl + "/admin/appTrustCertificate";
        try {
            uri = new URI(certUrl);
        }
        catch (URISyntaxException e) {
            throw new ApplicationRetriever.RemoteSystemNotFoundException(e);
        }
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new ApplicationRetriever.RemoteSystemNotFoundException(new MalformedURLException("Undefined URI scheme: " + uri));
        }
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            return this.getHttpApplication(certUrl);
        }
        return this.getURLApplication(certUrl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Application getURLApplication(String certUrl) throws ApplicationRetriever.RetrievalException {
        Application application;
        URLConnection con = new URL(certUrl).openConnection();
        con.connect();
        InputStream in = con.getInputStream();
        try {
            InputStreamReader reader = new InputStreamReader(in);
            ReaderApplicationRetriever retriever = new ReaderApplicationRetriever(reader, this.encryptionProvider);
            application = retriever.getApplication();
        }
        catch (Throwable throwable) {
            try {
                Closeables.closeQuietly((InputStream)in);
                throw throwable;
            }
            catch (MalformedURLException e) {
                throw new ApplicationRetriever.RemoteSystemNotFoundException(e);
            }
            catch (IOException e) {
                throw new ApplicationRetriever.RemoteSystemNotFoundException(e);
            }
        }
        Closeables.closeQuietly((InputStream)in);
        return application;
    }

    private Application getHttpApplication(String certUrl) throws ApplicationRetriever.RetrievalException {
        Closer closer = Closer.create();
        try {
            RequestConfig.Builder configBuilder = RequestConfig.copy((RequestConfig)RequestConfig.DEFAULT).setRedirectsEnabled(true).setConnectTimeout(this.getConnectionTimeout()).setSocketTimeout(this.getSocketTimeout());
            HttpHost proxyHost = ProxyHostSelector.withDefaultProxySelector().select(URI.create(certUrl));
            if (proxyHost != null) {
                log.info("Proxying through {}:{}", (Object)proxyHost.getHostName(), (Object)proxyHost.getPort());
                configBuilder.setProxy(proxyHost);
            }
            CloseableHttpClient client = (CloseableHttpClient)closer.register((Closeable)HttpClients.createDefault());
            HttpGet request = new HttpGet(certUrl);
            request.setConfig(configBuilder.build());
            CloseableHttpResponse response = (CloseableHttpResponse)closer.register((Closeable)client.execute((HttpUriRequest)request));
            StatusLine statusLine = response.getStatusLine();
            int responseCode = statusLine.getStatusCode();
            if (responseCode >= 300) {
                throw new ApplicationRetriever.ApplicationNotFoundException("Invalid response code of " + responseCode + " returned from: " + certUrl);
            }
            Application application = new InputStreamApplicationRetriever((InputStream)closer.register((Closeable)response.getEntity().getContent()), this.encryptionProvider).getApplication();
            return application;
        }
        catch (FileNotFoundException e) {
            throw new ApplicationRetriever.ApplicationNotFoundException(e);
        }
        catch (MalformedURLException e) {
            throw new ApplicationRetriever.RemoteSystemNotFoundException(e);
        }
        catch (IOException e) {
            throw new ApplicationRetriever.RemoteSystemNotFoundException(e);
        }
        finally {
            try {
                closer.close();
            }
            catch (IOException iOException) {}
        }
    }

    private int getSocketTimeout() {
        try {
            return Integer.parseInt(System.getProperty(SOCKET_TIMEOUT_ENV_VAR, String.valueOf(10000)));
        }
        catch (NumberFormatException e) {
            log.warn("Unable to convert the value of environment variable [{}] to an integer. Using default [{}]", new Object[]{SOCKET_TIMEOUT_ENV_VAR, 10000, e});
            return 10000;
        }
    }

    private int getConnectionTimeout() {
        try {
            return Integer.parseInt(System.getProperty(CONNECTION_TIMEOUT_ENV_VAR, String.valueOf(10000)));
        }
        catch (NumberFormatException e) {
            log.warn("Unable to convert the value of environment variable [{}] to an integer. Using default [{}]", new Object[]{CONNECTION_TIMEOUT_ENV_VAR, 10000, e});
            return 10000;
        }
    }
}

