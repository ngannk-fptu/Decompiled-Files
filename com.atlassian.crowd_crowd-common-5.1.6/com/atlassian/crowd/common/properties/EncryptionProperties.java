/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.common.properties;

import com.atlassian.crowd.common.properties.BooleanSystemProperty;
import com.atlassian.crowd.common.properties.DurationSystemProperty;
import com.atlassian.crowd.common.properties.IntegerSystemProperty;
import java.time.temporal.ChronoUnit;

public class EncryptionProperties {
    public static final BooleanSystemProperty ENCRYPTION_DURING_UPGRADE_DISABLED = new BooleanSystemProperty("crowd.encryption.upgrade.disabled", false);
    public static final BooleanSystemProperty ENRCYPTION_CACHE_ENABLED = new BooleanSystemProperty("crowd.encryption.cache.enabled", true);
    public static final IntegerSystemProperty ENCRYPTION_CACHE_MAX_SIZE = new IntegerSystemProperty("crowd.encryption.cache.max.size", 1000);
    public static final DurationSystemProperty ENCRYPTION_CACHE_EXPIRATION = new DurationSystemProperty("crowd.encryption.cache.expire.minutes", ChronoUnit.MINUTES, 10L);
    public static final BooleanSystemProperty SET_ENCRYPTION_KEYS_OWNERSHIP_ATTRIBUTES = new BooleanSystemProperty("crowd.encryption.keys.set.ownership.attributes", false);

    private EncryptionProperties() {
    }
}

