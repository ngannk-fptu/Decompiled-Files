/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.config.ClassFilter;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.nio.ClassNameFilter;
import com.hazelcast.util.Preconditions;

public final class SerializationClassNameFilter
implements ClassNameFilter {
    private static final String DESERIALIZATION_ERROR = "Resolving class %s is not allowed.";
    private static final ClassFilter DEFAULT_WHITELIST = new ClassFilter();
    private final ClassFilter blacklist;
    private final ClassFilter whitelist;
    private final boolean useDefaultWhitelist;

    public SerializationClassNameFilter(JavaSerializationFilterConfig config) {
        Preconditions.checkNotNull(config, "JavaSerializationFilterConfig has to be provided");
        this.blacklist = config.getBlacklist();
        this.whitelist = config.getWhitelist();
        this.useDefaultWhitelist = !config.isDefaultsDisabled();
    }

    @Override
    public void filter(String className) throws SecurityException {
        if (this.blacklist.isListed(className)) {
            throw new SecurityException(String.format(DESERIALIZATION_ERROR, className));
        }
        if (this.useDefaultWhitelist || !this.whitelist.isEmpty()) {
            if (this.whitelist.isListed(className) || this.useDefaultWhitelist && DEFAULT_WHITELIST.isListed(className)) {
                return;
            }
            throw new SecurityException(String.format(DESERIALIZATION_ERROR, className));
        }
    }

    static {
        DEFAULT_WHITELIST.addPrefixes("com.hazelcast.", "java", "[");
    }
}

