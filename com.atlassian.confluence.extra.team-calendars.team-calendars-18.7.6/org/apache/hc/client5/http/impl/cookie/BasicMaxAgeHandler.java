/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.cookie;

import java.time.Instant;
import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.client5.http.cookie.SetCookie;
import org.apache.hc.client5.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class BasicMaxAgeHandler
extends AbstractCookieAttributeHandler
implements CommonCookieAttributeHandler {
    public static final BasicMaxAgeHandler INSTANCE = new BasicMaxAgeHandler();

    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        int age;
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for 'max-age' attribute");
        }
        try {
            age = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new MalformedCookieException("Invalid 'max-age' attribute: " + value);
        }
        if (age < 0) {
            throw new MalformedCookieException("Negative 'max-age' attribute: " + value);
        }
        cookie.setExpiryDate(Instant.now().plusSeconds(age));
    }

    @Override
    public String getAttributeName() {
        return "max-age";
    }
}

