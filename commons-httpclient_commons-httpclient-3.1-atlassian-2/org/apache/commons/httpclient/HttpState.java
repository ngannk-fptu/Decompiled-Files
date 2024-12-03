/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpState {
    protected HashMap credMap = new HashMap();
    protected HashMap proxyCred = new HashMap();
    protected ArrayList cookies = new ArrayList();
    private boolean preemptive = false;
    private int cookiePolicy = -1;
    public static final String PREEMPTIVE_PROPERTY = "httpclient.authentication.preemptive";
    public static final String PREEMPTIVE_DEFAULT = "false";
    private static final Log LOG = LogFactory.getLog(HttpState.class);

    public synchronized void addCookie(Cookie cookie) {
        LOG.trace((Object)"enter HttpState.addCookie(Cookie)");
        if (cookie != null) {
            Iterator it = this.cookies.iterator();
            while (it.hasNext()) {
                Cookie tmp = (Cookie)it.next();
                if (!cookie.equals(tmp)) continue;
                it.remove();
                break;
            }
            if (!cookie.isExpired()) {
                this.cookies.add(cookie);
            }
        }
    }

    public synchronized void addCookies(Cookie[] cookies) {
        LOG.trace((Object)"enter HttpState.addCookies(Cookie[])");
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                this.addCookie(cookies[i]);
            }
        }
    }

    public synchronized Cookie[] getCookies() {
        LOG.trace((Object)"enter HttpState.getCookies()");
        return this.cookies.toArray(new Cookie[this.cookies.size()]);
    }

    public synchronized Cookie[] getCookies(String domain, int port, String path, boolean secure) {
        LOG.trace((Object)"enter HttpState.getCookies(String, int, String, boolean)");
        CookieSpec matcher = CookiePolicy.getDefaultSpec();
        ArrayList<Cookie> list = new ArrayList<Cookie>(this.cookies.size());
        int m = this.cookies.size();
        for (int i = 0; i < m; ++i) {
            Cookie cookie = (Cookie)this.cookies.get(i);
            if (!matcher.match(domain, port, path, secure, cookie)) continue;
            list.add(cookie);
        }
        return list.toArray(new Cookie[list.size()]);
    }

    public synchronized boolean purgeExpiredCookies() {
        LOG.trace((Object)"enter HttpState.purgeExpiredCookies()");
        return this.purgeExpiredCookies(new Date());
    }

    public synchronized boolean purgeExpiredCookies(Date date) {
        LOG.trace((Object)"enter HttpState.purgeExpiredCookies(Date)");
        boolean removed = false;
        Iterator it = this.cookies.iterator();
        while (it.hasNext()) {
            if (!((Cookie)it.next()).isExpired(date)) continue;
            it.remove();
            removed = true;
        }
        return removed;
    }

    public int getCookiePolicy() {
        return this.cookiePolicy;
    }

    public void setAuthenticationPreemptive(boolean value) {
        this.preemptive = value;
    }

    public boolean isAuthenticationPreemptive() {
        return this.preemptive;
    }

    public void setCookiePolicy(int policy) {
        this.cookiePolicy = policy;
    }

    public synchronized void setCredentials(String realm, String host, Credentials credentials) {
        LOG.trace((Object)"enter HttpState.setCredentials(String, String, Credentials)");
        this.credMap.put(new AuthScope(host, -1, realm, AuthScope.ANY_SCHEME), credentials);
    }

    public synchronized void setCredentials(AuthScope authscope, Credentials credentials) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        LOG.trace((Object)"enter HttpState.setCredentials(AuthScope, Credentials)");
        this.credMap.put(authscope, credentials);
    }

    private static Credentials matchCredentials(HashMap map, AuthScope authscope) {
        Credentials creds = (Credentials)map.get(authscope);
        if (creds == null) {
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (AuthScope current : map.keySet()) {
                int factor = authscope.match(current);
                if (factor <= bestMatchFactor) continue;
                bestMatchFactor = factor;
                bestMatch = current;
            }
            if (bestMatch != null) {
                creds = (Credentials)map.get(bestMatch);
            }
        }
        return creds;
    }

    public synchronized Credentials getCredentials(String realm, String host) {
        LOG.trace((Object)"enter HttpState.getCredentials(String, String");
        return HttpState.matchCredentials(this.credMap, new AuthScope(host, -1, realm, AuthScope.ANY_SCHEME));
    }

    public synchronized Credentials getCredentials(AuthScope authscope) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        LOG.trace((Object)"enter HttpState.getCredentials(AuthScope)");
        return HttpState.matchCredentials(this.credMap, authscope);
    }

    public synchronized void setProxyCredentials(String realm, String proxyHost, Credentials credentials) {
        LOG.trace((Object)"enter HttpState.setProxyCredentials(String, String, Credentials");
        this.proxyCred.put(new AuthScope(proxyHost, -1, realm, AuthScope.ANY_SCHEME), credentials);
    }

    public synchronized void setProxyCredentials(AuthScope authscope, Credentials credentials) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        LOG.trace((Object)"enter HttpState.setProxyCredentials(AuthScope, Credentials)");
        this.proxyCred.put(authscope, credentials);
    }

    public synchronized Credentials getProxyCredentials(String realm, String proxyHost) {
        LOG.trace((Object)"enter HttpState.getCredentials(String, String");
        return HttpState.matchCredentials(this.proxyCred, new AuthScope(proxyHost, -1, realm, AuthScope.ANY_SCHEME));
    }

    public synchronized Credentials getProxyCredentials(AuthScope authscope) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        LOG.trace((Object)"enter HttpState.getProxyCredentials(AuthScope)");
        return HttpState.matchCredentials(this.proxyCred, authscope);
    }

    public synchronized String toString() {
        StringBuffer sbResult = new StringBuffer();
        sbResult.append("[");
        sbResult.append(HttpState.getCredentialsStringRepresentation(this.proxyCred));
        sbResult.append(" | ");
        sbResult.append(HttpState.getCredentialsStringRepresentation(this.credMap));
        sbResult.append(" | ");
        sbResult.append(HttpState.getCookiesStringRepresentation(this.cookies));
        sbResult.append("]");
        String strResult = sbResult.toString();
        return strResult;
    }

    private static String getCredentialsStringRepresentation(Map credMap) {
        StringBuffer sbResult = new StringBuffer();
        for (Object key : credMap.keySet()) {
            Credentials cred = (Credentials)credMap.get(key);
            if (sbResult.length() > 0) {
                sbResult.append(", ");
            }
            sbResult.append(key);
            sbResult.append("#");
            sbResult.append(cred.toString());
        }
        return sbResult.toString();
    }

    private static String getCookiesStringRepresentation(List cookies) {
        StringBuffer sbResult = new StringBuffer();
        for (Cookie ck : cookies) {
            if (sbResult.length() > 0) {
                sbResult.append("#");
            }
            sbResult.append(ck.toExternalForm());
        }
        return sbResult.toString();
    }

    public void clearCredentials() {
        this.credMap.clear();
    }

    public void clearProxyCredentials() {
        this.proxyCred.clear();
    }

    public synchronized void clearCookies() {
        this.cookies.clear();
    }

    public void clear() {
        this.clearCookies();
        this.clearCredentials();
        this.clearProxyCredentials();
    }
}

