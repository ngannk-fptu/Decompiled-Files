/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.cookie;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.apache.hc.client5.http.cookie.Cookie;

public interface CookieStore {
    public void addCookie(Cookie var1);

    public List<Cookie> getCookies();

    @Deprecated
    public boolean clearExpired(Date var1);

    default public boolean clearExpired(Instant date) {
        return this.clearExpired(date != null ? new Date(date.toEpochMilli()) : null);
    }

    public void clear();
}

