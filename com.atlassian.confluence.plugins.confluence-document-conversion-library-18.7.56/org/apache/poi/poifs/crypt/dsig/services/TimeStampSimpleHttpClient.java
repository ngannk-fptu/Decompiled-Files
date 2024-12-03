/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.services;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.LambdaMetafactory;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampHttpClient;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.RandomSingleton;

public class TimeStampSimpleHttpClient
implements TimeStampHttpClient {
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String USER_AGENT = "User-Agent";
    protected static final String BASIC_AUTH = "Authorization";
    protected static final String REDIRECT_LOCATION = "Location";
    private static final Logger LOG = LogManager.getLogger(TimeStampSimpleHttpClient.class);
    private static final int DEFAULT_TIMESTAMP_RESPONSE_SIZE = 10000000;
    private static int MAX_TIMESTAMP_RESPONSE_SIZE = 10000000;
    protected SignatureConfig config;
    protected Proxy proxy = Proxy.NO_PROXY;
    protected final Map<String, String> header = new HashMap<String, String>();
    protected String contentTypeOut = null;
    protected boolean ignoreHttpsCertificates = false;
    protected boolean followRedirects = false;

    public static void setMaxTimestampResponseSize(int maxTimestampResponseSize) {
        MAX_TIMESTAMP_RESPONSE_SIZE = maxTimestampResponseSize;
    }

    public static int getMaxTimestampResponseSize() {
        return MAX_TIMESTAMP_RESPONSE_SIZE;
    }

    @Override
    public void init(SignatureConfig config) {
        this.config = config;
        this.header.clear();
        this.header.put(USER_AGENT, config.getUserAgent());
        this.contentTypeOut = null;
        this.setProxy(config.getProxyUrl());
        this.setBasicAuthentication(config.getTspUser(), config.getTspPass());
    }

    public void setProxy(String proxyUrl) {
        if (proxyUrl == null || proxyUrl.isEmpty()) {
            this.proxy = Proxy.NO_PROXY;
        } else {
            try {
                URL pUrl = new URL(proxyUrl);
                String host = pUrl.getHost();
                int port = pUrl.getPort();
                this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(host), port == -1 ? 80 : port));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    @Override
    public void setContentTypeIn(String contentType) {
        this.header.put(CONTENT_TYPE, contentType);
    }

    @Override
    public void setContentTypeOut(String contentType) {
        this.contentTypeOut = contentType;
    }

    @Override
    public void setBasicAuthentication(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            this.header.remove(BASIC_AUTH);
        } else {
            String userPassword = username + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes(StandardCharsets.ISO_8859_1));
            this.header.put(BASIC_AUTH, "Basic " + encoding);
        }
    }

    @Override
    public boolean isIgnoreHttpsCertificates() {
        return this.ignoreHttpsCertificates;
    }

    @Override
    public void setIgnoreHttpsCertificates(boolean ignoreHttpsCertificates) {
        this.ignoreHttpsCertificates = ignoreHttpsCertificates;
    }

    @Override
    public boolean isFollowRedirects() {
        return this.followRedirects;
    }

    @Override
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    @Override
    public TimeStampHttpClient.TimeStampHttpClientResponse post(String url, byte[] payload) throws IOException {
        MethodHandler handler = huc -> {
            huc.setRequestMethod("POST");
            huc.setDoOutput(true);
            try (OutputStream hucOut = huc.getOutputStream();){
                hucOut.write(payload);
            }
        };
        return this.handleRedirect(url, handler, this.isFollowRedirects());
    }

    @Override
    public TimeStampHttpClient.TimeStampHttpClientResponse get(String url) throws IOException {
        return this.handleRedirect(url, huc -> {}, this.isFollowRedirects());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    protected TimeStampHttpClient.TimeStampHttpClientResponse handleRedirect(String url, MethodHandler handler, boolean followRedirect) throws IOException {
        huc = (HttpURLConnection)new URL(url).openConnection(this.proxy);
        if (this.ignoreHttpsCertificates) {
            this.recklessConnection(huc);
        }
        huc.setConnectTimeout(20000);
        huc.setReadTimeout(20000);
        this.header.forEach((BiConsumer<String, String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)V, setRequestProperty(java.lang.String java.lang.String ), (Ljava/lang/String;Ljava/lang/String;)V)((HttpURLConnection)huc));
        try {
            handler.handle(huc);
            huc.connect();
            responseCode = huc.getResponseCode();
            switch (responseCode) {
                case 301: 
                case 302: 
                case 303: {
                    newUrl = huc.getHeaderField("Location");
                    if (newUrl != null && followRedirect) {
                        TimeStampSimpleHttpClient.LOG.atWarn().log("Received redirect: {} -> {}", (Object)url, (Object)newUrl);
                        var8_7 = this.handleRedirect(newUrl, handler, false);
                        return var8_7;
                    }
                    TimeStampSimpleHttpClient.LOG.atWarn().log("Redirect ignored - giving up: {} -> {}", (Object)url, (Object)newUrl);
                    responseBytes = null;
                    break;
                }
                case 200: {
                    contentType = huc.getHeaderField("Content-Type");
                    if (this.contentTypeOut != null && !this.contentTypeOut.equals(contentType)) {
                        throw new IOException("Content-Type mismatch - expected `" + this.contentTypeOut + "', received '" + contentType + "'");
                    }
                    is = huc.getInputStream();
                    var10_12 = null;
                    responseBytes = IOUtils.toByteArrayWithMaxLength(is, TimeStampSimpleHttpClient.getMaxTimestampResponseSize());
                    if (is == null) break;
                    if (var10_12 == null) ** GOTO lbl40
                    try {
                        is.close();
                    }
                    catch (Throwable var11_13) {
                        var10_12.addSuppressed(var11_13);
                    }
                    break;
lbl40:
                    // 1 sources

                    is.close();
                    break;
                    catch (Throwable var11_14) {
                        try {
                            var10_12 = var11_14;
                            throw var11_14;
                        }
                        catch (Throwable var12_15) {
                            if (is != null) {
                                if (var10_12 != null) {
                                    try {
                                        is.close();
                                    }
                                    catch (Throwable var13_16) {
                                        var10_12.addSuppressed(var13_16);
                                    }
                                } else {
                                    is.close();
                                }
                            }
                            throw var12_15;
                        }
                    }
                }
                default: {
                    message = "Error contacting TSP server " + url + ", had status code " + responseCode + "/" + huc.getResponseMessage();
                    TimeStampSimpleHttpClient.LOG.atError().log(message);
                    throw new IOException(message);
                }
            }
            var7_6 = new TimeStampSimpleHttpClientResponse(responseCode, responseBytes);
            return var7_6;
        }
        finally {
            huc.disconnect();
        }
    }

    protected void recklessConnection(HttpURLConnection conn) throws IOException {
        if (!(conn instanceof HttpsURLConnection)) {
            return;
        }
        HttpsURLConnection conns = (HttpsURLConnection)conn;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new UnsafeTrustManager()}, RandomSingleton.getInstance());
            conns.setSSLSocketFactory(sc.getSocketFactory());
            conns.setHostnameVerifier((hostname, session) -> true);
        }
        catch (GeneralSecurityException e) {
            throw new IOException("Unable to reckless wrap connection.", e);
        }
    }

    private static class UnsafeTrustManager
    implements X509TrustManager {
        private UnsafeTrustManager() {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }

    protected static interface MethodHandler {
        public void handle(HttpURLConnection var1) throws IOException;
    }

    private static class TimeStampSimpleHttpClientResponse
    implements TimeStampHttpClient.TimeStampHttpClientResponse {
        private final int responseCode;
        private final byte[] responseBytes;

        public TimeStampSimpleHttpClientResponse(int responseCode, byte[] responseBytes) {
            this.responseCode = responseCode;
            this.responseBytes = responseBytes;
        }

        @Override
        public int getResponseCode() {
            return this.responseCode;
        }

        @Override
        public byte[] getResponseBytes() {
            return this.responseBytes;
        }
    }
}

