/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.client;

import com.sun.xml.ws.transport.http.client.HttpCookie;
import java.net.URI;

public interface CookiePolicy {
    public static final CookiePolicy ACCEPT_ALL = new CookiePolicy(){

        @Override
        public boolean shouldAccept(URI uri, HttpCookie cookie) {
            return true;
        }
    };
    public static final CookiePolicy ACCEPT_NONE = new CookiePolicy(){

        @Override
        public boolean shouldAccept(URI uri, HttpCookie cookie) {
            return false;
        }
    };
    public static final CookiePolicy ACCEPT_ORIGINAL_SERVER = new CookiePolicy(){

        @Override
        public boolean shouldAccept(URI uri, HttpCookie cookie) {
            return HttpCookie.domainMatches(cookie.getDomain(), uri.getHost());
        }
    };

    public boolean shouldAccept(URI var1, HttpCookie var2);
}

