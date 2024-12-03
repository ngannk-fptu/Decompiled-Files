/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import java.util.Collection;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.MalformedCookieException;

public class IgnoreCookiesSpec
implements CookieSpec {
    @Override
    public Cookie[] parse(String host, int port, String path, boolean secure, String header) throws MalformedCookieException {
        return new Cookie[0];
    }

    @Override
    public Collection getValidDateFormats() {
        return null;
    }

    @Override
    public void setValidDateFormats(Collection datepatterns) {
    }

    @Override
    public String formatCookie(Cookie cookie) {
        return null;
    }

    @Override
    public Header formatCookieHeader(Cookie cookie) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Header formatCookieHeader(Cookie[] cookies) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String formatCookies(Cookie[] cookies) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean match(String host, int port, String path, boolean secure, Cookie cookie) {
        return false;
    }

    @Override
    public Cookie[] match(String host, int port, String path, boolean secure, Cookie[] cookies) {
        return new Cookie[0];
    }

    @Override
    public Cookie[] parse(String host, int port, String path, boolean secure, Header header) throws MalformedCookieException, IllegalArgumentException {
        return new Cookie[0];
    }

    @Override
    public void parseAttribute(NameValuePair attribute, Cookie cookie) throws MalformedCookieException, IllegalArgumentException {
    }

    @Override
    public void validate(String host, int port, String path, boolean secure, Cookie cookie) throws MalformedCookieException, IllegalArgumentException {
    }

    @Override
    public boolean domainMatch(String host, String domain) {
        return false;
    }

    @Override
    public boolean pathMatch(String path, String topmostPath) {
        return false;
    }
}

