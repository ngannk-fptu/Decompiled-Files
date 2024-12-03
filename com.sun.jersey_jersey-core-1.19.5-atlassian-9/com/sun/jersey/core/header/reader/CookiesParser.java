/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Cookie
 *  javax.ws.rs.core.NewCookie
 */
package com.sun.jersey.core.header.reader;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

class CookiesParser {
    CookiesParser() {
    }

    public static Map<String, Cookie> parseCookies(String header) {
        String[] bites = header.split("[;,]");
        LinkedHashMap<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();
        int version = 0;
        MutableCookie cookie = null;
        for (String bite : bites) {
            String value;
            String[] crumbs = bite.split("=", 2);
            String name = crumbs.length > 0 ? crumbs[0].trim() : "";
            String string = value = crumbs.length > 1 ? crumbs[1].trim() : "";
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            if (!name.startsWith("$")) {
                if (cookie != null) {
                    cookies.put(cookie.name, cookie.getImmutableCookie());
                }
                cookie = new MutableCookie(name, value);
                cookie.version = version;
                continue;
            }
            if (name.startsWith("$Version")) {
                version = Integer.parseInt(value);
                continue;
            }
            if (name.startsWith("$Path") && cookie != null) {
                cookie.path = value;
                continue;
            }
            if (!name.startsWith("$Domain") || cookie == null) continue;
            cookie.domain = value;
        }
        if (cookie != null) {
            cookies.put(cookie.name, cookie.getImmutableCookie());
        }
        return cookies;
    }

    public static Cookie parseCookie(String header) {
        Map<String, Cookie> cookies = CookiesParser.parseCookies(header);
        return cookies.entrySet().iterator().next().getValue();
    }

    public static NewCookie parseNewCookie(String header) {
        String[] bites = header.split("[;,]");
        MutableNewCookie cookie = null;
        for (String bite : bites) {
            String value;
            String[] crumbs = bite.split("=", 2);
            String name = crumbs.length > 0 ? crumbs[0].trim() : "";
            String string = value = crumbs.length > 1 ? crumbs[1].trim() : "";
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            if (cookie == null) {
                cookie = new MutableNewCookie(name, value);
                continue;
            }
            if (name.startsWith("Comment")) {
                cookie.comment = value;
                continue;
            }
            if (name.startsWith("Domain")) {
                cookie.domain = value;
                continue;
            }
            if (name.startsWith("Max-Age")) {
                cookie.maxAge = Integer.parseInt(value);
                continue;
            }
            if (name.startsWith("Path")) {
                cookie.path = value;
                continue;
            }
            if (name.startsWith("Secure")) {
                cookie.secure = true;
                continue;
            }
            if (name.startsWith("Version")) {
                cookie.version = Integer.parseInt(value);
                continue;
            }
            if (!name.startsWith("Domain")) continue;
            cookie.domain = value;
        }
        return cookie.getImmutableNewCookie();
    }

    private static class MutableNewCookie {
        String name = null;
        String value = null;
        String path = null;
        String domain = null;
        int version = 1;
        String comment = null;
        int maxAge = -1;
        boolean secure = false;

        public MutableNewCookie(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public NewCookie getImmutableNewCookie() {
            return new NewCookie(this.name, this.value, this.path, this.domain, this.version, this.comment, this.maxAge, this.secure);
        }
    }

    private static class MutableCookie {
        String name;
        String value;
        int version = 1;
        String path = null;
        String domain = null;

        public MutableCookie(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public Cookie getImmutableCookie() {
            return new Cookie(this.name, this.value, this.path, this.domain, this.version);
        }
    }
}

