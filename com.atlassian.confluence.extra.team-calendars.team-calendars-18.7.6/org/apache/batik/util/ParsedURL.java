/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.batik.Version;
import org.apache.batik.util.ParsedURLData;
import org.apache.batik.util.ParsedURLDataProtocolHandler;
import org.apache.batik.util.ParsedURLDefaultProtocolHandler;
import org.apache.batik.util.ParsedURLJarProtocolHandler;
import org.apache.batik.util.ParsedURLProtocolHandler;
import org.apache.batik.util.Service;

public class ParsedURL {
    ParsedURLData data;
    String userAgent;
    private static Map handlersMap = null;
    private static ParsedURLProtocolHandler defaultHandler = new ParsedURLDefaultProtocolHandler();
    private static String globalUserAgent = "Batik/" + Version.getVersion();

    public static String getGlobalUserAgent() {
        return globalUserAgent;
    }

    public static void setGlobalUserAgent(String userAgent) {
        globalUserAgent = userAgent;
    }

    private static synchronized Map getHandlersMap() {
        if (handlersMap != null) {
            return handlersMap;
        }
        handlersMap = new HashMap();
        ParsedURL.registerHandler(new ParsedURLDataProtocolHandler());
        ParsedURL.registerHandler(new ParsedURLJarProtocolHandler());
        Iterator iter = Service.providers(ParsedURLProtocolHandler.class);
        while (iter.hasNext()) {
            ParsedURLProtocolHandler handler = (ParsedURLProtocolHandler)iter.next();
            ParsedURL.registerHandler(handler);
        }
        return handlersMap;
    }

    public static synchronized ParsedURLProtocolHandler getHandler(String protocol) {
        if (protocol == null) {
            return defaultHandler;
        }
        Map handlers = ParsedURL.getHandlersMap();
        ParsedURLProtocolHandler ret = (ParsedURLProtocolHandler)handlers.get(protocol);
        if (ret == null) {
            ret = defaultHandler;
        }
        return ret;
    }

    public static synchronized void registerHandler(ParsedURLProtocolHandler handler) {
        if (handler.getProtocolHandled() == null) {
            defaultHandler = handler;
            return;
        }
        Map handlers = ParsedURL.getHandlersMap();
        handlers.put(handler.getProtocolHandled(), handler);
    }

    public static InputStream checkGZIP(InputStream is) throws IOException {
        return ParsedURLData.checkGZIP(is);
    }

    public ParsedURL(String urlStr) {
        this.userAgent = ParsedURL.getGlobalUserAgent();
        this.data = ParsedURL.parseURL(urlStr);
    }

    public ParsedURL(URL url) {
        this.userAgent = ParsedURL.getGlobalUserAgent();
        this.data = new ParsedURLData(url);
    }

    public ParsedURL(String baseStr, String urlStr) {
        this.userAgent = ParsedURL.getGlobalUserAgent();
        this.data = baseStr != null ? ParsedURL.parseURL(baseStr, urlStr) : ParsedURL.parseURL(urlStr);
    }

    public ParsedURL(URL baseURL, String urlStr) {
        this.userAgent = ParsedURL.getGlobalUserAgent();
        this.data = baseURL != null ? ParsedURL.parseURL(new ParsedURL(baseURL), urlStr) : ParsedURL.parseURL(urlStr);
    }

    public ParsedURL(ParsedURL baseURL, String urlStr) {
        if (baseURL != null) {
            this.userAgent = baseURL.getUserAgent();
            this.data = ParsedURL.parseURL(baseURL, urlStr);
        } else {
            this.data = ParsedURL.parseURL(urlStr);
        }
    }

    public String toString() {
        return this.data.toString();
    }

