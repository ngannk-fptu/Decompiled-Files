/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 */
package org.apache.hc.client5.http.impl.cookie;

import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieAttributeHandler;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.STATELESS)
public abstract class AbstractCookieAttributeHandler
implements CookieAttributeHandler {
    @Override
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
    }

    @Override
    public boolean match(Cookie cookie, CookieOrigin origin) {
        return true;
    }
}

