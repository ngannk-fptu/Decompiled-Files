/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cache.ehcache;

import java.io.IOException;
import java.util.Properties;

public interface EhCacheConfigStore {
    public Properties readStoredConfig() throws IOException;

    public void updateStoredConfig(Properties var1) throws IOException;
}

