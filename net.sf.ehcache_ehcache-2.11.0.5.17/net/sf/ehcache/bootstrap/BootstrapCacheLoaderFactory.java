/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.bootstrap;

import java.util.Properties;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.util.PropertyUtil;

public abstract class BootstrapCacheLoaderFactory<T extends BootstrapCacheLoader> {
    public static final String BOOTSTRAP_ASYNCHRONOUSLY = "bootstrapAsynchronously";

    public abstract T createBootstrapCacheLoader(Properties var1);

    protected boolean extractBootstrapAsynchronously(Properties properties) {
        return this.extractBoolean(properties, BOOTSTRAP_ASYNCHRONOUSLY, true);
    }

    protected boolean extractBoolean(Properties properties, String prop, boolean defaultValue) {
        String propString = PropertyUtil.extractAndLogProperty(prop, properties);
        boolean value = propString != null ? PropertyUtil.parseBoolean(propString) : defaultValue;
        return value;
    }

    protected long extractLong(Properties properties, String prop, long defaultValue) {
        String propString = PropertyUtil.extractAndLogProperty(prop, properties);
        long value = propString != null ? Long.parseLong(propString) : defaultValue;
        return value;
    }
}

