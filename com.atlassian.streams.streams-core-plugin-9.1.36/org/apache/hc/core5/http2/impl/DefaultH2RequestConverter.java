/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http2.H2MessageConverter;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.TextUtils;

public final class DefaultH2RequestConverter
implements H2MessageConverter<HttpRequest> {
    public static final DefaultH2RequestConverter INSTANCE = new DefaultH2RequestConverter();

    @Override
    public HttpRequest convert(List<Header> headers) throws HttpException {
        String method = null;
        String scheme = null;
        String authority = null;
        String path = null;
        ArrayList<Header> messageHeaders = new ArrayList<Header>();
        for (int i = 0; i < headers.size(); ++i) {
            Header header = headers.get(i);
            String name = header.getName();
            String value = header.getValue();
            for (int n = 0; n < name.length(); ++n) {
                char ch = name.charAt(n);
                if (!Character.isAlphabetic(ch) || Character.isLowerCase(ch)) continue;
                throw new ProtocolException("Header name '%s' is invalid (header name contains uppercase characters)", name);
            }
            if (name.startsWith(":")) {
                if (!messageHeaders.isEmpty()) {
                    throw new ProtocolException("Invalid sequence of headers (pseudo-headers must precede message headers)");
                }
                if (name.equals(":method")) {
                    if (method != null) {
                        throw new ProtocolException("Multiple '%s' request headers are illegal", name);
                    }
                    method = value;
                    continue;
                }
                if (name.equals(":scheme")) {
                    if (scheme != null) {
                        throw new ProtocolException("Multiple '%s' request headers are illegal", name);
                    }
                    scheme = value;
                    continue;
                }
                if (name.equals(":path")) {
                    if (path != null) {
                        throw new ProtocolException("Multiple '%s' request headers are illegal", name);
                    }
                    path = value;
                    continue;
                }
                if (name.equals(":authority")) {
                    authority = value;
                    continue;
                }
                throw new ProtocolException("Unsupported request header '%s'", name);
            }
            if (name.equalsIgnoreCase("Connection")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", header.getName(), header.getValue());
            }
            messageHeaders.add(header);
        }
        if (method == null) {
            throw new ProtocolException("Mandatory request header '%s' not found", ":method");
        }
        if (Method.CONNECT.isSame(method)) {
            if (authority == null) {
                throw new ProtocolException("Header '%s' is mandatory for CONNECT request", ":authority");
            }
            if (scheme != null) {
                throw new ProtocolException("Header '%s' must not be set for CONNECT request", ":scheme");
            }
            if (path != null) {
                throw new ProtocolException("Header '%s' must not be set for CONNECT request", ":path");
            }
        } else {
            if (scheme == null) {
                throw new ProtocolException("Mandatory request header '%s' not found", ":scheme");
            }
            if (path == null) {
                throw new ProtocolException("Mandatory request header '%s' not found", ":path");
            }
        }
        BasicHttpRequest httpRequest = new BasicHttpRequest(method, path);
        httpRequest.setVersion(HttpVersion.HTTP_2);
        httpRequest.setScheme(scheme);
        try {
            httpRequest.setAuthority(URIAuthority.create(authority));
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), ex);
        }
        httpRequest.setPath(path);
        for (int i = 0; i < messageHeaders.size(); ++i) {
            httpRequest.addHeader((Header)messageHeaders.get(i));
        }
        return httpRequest;
    }

    @Override
    public List<Header> convert(HttpRequest message) throws HttpException {
        if (TextUtils.isBlank(message.getMethod())) {
            throw new ProtocolException("Request method is empty");
        }
        boolean optionMethod = Method.CONNECT.name().equalsIgnoreCase(message.getMethod());
        if (optionMethod) {
            if (message.getAuthority() == null) {
                throw new ProtocolException("CONNECT request authority is not set");
            }
            if (message.getPath() != null) {
                throw new ProtocolException("CONNECT request path must be null");
            }
        } else {
            if (TextUtils.isBlank(message.getScheme())) {
                throw new ProtocolException("Request scheme is not set");
            }
            if (TextUtils.isBlank(message.getPath())) {
                throw new ProtocolException("Request path is not set");
            }
        }
        ArrayList<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader(":method", message.getMethod(), false));
        if (optionMethod) {
            headers.add(new BasicHeader(":authority", message.getAuthority(), false));
        } else {
            headers.add(new BasicHeader(":scheme", message.getScheme(), false));
            if (message.getAuthority() != null) {
                headers.add(new BasicHeader(":authority", message.getAuthority(), false));
            }
            headers.add(new BasicHeader(":path", message.getPath(), false));
        }
        Iterator<Header> it = message.headerIterator();
        while (it.hasNext()) {
            Header header = it.next();
            String name = header.getName();
            String value = header.getValue();
            if (name.startsWith(":")) {
                throw new ProtocolException("Header name '%s' is invalid", name);
            }
            if (name.equalsIgnoreCase("Connection")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", name, value);
            }
            headers.add(new BasicHeader(name.toLowerCase(Locale.ROOT), value));
        }
        return headers;
    }
}