    public String getPostConnectionURL() {
        return this.data.getPostConnectionURL();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ParsedURL)) {
            return false;
        }
        ParsedURL purl = (ParsedURL)obj;
        return this.data.equals(purl.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean complete() {
        return this.data.complete();
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getProtocol() {
        if (this.data.protocol == null) {
            return null;
        }
        return this.data.protocol;
    }

    public String getHost() {
        if (this.data.host == null) {
            return null;
        }
        return this.data.host;
    }

    public int getPort() {
        return this.data.port;
    }

    public String getPath() {
        if (this.data.path == null) {
            return null;
        }
        return this.data.path;
    }

    public String getRef() {
        if (this.data.ref == null) {
            return null;
        }
        return this.data.ref;
    }

    public String getPortStr() {
        return this.data.getPortStr();
    }

    public String getContentType() {
        return this.data.getContentType(this.userAgent);
    }

    public String getContentTypeMediaType() {
        return this.data.getContentTypeMediaType(this.userAgent);
    }

    public String getContentTypeCharset() {
        return this.data.getContentTypeCharset(this.userAgent);
    }

    public boolean hasContentTypeParameter(String param) {
        return this.data.hasContentTypeParameter(this.userAgent, param);
    }

    public String getContentEncoding() {
        return this.data.getContentEncoding(this.userAgent);
    }

    public InputStream openStream() throws IOException {
        return this.data.openStream(this.userAgent, null);
    }

    public InputStream openStream(String mimeType) throws IOException {
        ArrayList<String> mt = new ArrayList<String>(1);
        mt.add(mimeType);
        return this.data.openStream(this.userAgent, mt.iterator());
    }

    public InputStream openStream(String[] mimeTypes) throws IOException {
        ArrayList<String> mt = new ArrayList<String>(mimeTypes.length);
        for (String mimeType : mimeTypes) {
            mt.add(mimeType);
        }
        return this.data.openStream(this.userAgent, mt.iterator());
    }

    public InputStream openStream(Iterator mimeTypes) throws IOException {
        return this.data.openStream(this.userAgent, mimeTypes);
    }

    public InputStream openStreamRaw() throws IOException {
        return this.data.openStreamRaw(this.userAgent, null);
    }

    public InputStream openStreamRaw(String mimeType) throws IOException {
        ArrayList<String> mt = new ArrayList<String>(1);
        mt.add(mimeType);
        return this.data.openStreamRaw(this.userAgent, mt.iterator());
    }

    public InputStream openStreamRaw(String[] mimeTypes) throws IOException {
        ArrayList<String> mt = new ArrayList<String>(mimeTypes.length);
        mt.addAll(Arrays.asList(mimeTypes));
        return this.data.openStreamRaw(this.userAgent, mt.iterator());
    }

    public InputStream openStreamRaw(Iterator mimeTypes) throws IOException {
        return this.data.openStreamRaw(this.userAgent, mimeTypes);
    }

    public boolean sameFile(ParsedURL other) {
        return this.data.sameFile(other.data);
    }

    protected static String getProtocol(String urlStr) {
        if (urlStr == null) {
            return null;
        }
        int idx = 0;
        int len = urlStr.length();
        if (len == 0) {
            return null;
        }
        char ch = urlStr.charAt(idx);
        while (ch == '-' || ch == '+' || ch == '.' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
            if (++idx == len) {
                ch = '\u0000';
                break;
            }
            ch = urlStr.charAt(idx);
        }
        if (ch == ':') {
            return urlStr.substring(0, idx).toLowerCase();
        }
        return null;
    }

    public static ParsedURLData parseURL(String urlStr) {
        if (urlStr != null && !urlStr.contains(":") && !urlStr.startsWith("#")) {
            urlStr = "file:" + urlStr;
        }
        ParsedURLProtocolHandler handler = ParsedURL.getHandler(ParsedURL.getProtocol(urlStr));
        return handler.parseURL(urlStr);
    }

    public static ParsedURLData parseURL(String baseStr, String urlStr) {
        if (baseStr == null) {
            return ParsedURL.parseURL(urlStr);
        }
        ParsedURL purl = new ParsedURL(baseStr);
        return ParsedURL.parseURL(purl, urlStr);
    }

    public static ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        if (baseURL == null) {
            return ParsedURL.parseURL(urlStr);
        }
        String protocol = ParsedURL.getProtocol(urlStr);
        if (protocol == null) {
            protocol = baseURL.getProtocol();
        }
        ParsedURLProtocolHandler handler = ParsedURL.getHandler(protocol);
        return handler.parseURL(baseURL, urlStr);
    }
}

