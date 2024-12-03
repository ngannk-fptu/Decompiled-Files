/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.cookie.CookieOrigin;
import org.apache.commons.httpclient.cookie.MalformedCookieException;

public interface CookieAttributeHandler {
    public void parse(Cookie var1, String var2) throws MalformedCookieException;

    public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException;

    public boolean match(Cookie var1, CookieOrigin var2);
}

