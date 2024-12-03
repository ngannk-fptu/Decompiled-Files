/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.client;

import com.sun.xml.ws.transport.http.client.CookiePolicy;
import com.sun.xml.ws.transport.http.client.CookieStore;
import com.sun.xml.ws.transport.http.client.HttpCookie;
import com.sun.xml.ws.transport.http.client.InMemoryCookieStore;
import java.io.IOException;
import java.io.Serializable;
import java.net.CookieHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieManager
extends CookieHandler {
    private CookiePolicy policyCallback;
    private CookieStore cookieJar = null;

    public CookieManager() {
        this(null, null);
    }

    public CookieManager(CookieStore store, CookiePolicy cookiePolicy) {
        this.policyCallback = cookiePolicy == null ? CookiePolicy.ACCEPT_ORIGINAL_SERVER : cookiePolicy;
        this.cookieJar = store == null ? new InMemoryCookieStore() : store;
    }

    public void setCookiePolicy(CookiePolicy cookiePolicy) {
        if (cookiePolicy != null) {
            this.policyCallback = cookiePolicy;
        }
    }

    public CookieStore getCookieStore() {
        return this.cookieJar;
    }

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        if (uri == null || requestHeaders == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        HashMap<String, List<String>> cookieMap = new HashMap<String, List<String>>();
        if (this.cookieJar == null) {
            return Collections.unmodifiableMap(cookieMap);
        }
        boolean secureLink = "https".equalsIgnoreCase(uri.getScheme());
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
        String path = uri.getPath();
        if (path == null || path.length() == 0) {
            path = "/";
        }
        for (HttpCookie cookie : this.cookieJar.get(uri)) {
            String s;
            if (!this.pathMatches(path, cookie.getPath()) || !secureLink && cookie.getSecure() || cookie.isHttpOnly() && !"http".equalsIgnoreCase(s = uri.getScheme()) && !"https".equalsIgnoreCase(s)) continue;
            String ports = cookie.getPortlist();
            if (ports != null && ports.length() != 0) {
                int port = uri.getPort();
                if (port == -1) {
                    int n = port = "https".equals(uri.getScheme()) ? 443 : 80;
                }
                if (!CookieManager.isInPortList(ports, port)) continue;
                cookies.add(cookie);
                continue;
            }
            cookies.add(cookie);
        }
        List<String> cookieHeader = this.sortByPath(cookies);
        cookieMap.put("Cookie", cookieHeader);
        return Collections.unmodifiableMap(cookieMap);
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        if (uri == null || responseHeaders == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        if (this.cookieJar == null) {
            return;
        }
        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            String headerKey = entry.getKey();
            if (headerKey == null || !headerKey.equalsIgnoreCase("Set-Cookie2") && !headerKey.equalsIgnoreCase("Set-Cookie")) continue;
            for (String headerValue : entry.getValue()) {
                try {
                    List<HttpCookie> cookies = HttpCookie.parse(headerValue);
                    for (HttpCookie cookie : cookies) {
                        String ports;
                        if (cookie.getPath() == null) {
                            String path = uri.getPath();
                            if (!path.endsWith("/")) {
                                int i = path.lastIndexOf("/");
                                path = i > 0 ? path.substring(0, i + 1) : "/";
                            }
                            cookie.setPath(path);
                        }
                        if (cookie.getDomain() == null) {
                            cookie.setDomain(uri.getHost());
                        }
                        if ((ports = cookie.getPortlist()) != null) {
                            int port = uri.getPort();
                            if (port == -1) {
                                int n = port = "https".equals(uri.getScheme()) ? 443 : 80;
                            }
                            if (ports.length() == 0) {
                                cookie.setPortlist("" + port);
                                if (!this.shouldAcceptInternal(uri, cookie)) continue;
                                this.cookieJar.add(uri, cookie);
                                continue;
                            }
                            if (!CookieManager.isInPortList(ports, port) || !this.shouldAcceptInternal(uri, cookie)) continue;
                            this.cookieJar.add(uri, cookie);
                            continue;
                        }
                        if (!this.shouldAcceptInternal(uri, cookie)) continue;
                        this.cookieJar.add(uri, cookie);
                    }
                }
                catch (IllegalArgumentException illegalArgumentException) {
                }
            }
        }
    }

    private boolean shouldAcceptInternal(URI uri, HttpCookie cookie) {
        try {
            return this.policyCallback.shouldAccept(uri, cookie);
        }
        catch (Exception ignored) {
            return false;
        }
    }

    private static boolean isInPortList(String lst, int port) {
        int i = lst.indexOf(",");
        int val = -1;
        while (i > 0) {
            try {
                val = Integer.parseInt(lst.substring(0, i));
                if (val == port) {
                    return true;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            lst = lst.substring(i + 1);
            i = lst.indexOf(",");
        }
        if (lst.length() != 0) {
            try {
                val = Integer.parseInt(lst);
                if (val == port) {
                    return true;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return false;
    }

    private boolean pathMatches(String path, String pathToMatchWith) {
        if (path == pathToMatchWith) {
            return true;
        }
        if (path == null || pathToMatchWith == null) {
            return false;
        }
        return path.startsWith(pathToMatchWith);
    }

    private List<String> sortByPath(List<HttpCookie> cookies) {
        Collections.sort(cookies, new CookiePathComparator());
        ArrayList<String> cookieHeader = new ArrayList<String>();
        for (HttpCookie cookie : cookies) {
            if (cookies.indexOf(cookie) == 0 && cookie.getVersion() > 0) {
                cookieHeader.add("$Version=\"1\"");
            }
            cookieHeader.add(cookie.toString());
        }
        return cookieHeader;
    }

    static class CookiePathComparator
    implements Comparator<HttpCookie>,
    Serializable {
        CookiePathComparator() {
        }

        @Override
        public int compare(HttpCookie c1, HttpCookie c2) {
            if (c1 == c2) {
                return 0;
            }
            if (c1 == null) {
                return -1;
            }
            if (c2 == null) {
                return 1;
            }
            if (!c1.getName().equals(c2.getName())) {
                return 0;
            }
            if (c1.getPath().startsWith(c2.getPath())) {
                return -1;
            }
            if (c2.getPath().startsWith(c1.getPath())) {
                return 1;
            }
            return 0;
        }
    }
}

