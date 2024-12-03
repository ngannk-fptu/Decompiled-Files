/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.expiry;

import javax.cache.expiry.Duration;

public interface ExpiryPolicy {
    public Duration getExpiryForCreation();

    public Duration getExpiryForAccess();

    public Duration getExpiryForUpdate();
}

