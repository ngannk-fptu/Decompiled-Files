/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.cookie;

import java.time.Instant;
import java.util.Date;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.utils.DateUtils;

public interface SetCookie
extends Cookie {
    public void setValue(String var1);

    @Deprecated
    public void setExpiryDate(Date var1);

    default public void setExpiryDate(Instant expiryDate) {
        this.setExpiryDate(DateUtils.toDate(expiryDate));
    }

    public void setDomain(String var1);

    public void setPath(String var1);

    public void setSecure(boolean var1);

    default public void setHttpOnly(boolean httpOnly) {
    }
}

