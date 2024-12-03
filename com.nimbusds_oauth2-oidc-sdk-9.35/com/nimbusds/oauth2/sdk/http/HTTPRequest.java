/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jwt.SignedJWT
 *  net.jcip.annotations.ThreadSafe
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.http;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPMessage;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;

@ThreadSafe
public class HTTPRequest
extends HTTPMessage {
    private final Method method;
    private final URL url;
    private String query = null;
    private String fragment = null;
    private int connectTimeout = 0;
    private int readTimeout = 0;
    private Proxy proxy = null;
    private boolean followRedirects = true;
    private X509Certificate clientX509Certificate = null;
    private String clientX509CertificateSubjectDN = null;
    private String clientX509CertificateRootDN = null;
    private HostnameVerifier hostnameVerifier = null;
    private SSLSocketFactory sslSocketFactory = null;
    private static HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    private static SSLSocketFactory defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();

    public HTTPRequest(Method method, URL url) {
        if (method == null) {
            throw new IllegalArgumentException("The HTTP method must not be null");
        }
        this.method = method;
        if (url == null) {
            throw new IllegalArgumentException("The HTTP URL must not be null");
        }
        this.url = url;
    }

    public HTTPRequest(Method method, URI uri) {
        this(method, HTTPRequest.toURLWithUncheckedException(uri));
    }

    private static URL toURLWithUncheckedException(URI uri) {
        try {
            return uri.toURL();
        }
        catch (IllegalArgumentException | MalformedURLException e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }

    public Method getMethod() {
        return this.method;
    }

    public URL getURL() {
        return this.url;
    }

    public URI getURI() {
        try {
            return this.url.toURI();
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void ensureMethod(Method expectedMethod) throws ParseException {
        if (this.method != expectedMethod) {
            throw new ParseException("The HTTP request method must be " + (Object)((Object)expectedMethod));
        }
    }

    public String getAuthorization() {
        return this.getHeaderValue("Authorization");
    }

    public void setAuthorization(String authz) {
        this.setHeader("Authorization", new String[]{authz});
    }

    public SignedJWT getDPoP() {
        try {
            return this.getPoPWithException();
        }
        catch (ParseException e) {
            return null;
        }
    }

    public SignedJWT getPoPWithException() throws ParseException {
        String dPoP = this.getHeaderValue("DPoP");
        if (dPoP == null) {
            return null;
        }
        try {
            return SignedJWT.parse((String)dPoP);
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public void setDPoP(SignedJWT dPoPJWT) {
        if (dPoPJWT != null) {
            this.setHeader("DPoP", new String[]{dPoPJWT.serialize()});
        } else {
            this.setHeader("DPoP", null);
        }
    }

    public String getAccept() {
        return this.getHeaderValue("Accept");
    }

    public void setAccept(String accept) {
        this.setHeader("Accept", new String[]{accept});
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    private void ensureQuery() throws ParseException {
        if (this.query == null || this.query.trim().isEmpty()) {
            throw new ParseException("Missing or empty HTTP query string / entity body");
        }
    }

    public Map<String, List<String>> getQueryParameters() {
        return URLUtils.parseParameters(this.query);
    }

    public JSONObject getQueryAsJSONObject() throws ParseException {
        this.ensureEntityContentType(ContentType.APPLICATION_JSON);
        this.ensureQuery();
        return JSONObjectUtils.parse(this.query);
    }

    public String getFragment() {
        return this.fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("The HTTP connect timeout must be zero or positive");
        }
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        if (readTimeout < 0) {
            throw new IllegalArgumentException("The HTTP response read timeout must be zero or positive");
        }
        this.readTimeout = readTimeout;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public boolean getFollowRedirects() {
        return this.followRedirects;
    }

    public void setFollowRedirects(boolean follow) {
        this.followRedirects = follow;
    }

    public X509Certificate getClientX509Certificate() {
        return this.clientX509Certificate;
    }

    public void setClientX509Certificate(X509Certificate clientX509Certificate) {
        this.clientX509Certificate = clientX509Certificate;
    }

    public String getClientX509CertificateSubjectDN() {
        return this.clientX509CertificateSubjectDN;
    }

    public void setClientX509CertificateSubjectDN(String subjectDN) {
        this.clientX509CertificateSubjectDN = subjectDN;
    }

    public String getClientX509CertificateRootDN() {
        return this.clientX509CertificateRootDN;
    }

    public void setClientX509CertificateRootDN(String rootDN) {
        this.clientX509CertificateRootDN = rootDN;
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return this.sslSocketFactory;
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public static HostnameVerifier getDefaultHostnameVerifier() {
        return defaultHostnameVerifier;
    }

    public static void setDefaultHostnameVerifier(HostnameVerifier defaultHostnameVerifier) {
        if (defaultHostnameVerifier == null) {
            throw new IllegalArgumentException("The hostname verifier must not be null");
        }
        HTTPRequest.defaultHostnameVerifier = defaultHostnameVerifier;
    }

    public static SSLSocketFactory getDefaultSSLSocketFactory() {
        return defaultSSLSocketFactory;
    }

    public static void setDefaultSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        if (sslSocketFactory == null) {
            throw new IllegalArgumentException("The SSL socket factory must not be null");
        }
        defaultSSLSocketFactory = sslSocketFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public HttpURLConnection toHttpURLConnection(HostnameVerifier hostnameVerifier, SSLSocketFactory sslSocketFactory) throws IOException {
        HostnameVerifier savedHostnameVerifier = this.getHostnameVerifier();
        SSLSocketFactory savedSSLFactory = this.getSSLSocketFactory();
        try {
            this.setHostnameVerifier(hostnameVerifier);
            this.setSSLSocketFactory(sslSocketFactory);
            HttpURLConnection httpURLConnection = this.toHttpURLConnection();
            return httpURLConnection;
        }
        finally {
            this.setHostnameVerifier(savedHostnameVerifier);
            this.setSSLSocketFactory(savedSSLFactory);
        }
    }

    public HttpURLConnection toHttpURLConnection() throws IOException {
        HttpURLConnection conn;
        StringBuilder sb;
        URL finalURL = this.url;
        if (this.query != null && (this.method.equals((Object)Method.GET) || this.method.equals((Object)Method.DELETE))) {
            sb = new StringBuilder(this.url.toString());
            sb.append('?');
            sb.append(this.query);
            try {
                finalURL = new URL(sb.toString());
            }
            catch (MalformedURLException e) {
                throw new IOException("Couldn't append query string: " + e.getMessage(), e);
            }
        }
        if (this.fragment != null) {
            sb = new StringBuilder(finalURL.toString());
            sb.append('#');
            sb.append(this.fragment);
            try {
                finalURL = new URL(sb.toString());
            }
            catch (MalformedURLException e) {
                throw new IOException("Couldn't append raw fragment: " + e.getMessage(), e);
            }
        }
        if ((conn = (HttpURLConnection)(this.proxy == null ? finalURL.openConnection() : finalURL.openConnection(this.proxy))) instanceof HttpsURLConnection) {
            HttpsURLConnection sslConn = (HttpsURLConnection)conn;
            sslConn.setHostnameVerifier(this.hostnameVerifier != null ? this.hostnameVerifier : HTTPRequest.getDefaultHostnameVerifier());
            sslConn.setSSLSocketFactory(this.sslSocketFactory != null ? this.sslSocketFactory : HTTPRequest.getDefaultSSLSocketFactory());
        }
        for (Map.Entry entry : this.getHeaderMap().entrySet()) {
            for (String headerValue : (List)entry.getValue()) {
                conn.addRequestProperty((String)entry.getKey(), headerValue);
            }
        }
        conn.setRequestMethod(this.method.name());
        conn.setConnectTimeout(this.connectTimeout);
        conn.setReadTimeout(this.readTimeout);
        conn.setInstanceFollowRedirects(this.followRedirects);
        if (this.method.equals((Object)Method.POST) || this.method.equals((Object)Method.PUT)) {
            conn.setDoOutput(true);
            if (this.getEntityContentType() != null) {
                conn.setRequestProperty("Content-Type", this.getEntityContentType().toString());
            }
            if (this.query != null) {
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(this.query);
                    writer.close();
                }
                catch (IOException e) {
                    HTTPRequest.closeStreams(conn);
                    throw e;
                }
            }
        }
        return conn;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public HTTPResponse send(HostnameVerifier hostnameVerifier, SSLSocketFactory sslSocketFactory) throws IOException {
        HostnameVerifier savedHostnameVerifier = this.getHostnameVerifier();
        SSLSocketFactory savedSSLFactory = this.getSSLSocketFactory();
        try {
            this.setHostnameVerifier(hostnameVerifier);
            this.setSSLSocketFactory(sslSocketFactory);
            HTTPResponse hTTPResponse = this.send();
            return hTTPResponse;
        }
        finally {
            this.setHostnameVerifier(savedHostnameVerifier);
            this.setSSLSocketFactory(savedSSLFactory);
        }
    }

    public HTTPResponse send() throws IOException {
        String line;
        int statusCode;
        BufferedReader reader;
        HttpURLConnection conn = this.toHttpURLConnection();
        try {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            statusCode = conn.getResponseCode();
        }
        catch (IOException e) {
            statusCode = conn.getResponseCode();
            if (statusCode == -1) {
                throw e;
            }
            InputStream errStream = conn.getErrorStream();
            reader = errStream != null ? new BufferedReader(new InputStreamReader(errStream)) : new BufferedReader(new StringReader(""));
        }
        StringBuilder body = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            body.append(line);
            body.append(System.getProperty("line.separator"));
        }
        reader.close();
        HTTPResponse response = new HTTPResponse(statusCode);
        response.setStatusMessage(conn.getResponseMessage());
        for (Map.Entry<String, List<String>> responseHeader : conn.getHeaderFields().entrySet()) {
            List<String> values;
            if (responseHeader.getKey() == null || (values = responseHeader.getValue()) == null || values.isEmpty() || values.get(0) == null) continue;
            response.setHeader(responseHeader.getKey(), values.toArray(new String[0]));
        }
        HTTPRequest.closeStreams(conn);
        String bodyContent = body.toString();
        if (!bodyContent.isEmpty()) {
            response.setContent(bodyContent);
        }
        return response;
    }

    private static void closeStreams(HttpURLConnection conn) {
        if (conn == null) {
            return;
        }
        try {
            if (conn.getInputStream() != null) {
                conn.getInputStream().close();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (conn.getOutputStream() != null) {
                conn.getOutputStream().close();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (conn.getErrorStream() != null) {
                conn.getOutputStream().close();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static enum Method {
        GET,
        POST,
        PUT,
        DELETE;

    }
}

