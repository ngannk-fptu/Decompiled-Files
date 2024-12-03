/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpVersion
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.message.BasicHeader
 *  org.apache.hc.core5.http.message.BasicHttpRequest
 *  org.apache.hc.core5.net.URIAuthority
 *  org.apache.hc.core5.util.TextUtils
 */
package org.apache.hc.core5.http2.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;
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
                throw new ProtocolException("Header name '%s' is invalid (header name contains uppercase characters)", new Object[]{name});
            }
            if (name.startsWith(":")) {
                if (!messageHeaders.isEmpty()) {
                    throw new ProtocolException("Invalid sequence of headers (pseudo-headers must precede message headers)");
                }
                switch (name) {
                    case ":method": {
                        if (method != null) {
                            throw new ProtocolException("Multiple '%s' request headers are illegal", new Object[]{name});
                        }
                        method = value;
                        break;
                    }
                    case ":scheme": {
                        if (scheme != null) {
                            throw new ProtocolException("Multiple '%s' request headers are illegal", new Object[]{name});
                        }
                        scheme = value;
                        break;
                    }
                    case ":path": {
                        if (path != null) {
                            throw new ProtocolException("Multiple '%s' request headers are illegal", new Object[]{name});
                        }
                        path = value;
                        break;
                    }
                    case ":authority": {
                        authority = value;
                        break;
                    }
                    default: {
                        throw new ProtocolException("Unsupported request header '%s'", new Object[]{name});
                    }
                }
                continue;
            }
            if (name.equalsIgnoreCase("Connection") || name.equalsIgnoreCase("Keep-Alive") || name.equalsIgnoreCase("Proxy-Connection") || name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase("Host") || name.equalsIgnoreCase("Upgrade")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", new Object[]{header.getName(), header.getValue()});
            }
            if (name.equalsIgnoreCase("TE") && !value.equalsIgnoreCase("trailers")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", new Object[]{header.getName(), header.getValue()});
            }
            messageHeaders.add(header);
        }
        if (method == null) {
            throw new ProtocolException("Mandatory request header '%s' not found", new Object[]{":method"});
        }
        if (Method.CONNECT.isSame(method)) {
            if (authority == null) {
                throw new ProtocolException("Header '%s' is mandatory for CONNECT request", new Object[]{":authority"});
            }
            if (scheme != null) {
                throw new ProtocolException("Header '%s' must not be set for CONNECT request", new Object[]{":scheme"});
            }
            if (path != null) {
                throw new ProtocolException("Header '%s' must not be set for CONNECT request", new Object[]{":path"});
            }
        } else {
            if (scheme == null) {
                throw new ProtocolException("Mandatory request header '%s' not found", new Object[]{":scheme"});
            }
            if (path == null) {
                throw new ProtocolException("Mandatory request header '%s' not found", new Object[]{":path"});
            }
        }
        BasicHttpRequest httpRequest = new BasicHttpRequest(method, path);
        httpRequest.setVersion((ProtocolVersion)HttpVersion.HTTP_2);
        httpRequest.setScheme(scheme);
        try {
            httpRequest.setAuthority(URIAuthority.create(authority));
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), (Throwable)ex);
        }
        httpRequest.setPath(path);
        for (int i = 0; i < messageHeaders.size(); ++i) {
            httpRequest.addHeader((Header)messageHeaders.get(i));
        }
        return httpRequest;
    }

    @Override
    public List<Header> convert(HttpRequest message) throws HttpException {
        if (TextUtils.isBlank((CharSequence)message.getMethod())) {
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
            if (TextUtils.isBlank((CharSequence)message.getScheme())) {
                throw new ProtocolException("Request scheme is not set");
            }
            if (TextUtils.isBlank((CharSequence)message.getPath())) {
                throw new ProtocolException("Request path is not set");
            }
        }
        ArrayList<Header> headers = new ArrayList<Header>();
        headers.add((Header)new BasicHeader(":method", (Object)message.getMethod(), false));
        if (optionMethod) {
            headers.add((Header)new BasicHeader(":authority", (Object)message.getAuthority(), false));
        } else {
            headers.add((Header)new BasicHeader(":scheme", (Object)message.getScheme(), false));
            if (message.getAuthority() != null) {
                headers.add((Header)new BasicHeader(":authority", (Object)message.getAuthority(), false));
            }
            headers.add((Header)new BasicHeader(":path", (Object)message.getPath(), false));
        }
        Iterator it = message.headerIterator();
        while (it.hasNext()) {
            Header header = (Header)it.next();
            String name = header.getName();
            String value = header.getValue();
            if (name.startsWith(":")) {
                throw new ProtocolException("Header name '%s' is invalid", new Object[]{name});
            }
            if (name.equalsIgnoreCase("Connection") || name.equalsIgnoreCase("Keep-Alive") || name.equalsIgnoreCase("Proxy-Connection") || name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase("Host") || name.equalsIgnoreCase("Upgrade")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", new Object[]{header.getName(), header.getValue()});
            }
            if (name.equalsIgnoreCase("TE") && !value.equalsIgnoreCase("trailers")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", new Object[]{header.getName(), header.getValue()});
            }
            headers.add((Header)new BasicHeader(TextUtils.toLowerCase((String)name), (Object)value));
        }
        return headers;
    }
}

