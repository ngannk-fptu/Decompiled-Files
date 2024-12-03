/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.httpclient.Header
 *  org.apache.commons.httpclient.HostConfiguration
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.HttpConnectionManager
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.ProxyHost
 *  org.apache.commons.httpclient.SimpleHttpConnectionManager
 *  org.apache.commons.httpclient.methods.EntityEnclosingMethod
 *  org.apache.commons.httpclient.methods.GetMethod
 *  org.apache.commons.httpclient.methods.InputStreamRequestEntity
 *  org.apache.commons.httpclient.methods.PostMethod
 *  org.apache.commons.httpclient.methods.RequestEntity
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.tuckey.web.filters.urlrewrite.RequestProxyCustomRequestEntity;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class RequestProxy {
    private static final Log log = Log.getLog(RequestProxy.class);

    public static void execute(String target, HttpServletRequest hsRequest, HttpServletResponse hsResponse) throws IOException {
        int result;
        URL url;
        log.info("execute, target is " + target);
        log.info("response commit state: " + hsResponse.isCommitted());
        if (StringUtils.isBlank(target)) {
            log.error("The target address is not given. Please provide a target address.");
            return;
        }
        log.info("checking url");
        try {
            url = new URL(target);
        }
        catch (MalformedURLException e) {
            log.error("The provided target url is not valid.", e);
            return;
        }
        log.info("seting up the host configuration");
        HostConfiguration config = new HostConfiguration();
        ProxyHost proxyHost = RequestProxy.getUseProxyServer((String)hsRequest.getAttribute("use-proxy"));
        if (proxyHost != null) {
            config.setProxyHost(proxyHost);
        }
        int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
        config.setHost(url.getHost(), port, url.getProtocol());
        log.info("config is " + config.toString());
        HttpMethod targetRequest = RequestProxy.setupProxyRequest(hsRequest, url);
        if (targetRequest == null) {
            log.error("Unsupported request method found: " + hsRequest.getMethod());
            return;
        }
        HttpClient client = new HttpClient((HttpConnectionManager)new SimpleHttpConnectionManager());
        if (log.isInfoEnabled()) {
            log.info("client state" + client.getState());
            log.info("client params" + client.getParams().toString());
            log.info("executeMethod / fetching data ...");
        }
        if (targetRequest instanceof EntityEnclosingMethod) {
            RequestProxyCustomRequestEntity requestEntity = new RequestProxyCustomRequestEntity((InputStream)hsRequest.getInputStream(), hsRequest.getContentLength(), hsRequest.getContentType());
            EntityEnclosingMethod entityEnclosingMethod = (EntityEnclosingMethod)targetRequest;
            entityEnclosingMethod.setRequestEntity((RequestEntity)requestEntity);
            result = client.executeMethod(config, (HttpMethod)entityEnclosingMethod);
        } else {
            result = client.executeMethod(config, targetRequest);
        }
        RequestProxy.setupResponseHeaders(targetRequest, hsResponse);
        InputStream originalResponseStream = targetRequest.getResponseBodyAsStream();
        if (originalResponseStream != null) {
            ServletOutputStream responseStream = hsResponse.getOutputStream();
            RequestProxy.copyStream(originalResponseStream, (OutputStream)responseStream);
        }
        log.info("set up response, result code was " + result);
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        int count;
        byte[] buf = new byte[65536];
        while ((count = in.read(buf)) != -1) {
            out.write(buf, 0, count);
        }
    }

    public static ProxyHost getUseProxyServer(String useProxyServer) {
        ProxyHost proxyHost = null;
        if (useProxyServer != null) {
            String proxyHostStr = useProxyServer;
            int colonIdx = proxyHostStr.indexOf(58);
            if (colonIdx != -1) {
                proxyHostStr = proxyHostStr.substring(0, colonIdx);
                String proxyPortStr = useProxyServer.substring(colonIdx + 1);
                if (proxyPortStr != null && proxyPortStr.length() > 0 && proxyPortStr.matches("[0-9]+")) {
                    int proxyPort = Integer.parseInt(proxyPortStr);
                    proxyHost = new ProxyHost(proxyHostStr, proxyPort);
                } else {
                    proxyHost = new ProxyHost(proxyHostStr);
                }
            } else {
                proxyHost = new ProxyHost(proxyHostStr);
            }
        }
        return proxyHost;
    }

    private static HttpMethod setupProxyRequest(HttpServletRequest hsRequest, URL targetUrl) throws IOException {
        GetMethod method;
        String methodName = hsRequest.getMethod();
        if ("POST".equalsIgnoreCase(methodName)) {
            PostMethod postMethod = new PostMethod();
            InputStreamRequestEntity inputStreamRequestEntity = new InputStreamRequestEntity((InputStream)hsRequest.getInputStream());
            postMethod.setRequestEntity((RequestEntity)inputStreamRequestEntity);
            method = postMethod;
        } else if ("GET".equalsIgnoreCase(methodName)) {
            method = new GetMethod();
        } else {
            log.warn("Unsupported HTTP method requested: " + hsRequest.getMethod());
            return null;
        }
        method.setFollowRedirects(false);
        method.setPath(targetUrl.getPath());
        method.setQueryString(targetUrl.getQuery());
        Enumeration e = hsRequest.getHeaderNames();
        if (e != null) {
            while (e.hasMoreElements()) {
                String headerName = (String)e.nextElement();
                if ("host".equalsIgnoreCase(headerName) || "content-length".equalsIgnoreCase(headerName) || "accept-encoding".equalsIgnoreCase(headerName) || headerName.toLowerCase().startsWith("cookie")) continue;
                Enumeration values = hsRequest.getHeaders(headerName);
                while (values.hasMoreElements()) {
                    String headerValue = (String)values.nextElement();
                    log.info("setting proxy request parameter:" + headerName + ", value: " + headerValue);
                    method.addRequestHeader(headerName, headerValue);
                }
            }
        }
        log.info("proxy query string " + method.getQueryString());
        return method;
    }

    private static void setupResponseHeaders(HttpMethod httpMethod, HttpServletResponse hsResponse) {
        if (log.isInfoEnabled()) {
            log.info("setupResponseHeaders");
            log.info("status text: " + httpMethod.getStatusText());
            log.info("status line: " + httpMethod.getStatusLine());
        }
        for (int i = 0; i < httpMethod.getResponseHeaders().length; ++i) {
            Header h = httpMethod.getResponseHeaders()[i];
            if ("content-encoding".equalsIgnoreCase(h.getName()) || "content-length".equalsIgnoreCase(h.getName()) || "transfer-encoding".equalsIgnoreCase(h.getName()) || h.getName().toLowerCase().startsWith("cookie") || h.getName().toLowerCase().startsWith("set-cookie")) continue;
            hsResponse.addHeader(h.getName(), h.getValue());
            log.info("setting response parameter:" + h.getName() + ", value: " + h.getValue());
        }
        if (httpMethod.getStatusCode() != 200) {
            hsResponse.setStatus(httpMethod.getStatusCode());
        }
    }
}

