/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.HttpMethod;
import com.microsoft.aad.msal4j.HttpRequest;
import com.microsoft.aad.msal4j.HttpResponse;
import com.microsoft.aad.msal4j.IHttpClient;
import com.microsoft.aad.msal4j.IHttpResponse;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultHttpClient
implements IHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpClient.class);
    private final Proxy proxy;
    private final SSLSocketFactory sslSocketFactory;
    private int connectTimeout = 0;
    private int readTimeout = 0;

    DefaultHttpClient(Proxy proxy, SSLSocketFactory sslSocketFactory, Integer connectTimeout, Integer readTimeout) {
        this.proxy = proxy;
        this.sslSocketFactory = sslSocketFactory;
        if (connectTimeout != null) {
            this.connectTimeout = connectTimeout;
        }
        if (readTimeout != null) {
            this.readTimeout = readTimeout;
        }
    }

    @Override
    public IHttpResponse send(HttpRequest httpRequest) throws Exception {
        HttpResponse response = null;
        if (httpRequest.httpMethod() == HttpMethod.GET) {
            response = this.executeHttpGet(httpRequest);
        } else if (httpRequest.httpMethod() == HttpMethod.POST) {
            response = this.executeHttpPost(httpRequest);
        }
        return response;
    }

    private HttpResponse executeHttpGet(HttpRequest httpRequest) throws Exception {
        HttpsURLConnection conn = this.openConnection(httpRequest.url());
        this.configureAdditionalHeaders(conn, httpRequest);
        return this.readResponseFromConnection(conn);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private HttpResponse executeHttpPost(HttpRequest httpRequest) throws Exception {
        HttpsURLConnection conn = this.openConnection(httpRequest.url());
        this.configureAdditionalHeaders(conn, httpRequest);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        try (DataOutputStream wr = null;){
            wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(httpRequest.body());
            wr.flush();
            HttpResponse httpResponse = this.readResponseFromConnection(conn);
            return httpResponse;
        }
    }

    private HttpsURLConnection openConnection(URL finalURL) throws IOException {
        HttpsURLConnection connection = this.proxy != null ? (HttpsURLConnection)finalURL.openConnection(this.proxy) : (HttpsURLConnection)finalURL.openConnection();
        if (this.sslSocketFactory != null) {
            connection.setSSLSocketFactory(this.sslSocketFactory);
        }
        connection.setConnectTimeout(this.connectTimeout);
        connection.setReadTimeout(this.readTimeout);
        return connection;
    }

    private void configureAdditionalHeaders(HttpsURLConnection conn, HttpRequest httpRequest) {
        if (httpRequest.headers() != null) {
            for (Map.Entry<String, String> entry : httpRequest.headers().entrySet()) {
                if (entry.getValue() == null) continue;
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private HttpResponse readResponseFromConnection(HttpsURLConnection conn) throws IOException {
        try (InputStream is = null;){
            HttpResponse httpResponse = new HttpResponse();
            int responseCode = conn.getResponseCode();
            httpResponse.statusCode(responseCode);
            if (responseCode != 200) {
                is = conn.getErrorStream();
                if (is != null) {
                    httpResponse.addHeaders(conn.getHeaderFields());
                    httpResponse.body(this.inputStreamToString(is));
                }
                HttpResponse httpResponse2 = httpResponse;
                return httpResponse2;
            }
            is = conn.getInputStream();
            httpResponse.addHeaders(conn.getHeaderFields());
            httpResponse.body(this.inputStreamToString(is));
            HttpResponse httpResponse3 = httpResponse;
            return httpResponse3;
        }
    }

    private String inputStreamToString(InputStream is) {
        Scanner s = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

