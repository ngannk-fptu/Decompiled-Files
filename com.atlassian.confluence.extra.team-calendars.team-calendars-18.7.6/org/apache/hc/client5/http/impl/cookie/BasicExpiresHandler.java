/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.cookie;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.client5.http.cookie.SetCookie;
import org.apache.hc.client5.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class BasicExpiresHandler
extends AbstractCookieAttributeHandler
implements CommonCookieAttributeHandler {
    private final DateTimeFormatter[] datePatterns;

    public BasicExpiresHandler(DateTimeFormatter ... datePatterns) {
        this.datePatterns = datePatterns;
    }

    @Deprecated
    public BasicExpiresHandler(String[] datePatterns) {
        Args.notNull(datePatterns, "Array of date patterns");
        this.datePatterns = new DateTimeFormatter[datePatterns.length];
        for (int i = 0; i < datePatterns.length; ++i) {
            this.datePatterns[i] = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern(datePatterns[i]).toFormatter();
        }
    }

    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for 'expires' attribute");
        }
        Instant expiry = DateUtils.parseDate(value, this.datePatterns);
        if (expiry == null) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
        }
        cookie.setExpiryDate(expiry);
    }

    @Override
    public String getAttributeName() {
        return "expires";
    }
}

