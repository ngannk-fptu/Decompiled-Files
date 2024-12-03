/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.SetCookie2;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RFC2965DiscardAttributeHandler
implements CommonCookieAttributeHandler {
    @Override
    public void parse(SetCookie cookie, String commenturl) throws MalformedCookieException {
        if (cookie instanceof SetCookie2) {
            SetCookie2 cookie2 = (SetCookie2)cookie;
            cookie2.setDiscard(true);
        }
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
        return "discard";
    }
}

