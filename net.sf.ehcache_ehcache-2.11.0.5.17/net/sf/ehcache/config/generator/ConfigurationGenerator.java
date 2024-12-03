/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator;

import java.util.Map;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.generator.ConfigurationUtil;

@Deprecated
public class ConfigurationGenerator {
    @Deprecated
    public String generate(Configuration configuration, CacheConfiguration defaultCacheConfiguration, Map<String, CacheConfiguration> cacheConfigs) {
        return ConfigurationUtil.generateCacheManagerConfigurationText(configuration);
    }

    @Deprecated
    public String generate(Configuration configuration, CacheConfiguration cacheConfiguration) {
        return ConfigurationUtil.generateCacheConfigurationText(configuration, cacheConfiguration);
    }
}

