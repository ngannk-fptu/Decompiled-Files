/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.nimbusds.oauth2.sdk.http;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.oauth2.sdk.util.X509CertificateUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ServletUtils {
    private static String reconstructRequestURLString(HttpServletRequest request) {
        String path;
        StringBuilder sb = new StringBuilder("http");
        if (request.isSecure()) {
            sb.append('s');
        }
        sb.append("://");
        String localAddress = request.getLocalAddr();
        if (localAddress != null && !localAddress.trim().isEmpty()) {
            if (localAddress.contains(".")) {
                sb.append(localAddress);
            } else if (localAddress.contains(":")) {
                sb.append('[');
                sb.append(localAddress);
                sb.append(']');
            }
        }
        if (!request.isSecure() && request.getLocalPort() != 80) {
            sb.append(':');
            sb.append(request.getLocalPort());
        }
        if (request.isSecure() && request.getLocalPort() != 443) {
            sb.append(':');
            sb.append(request.getLocalPort());
        }
        if ((path = request.getRequestURI()) != null) {
            sb.append(path);
        }
        return sb.toString();
    }

    public static HTTPRequest createHTTPRequest(HttpServletRequest sr) throws IOException {
        return ServletUtils.createHTTPRequest(sr, -1L);
    }

    public static HTTPRequest createHTTPRequest(HttpServletRequest sr, long maxEntityLength) throws IOException {
        URL url;
        HTTPRequest.Method method = HTTPRequest.Method.valueOf(sr.getMethod().toUpperCase());
        String urlString = ServletUtils.reconstructRequestURLString(sr);
        try {
            url = new URL(urlString);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid request URL: " + e.getMessage() + ": " + urlString, e);
        }
        HTTPRequest request = new HTTPRequest(method, url);
        try {
            request.setContentType(sr.getContentType());
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Content-Type header value: " + e.getMessage(), e);
        }
        Enumeration headerNames = sr.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            Enumeration headerValues = sr.getHeaders(headerName);
            if (headerValues == null || !headerValues.hasMoreElements()) continue;
            LinkedList headerValuesList = new LinkedList();
            while (headerValues.hasMoreElements()) {
                headerValuesList.add(headerValues.nextElement());
            }
            request.setHeader(headerName, headerValuesList.toArray(new String[0]));
        }
        if (method.equals((Object)HTTPRequest.Method.GET) || method.equals((Object)HTTPRequest.Method.DELETE)) {
            request.setQuery(sr.getQueryString());
        } else if (method.equals((Object)HTTPRequest.Method.POST) || method.equals((Object)HTTPRequest.Method.PUT)) {
            if (ContentType.APPLICATION_URLENCODED.matches(request.getEntityContentType())) {
                request.setQuery(URLUtils.serializeParametersAlt(sr.getParameterMap()));
            } else {
                int readChars;
                StringBuilder body = new StringBuilder(256);
                BufferedReader reader = sr.getReader();
                char[] cbuf = new char[256];
                while ((readChars = reader.read(cbuf)) != -1) {
                    body.append(cbuf, 0, readChars);
                    if (maxEntityLength <= 0L || (long)body.length() <= maxEntityLength) continue;
                    throw new IOException("Request entity body is too large, limit is " + maxEntityLength + " chars");
                }
                reader.close();
                request.setQuery(body.toString());
            }
        }
        X509Certificate cert = ServletUtils.extractClientX509Certificate((ServletRequest)sr);
        if (cert != null) {
            request.setClientX509Certificate(cert);
            request.setClientX509CertificateSubjectDN(cert.getSubjectDN() != null ? cert.getSubjectDN().getName() : null);
            if (X509CertificateUtils.hasMatchingIssuerAndSubject(cert)) {
                request.setClientX509CertificateRootDN(cert.getIssuerDN() != null ? cert.getIssuerDN().getName() : null);
            }
        }
        request.setClientIPAddress(sr.getRemoteAddr());
        return request;
    }

    public static void applyHTTPResponse(HTTPResponse httpResponse, HttpServletResponse servletResponse) throws IOException {
        servletResponse.setStatus(httpResponse.getStatusCode());
        for (Map.Entry header : httpResponse.getHeaderMap().entrySet()) {
            for (String headerValue : (List)header.getValue()) {
                servletResponse.addHeader((String)header.getKey(), headerValue);
            }
        }
        if (httpResponse.getEntityContentType() != null) {
            servletResponse.setContentType(httpResponse.getEntityContentType().toString());
        }
        if (httpResponse.getContent() != null) {
            PrintWriter writer = servletResponse.getWriter();
            writer.print(httpResponse.getContent());
            writer.close();
        }
    }

    public static X509Certificate extractClientX509Certificate(ServletRequest servletRequest) {
        X509Certificate[] certs = (X509Certificate[])servletRequest.getAttribute("javax.servlet.request.X509Certificate");
        if (certs == null || certs.length == 0) {
            return null;
        }
        return certs[0];
    }

    private ServletUtils() {
    }
}

