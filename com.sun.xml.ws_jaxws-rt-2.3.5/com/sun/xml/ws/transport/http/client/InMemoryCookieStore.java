/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.client;

import com.sun.xml.ws.transport.http.client.CookieStore;
import com.sun.xml.ws.transport.http.client.HttpCookie;
import com.sun.xml.ws.transport.http.client.HttpTransportPipe;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

class InMemoryCookieStore
implements CookieStore {
    private static final Logger LOGGER = Logger.getLogger(HttpTransportPipe.class.getName());
    private List<HttpCookie> cookieJar = new ArrayList<HttpCookie>();
    private Map<String, List<HttpCookie>> domainIndex = new HashMap<String, List<HttpCookie>>();
    private Map<URI, List<HttpCookie>> uriIndex = new HashMap<URI, List<HttpCookie>>();
    private ReentrantLock lock = new ReentrantLock(false);

    InMemoryCookieStore() {
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie is null");
        }
        this.lock.lock();
        try {
            this.cookieJar.remove(cookie);
            if (cookie.getMaxAge() != 0L) {
                this.cookieJar.add(cookie);
                if (cookie.getDomain() != null) {
                    this.addIndex(this.domainIndex, cookie.getDomain(), cookie);
                }
                this.addIndex(this.uriIndex, this.getEffectiveURI(uri), cookie);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<HttpCookie> get(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri is null");
        }
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
        boolean secureLink = "https".equalsIgnoreCase(uri.getScheme());
        this.lock.lock();
        try {
            this.getInternal1(cookies, this.domainIndex, uri.getHost(), secureLink);
            this.getInternal2(cookies, this.uriIndex, this.getEffectiveURI(uri), secureLink);
        }
        finally {
            this.lock.unlock();
        }
        return cookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
        List<HttpCookie> rt;
        this.lock.lock();
        try {
            Iterator<HttpCookie> it = this.cookieJar.iterator();
            while (it.hasNext()) {
                if (!it.next().hasExpired()) continue;
                it.remove();
            }
            rt = Collections.unmodifiableList(this.cookieJar);
        }
        catch (Exception e) {
            rt = Collections.unmodifiableList(this.cookieJar);
            LOGGER.log(Level.INFO, null, e);
        }
        finally {
            this.lock.unlock();
        }
        return rt;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<URI> getURIs() {
        ArrayList<URI> uris;
        this.lock.lock();
        try {
            Iterator<URI> it = this.uriIndex.keySet().iterator();
            while (it.hasNext()) {
                URI uri = it.next();
                List<HttpCookie> cookies = this.uriIndex.get(uri);
                if (cookies != null && !cookies.isEmpty()) continue;
                it.remove();
            }
            uris = new ArrayList<URI>(this.uriIndex.keySet());
        }
        catch (Exception e) {
            uris = new ArrayList<URI>(this.uriIndex.keySet());
            LOGGER.log(Level.INFO, null, e);
        }
        finally {
            this.lock.unlock();
        }
        return uris;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(URI uri, HttpCookie ck) {
        if (ck == null) {
            throw new NullPointerException("cookie is null");
        }
        boolean modified = false;
        this.lock.lock();
        try {
            modified = this.cookieJar.remove(ck);
        }
        finally {
            this.lock.unlock();
        }
        return modified;
    }

    @Override
    public boolean removeAll() {
        this.lock.lock();
        try {
            this.cookieJar.clear();
            this.domainIndex.clear();
            this.uriIndex.clear();
        }
        finally {
            this.lock.unlock();
        }
        return true;
    }

    private boolean netscapeDomainMatches(String domain, String host) {
        if (domain == null || host == null) {
            return false;
        }
        boolean isLocalDomain = ".local".equalsIgnoreCase(domain);
        int embeddedDotInDomain = domain.indexOf(46);
        if (embeddedDotInDomain == 0) {
            embeddedDotInDomain = domain.indexOf(46, 1);
        }
        if (!(isLocalDomain || embeddedDotInDomain != -1 && embeddedDotInDomain != domain.length() - 1)) {
            return false;
        }
        int firstDotInHost = host.indexOf(46);
        if (firstDotInHost == -1 && isLocalDomain) {
            return true;
        }
        int domainLength = domain.length();
        int lengthDiff = host.length() - domainLength;
        if (lengthDiff == 0) {
            return host.equalsIgnoreCase(domain);
        }
        if (lengthDiff > 0) {
            String D = host.substring(lengthDiff);
            return D.equalsIgnoreCase(domain);
        }
        if (lengthDiff == -1) {
            return domain.charAt(0) == '.' && host.equalsIgnoreCase(domain.substring(1));
        }
        return false;
    }

    private void getInternal1(List<HttpCookie> cookies, Map<String, List<HttpCookie>> cookieIndex, String host, boolean secureLink) {
        ArrayList<HttpCookie> toRemove = new ArrayList<HttpCookie>();
        for (Map.Entry<String, List<HttpCookie>> entry : cookieIndex.entrySet()) {
            String domain = entry.getKey();
            List<HttpCookie> lst = entry.getValue();
            for (HttpCookie c : lst) {
                if ((c.getVersion() != 0 || !this.netscapeDomainMatches(domain, host)) && (c.getVersion() != 1 || !HttpCookie.domainMatches(domain, host))) continue;
                if (this.cookieJar.indexOf(c) != -1) {
                    if (!c.hasExpired()) {
                        if (!secureLink && c.getSecure() || cookies.contains(c)) continue;
                        cookies.add(c);
                        continue;
                    }
                    toRemove.add(c);
                    continue;
                }
                toRemove.add(c);
            }
            for (HttpCookie c : toRemove) {
                lst.remove(c);
                this.cookieJar.remove(c);
            }
            toRemove.clear();
        }
    }

    private <T> void getInternal2(List<HttpCookie> cookies, Map<T, List<HttpCookie>> cookieIndex, Comparable<T> comparator, boolean secureLink) {
        for (Map.Entry<T, List<HttpCookie>> entry : cookieIndex.entrySet()) {
            List<HttpCookie> indexedCookies;
            T index = entry.getKey();
            if (comparator.compareTo(index) != 0 || (indexedCookies = entry.getValue()) == null) continue;
            Iterator<HttpCookie> it = indexedCookies.iterator();
            while (it.hasNext()) {
                HttpCookie ck = it.next();
                if (this.cookieJar.indexOf(ck) != -1) {
                    if (!ck.hasExpired()) {
                        if (!secureLink && ck.getSecure() || cookies.contains(ck)) continue;
                        cookies.add(ck);
                        continue;
                    }
                    it.remove();
                    this.cookieJar.remove(ck);
                    continue;
                }
                it.remove();
            }
        }
    }

    private <T> void addIndex(Map<T, List<HttpCookie>> indexStore, T index, HttpCookie cookie) {
        if (index != null) {
            List<HttpCookie> cookies = indexStore.get(index);
            if (cookies != null) {
                cookies.remove(cookie);
                cookies.add(cookie);
            } else {
                cookies = new ArrayList<HttpCookie>();
                cookies.add(cookie);
                indexStore.put(index, cookies);
            }
        }
    }

    private URI getEffectiveURI(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI("http", uri.getHost(), null, null, null);
        }
        catch (URISyntaxException ignored) {
            effectiveURI = uri;
        }
        return effectiveURI;
    }
}

