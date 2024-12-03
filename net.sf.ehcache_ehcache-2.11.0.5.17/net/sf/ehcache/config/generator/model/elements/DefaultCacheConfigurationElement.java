/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.CacheConfigurationElement;

public class DefaultCacheConfigurationElement
extends SimpleNodeElement {
    private final Configuration configuration;
    private final CacheConfiguration cacheConfiguration;

    public DefaultCacheConfigurationElement(NodeElement parent, Configuration configuration, CacheConfiguration cacheConfiguration) {
        super(parent, "defaultCache");
        this.configuration = configuration;
        this.cacheConfiguration = cacheConfiguration;
        this.init();
        this.optional = false;
    }

    private void init() {
        if (this.cacheConfiguration == null) {
            return;
        }
        CacheConfigurationElement.addCommonAttributesWithDefaultCache(this, this.configuration, this.cacheConfiguration);
        CacheConfigurationElement.addCommonChildElementsWithDefaultCache(this, this.cacheConfiguration);
    }
}

