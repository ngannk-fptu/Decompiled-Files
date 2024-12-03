/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http2.H2MessageConverter;
import org.apache.hc.core5.util.TextUtils;

public class DefaultH2ResponseConverter
implements H2MessageConverter<HttpResponse> {
    public static final DefaultH2ResponseConverter INSTANCE = new DefaultH2ResponseConverter();

    @Override
    public HttpResponse convert(List<Header> headers) throws HttpException {
        int statusCode;
        String statusText = null;
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
                if (name.equals(":status")) {
                    if (statusText != null) {
                        throw new ProtocolException("Multiple '%s' response headers are illegal", name);
                    }
                    statusText = value;
                    continue;
                }
                throw new ProtocolException("Unsupported response header '%s'", name);
            }
            if (name.equalsIgnoreCase("Connection") || name.equalsIgnoreCase("Keep-Alive") || name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase("Upgrade")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", header.getName(), header.getValue());
            }
            messageHeaders.add(header);
        }
        if (statusText == null) {
            throw new ProtocolException("Mandatory response header '%s' not found", ":status");
        }
        try {
            statusCode = Integer.parseInt(statusText);
        }
        catch (NumberFormatException ex) {
            throw new ProtocolException("Invalid response status: " + statusText);
        }
        BasicHttpResponse response = new BasicHttpResponse(statusCode, null);
        response.setVersion(HttpVersion.HTTP_2);
        for (int i = 0; i < messageHeaders.size(); ++i) {
            response.addHeader((Header)messageHeaders.get(i));
        }
        return response;
    }

    @Override
    public List<Header> convert(HttpResponse message) throws HttpException {
        int code = message.getCode();
        if (code < 100 || code >= 600) {
            throw new ProtocolException("Response status %s is invalid", code);
        }
        ArrayList<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader(":status", Integer.toString(code), false));
        Iterator<Header> it = message.headerIterator();
        while (it.hasNext()) {
            Header header = it.next();
            String name = header.getName();
            String value = header.getValue();
            if (name.startsWith(":")) {
                throw new ProtocolException("Header name '%s' is invalid", name);
            }
            if (name.equalsIgnoreCase("Connection") || name.equalsIgnoreCase("Keep-Alive") || name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase("Upgrade")) {
                throw new ProtocolException("Header '%s: %s' is illegal for HTTP/2 messages", header.getName(), header.getValue());
            }
            headers.add(new BasicHeader(TextUtils.toLowerCase(name), value));
        }
        return headers;
    }
}

