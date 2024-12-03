/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.cookie;

import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.client5.http.cookie.SetCookie;
import org.apache.hc.client5.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class BasicSecureHandler
extends AbstractCookieAttributeHandler
implements CommonCookieAttributeHandler {
    public static final BasicSecureHandler INSTANCE = new BasicSecureHandler();

    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        Args.notNull((Object)cookie, (String)"Cookie");
        cookie.setSecure(true);
    }

    @Override
    public boolean match(Cookie cookie, CookieOrigin origin) {
        Args.notNull((Object)cookie, (String)"Cookie");
        Args.notNull((Object)origin, (String)"Cookie origin");
        return !cookie.isSecure() || origin.isSecure();
    }

    @Override
    public String getAttributeName() {
        return "secure";
    }
}

