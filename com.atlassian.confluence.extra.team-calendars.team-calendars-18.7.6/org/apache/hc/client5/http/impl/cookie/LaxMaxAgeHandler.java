/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.cookie;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.client5.http.cookie.SetCookie;
import org.apache.hc.client5.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

@Contract(threading=ThreadingBehavior.STATELESS)
public class LaxMaxAgeHandler
extends AbstractCookieAttributeHandler
implements CommonCookieAttributeHandler {
    public static final LaxMaxAgeHandler INSTANCE = new LaxMaxAgeHandler();
    private static final Pattern MAX_AGE_PATTERN = Pattern.compile("^\\-?[0-9]+$");

    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (TextUtils.isBlank(value)) {
            return;
        }
        Matcher matcher = MAX_AGE_PATTERN.matcher(value);
        if (matcher.matches()) {
            int age;
            try {
                age = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                return;
            }
            Instant expiryDate = age >= 0 ? Instant.now().plusSeconds(age) : Instant.EPOCH;
            cookie.setExpiryDate(expiryDate);
        }
    }

    @Override
    public String getAttributeName() {
        return "max-age";
    }
}

