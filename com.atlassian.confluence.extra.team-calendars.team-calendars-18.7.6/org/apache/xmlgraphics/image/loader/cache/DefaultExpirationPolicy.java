/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.cache;

import org.apache.xmlgraphics.image.loader.cache.ExpirationPolicy;
import org.apache.xmlgraphics.image.loader.cache.TimeStampProvider;

public class DefaultExpirationPolicy
implements ExpirationPolicy {
    public static final int EXPIRATION_IMMEDIATE = 0;
    public static final int EXPIRATION_NEVER = -1;
    private int expirationAfter;

    public DefaultExpirationPolicy() {
        this(60);
    }

    public DefaultExpirationPolicy(int expirationAfter) {
        this.expirationAfter = expirationAfter;
    }

    private boolean isNeverExpired() {
        return this.expirationAfter < 0;
    }

    @Override
    public boolean isExpired(TimeStampProvider provider, long timestamp) {
        if (this.isNeverExpired()) {
            return false;
        }
        long now = provider.getTimeStamp();
        return now >= timestamp + (long)this.expirationAfter * 1000L;
    }
}

