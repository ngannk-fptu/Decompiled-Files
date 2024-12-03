/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.httpclient.Header
 *  org.apache.commons.httpclient.HeaderElement
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.NameValuePair
 *  org.apache.commons.httpclient.URIException
 */
package com.atlassian.confluence.util.http.httpclient;

import com.atlassian.confluence.util.http.HttpRequest;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.SizeLimitedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;

@Deprecated(forRemoval=true)
public class HttpClientHttpResponse
implements HttpResponse {
    private HttpRequest request;
    private HttpMethod method;

    public HttpClientHttpResponse(HttpRequest httpRequest, HttpMethod method) {
        this.request = httpRequest;
        this.method = method;
    }

    @Override
    public URI getResponseURI() {
        try {
            return new URI(this.method.getURI().getURI());
        }
        catch (URISyntaxException | URIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return this.method == null || this.method.getStatusCode() < 200 || this.method.getStatusCode() > 299;
    }

    @Override
    public boolean isNotFound() {
        return this.method.getStatusCode() == 404;
    }

    @Override
    public boolean isNotPermitted() {
        return this.method.getStatusCode() == 403 || this.method.getStatusCode() == 401;
    }

    @Override
    public InputStream getResponse() throws IOException {
        if (this.request.getMaximumSize() > 0) {
            return new SizeLimitedInputStream(this.method.getResponseBodyAsStream(), this.request.getMaximumSize());
        }
        return this.method.getResponseBodyAsStream();
    }

    @Override
    public String getContentType() {
        return this.getContentTypeHeader().toString();
    }

    private Header getContentTypeHeader() {
        return this.method.getResponseHeader("Content-Type");
    }

    @Override
    public String getCharset() {
        Header contentType = this.getContentTypeHeader();
        if (contentType != null) {
            HeaderElement[] headerElements;
            for (HeaderElement headerElement : headerElements = contentType.getElements()) {
                NameValuePair charset = headerElement.getParameterByName("charset");
                if (charset == null) continue;
                return charset.getValue();
            }
        }
        return null;
    }

    @Override
    public String getMIMEType() {
        HeaderElement[] elements;
        for (HeaderElement element : elements = this.getContentTypeHeader().getElements()) {
            if (element.getValue() != null) continue;
            return element.getName();
        }
        return null;
    }

    @Override
    public String[] getHeaders(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name parameter must not be null");
        }
        Header[] headers = this.method.getResponseHeaders(name);
        if (headers == null) {
            return new String[0];
        }
        String[] headerValues = new String[headers.length];
        for (int i = 0; i < headers.length; ++i) {
            headerValues[i] = headers[i].getValue();
        }
        return headerValues;
    }

    @Override
    public String getStatusMessage() {
        return this.method.getStatusText();
    }

    @Override
    public int getStatusCode() {
        return this.method.getStatusCode();
    }

    @Override
    public void finish() {
        this.method.releaseConnection();
    }
}

