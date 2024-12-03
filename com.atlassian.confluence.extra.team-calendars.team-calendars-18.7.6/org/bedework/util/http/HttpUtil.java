/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpException
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpVersion
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.StatusLine
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpHead
 *  org.apache.http.client.methods.HttpOptions
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpRequestBase
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.ByteArrayEntity
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.message.BasicStatusLine
 */
package org.bedework.util.http;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.bedework.util.http.Headers;
import org.bedework.util.http.HttpMkcalendar;
import org.bedework.util.http.HttpMkcol;
import org.bedework.util.http.HttpPropfind;
import org.bedework.util.http.HttpReport;

public class HttpUtil
implements Serializable {
    private HttpUtil() {
    }

    public static CloseableHttpClient getClient(boolean disableRedirects) {
        HttpClientBuilder bldr = HttpClientBuilder.create();
        if (disableRedirects) {
            bldr.disableRedirectHandling();
        }
        return bldr.build();
    }

    public static CloseableHttpResponse doGet(CloseableHttpClient cl, URI uri, Headers hdrs, String acceptContentType) throws IOException {
        Headers headers = HttpUtil.ensureHeaders(hdrs);
        if (acceptContentType != null) {
            headers.add("Accept", acceptContentType);
        }
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeaders(headers.asArray());
        return cl.execute((HttpUriRequest)httpGet);
    }

    public static CloseableHttpResponse doHead(CloseableHttpClient cl, URI uri, Headers hdrs, String acceptContentType) throws IOException {
        Headers headers = HttpUtil.ensureHeaders(hdrs);
        if (acceptContentType != null) {
            headers.add("Accept", acceptContentType);
        }
        HttpHead httphead = new HttpHead(uri);
        httphead.setHeaders(headers.asArray());
        return cl.execute((HttpUriRequest)httphead);
    }

    public static CloseableHttpResponse doPost(CloseableHttpClient cl, URI uri, Headers hdrs, String acceptContentType, String content) throws IOException {
        Headers headers = HttpUtil.ensureHeaders(hdrs);
        if (acceptContentType != null) {
            headers.add("Accept", acceptContentType);
        }
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeaders(hdrs.asArray());
        StringEntity entity = new StringEntity(content);
        httpPost.setEntity((HttpEntity)entity);
        return cl.execute((HttpUriRequest)httpPost);
    }

    private static Headers ensureHeaders(Headers hdrs) {
        if (hdrs == null) {
            return new Headers();
        }
        return hdrs;
    }

    public static String getFirstHeaderValue(HttpResponse resp, String name) {
        Header h = resp.getFirstHeader(name);
        if (h == null) {
            return null;
        }
        return h.getValue();
    }

    public static HttpRequestBase findMethod(String name, URI uri) {
        String nm = name.toUpperCase();
        if ("PUT".equals(nm)) {
            return new HttpPut(uri);
        }
        if ("GET".equals(nm)) {
            return new HttpGet(uri);
        }
        if ("DELETE".equals(nm)) {
            return new HttpDelete(uri);
        }
        if ("POST".equals(nm)) {
            return new HttpPost(uri);
        }
        if ("PROPFIND".equals(nm)) {
            return new HttpPropfind(uri);
        }
        if ("MKCALENDAR".equals(nm)) {
            return new HttpMkcalendar(uri);
        }
        if ("MKCOL".equals(nm)) {
            return new HttpMkcol(uri);
        }
        if ("OPTIONS".equals(nm)) {
            return new HttpOptions(uri);
        }
        if ("REPORT".equals(nm)) {
            return new HttpReport(uri);
        }
        if ("HEAD".equals(nm)) {
            return new HttpHead(uri);
        }
        return null;
    }

    public static void setContent(HttpRequestBase req, byte[] content, String contentType) throws HttpException {
        if (content == null) {
            return;
        }
        if (!(req instanceof HttpEntityEnclosingRequestBase)) {
            throw new HttpException("Invalid operation for method " + req.getMethod());
        }
        HttpEntityEnclosingRequestBase eem = (HttpEntityEnclosingRequestBase)req;
        ByteArrayEntity entity = new ByteArrayEntity(content);
        entity.setContentType(contentType);
        eem.setEntity((HttpEntity)entity);
    }

    public static int getStatus(HttpResponse resp) {
        return resp.getStatusLine().getStatusCode();
    }

    public static StatusLine getHttpStatus(String statusLine) throws HttpException {
        String[] splits = statusLine.split("\\s+");
        if (splits.length < 2 || !splits[0].startsWith("HTTP/")) {
            throw new HttpException("Bad status line: " + statusLine);
        }
        String[] version = splits[0].substring(5).split(".");
        if (version.length != 2) {
            throw new HttpException("Bad status line: " + statusLine);
        }
        HttpVersion hv = new HttpVersion(Integer.valueOf(version[0]).intValue(), Integer.valueOf(version[1]).intValue());
        int status = Integer.valueOf(splits[1]);
        String reason = splits.length < 3 ? null : splits[2];
        return new BasicStatusLine((ProtocolVersion)hv, status, reason);
    }

    public static String makeHttpStatus(int status, String reason) {
        return "HTTP/1.1 " + status + reason;
    }

    public static String makeOKHttpStatus() {
        return HttpUtil.makeHttpStatus(200, "OK");
    }
}

