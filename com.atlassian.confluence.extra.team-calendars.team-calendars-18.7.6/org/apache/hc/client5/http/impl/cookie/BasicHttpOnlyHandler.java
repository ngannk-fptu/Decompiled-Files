/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.cookie;

import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.client5.http.cookie.SetCookie;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class BasicHttpOnlyHandler
implements CommonCookieAttributeHandler {
    public static final BasicHttpOnlyHandler INSTANCE = new BasicHttpOnlyHandler();

    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        cookie.setHttpOnly(true);
    }

    @Override
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
    }

    @Override
    public boolean match(Cookie cookie, CookieOrigin origin) {
        return true;
    }

    @Override
    public String getAttributeName() {
        return "httpOnly";
    }
}

