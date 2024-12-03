/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.marketplace.client.api.ApplicationKey;

public final class ApplicationKeyUtils {
    public static final ApplicationKey FISHEYE = ApplicationKey.valueOf("fisheye");
    public static final ApplicationKey CRUCIBLE = ApplicationKey.valueOf("crucible");
    public static final ApplicationKey STASH = ApplicationKey.valueOf("stash");

    private ApplicationKeyUtils() {
    }

    public static ApplicationKey getMarketplaceApplicationKey(ApplicationKey key) {
        if (key.equals(FISHEYE) || key.equals(CRUCIBLE)) {
            return ApplicationKey.FECRU;
        }
        if (key.equals(STASH)) {
            return ApplicationKey.BITBUCKET;
        }
        return key;
    }
}

