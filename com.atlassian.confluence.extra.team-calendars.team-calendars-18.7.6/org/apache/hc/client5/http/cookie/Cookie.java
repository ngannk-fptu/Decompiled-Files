/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.cookie;

import java.time.Instant;
import java.util.Date;

public interface Cookie {
    public static final String PATH_ATTR = "path";
    public static final String DOMAIN_ATTR = "domain";
    public static final String MAX_AGE_ATTR = "max-age";
    public static final String SECURE_ATTR = "secure";
    public static final String EXPIRES_ATTR = "expires";
    public static final String HTTP_ONLY_ATTR = "httpOnly";

    public String getAttribute(String var1);

    public boolean containsAttribute(String var1);

    public String getName();

    public String getValue();

    @Deprecated
    public Date getExpiryDate();

    default public Instant getExpiryInstant() {
        Date date = this.getExpiryDate();
        return date != null ? Instant.ofEpochMilli(date.getTime()) : null;
    }

    public boolean isPersistent();

    public String getDomain();

    public String getPath();

    public boolean isSecure();

    @Deprecated
    public boolean isExpired(Date var1);

    default public boolean isExpired(Instant date) {
        return this.isExpired(date != null ? new Date(date.toEpochMilli()) : null);
    }

    @Deprecated
    public Date getCreationDate();

    default public Instant getCreationInstant() {
        return null;
    }

    default public boolean isHttpOnly() {
        return false;
    }
}

