/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.FactoryConfigurationElement;

public class CacheWriterConfigurationElement
extends SimpleNodeElement {
    private final CacheWriterConfiguration cacheWriterConfiguration;

    public CacheWriterConfigurationElement(NodeElement parent, CacheWriterConfiguration cacheWriterConfiguration) {
        super(parent, "cacheWriter");
        this.cacheWriterConfiguration = cacheWriterConfiguration;
        this.init();
    }

    private void init() {
        if (this.cacheWriterConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("minWriteDelay", this.cacheWriterConfiguration.getMinWriteDelay()).optional(true).defaultValue(1));
        this.addAttribute(new SimpleNodeAttribute("writeMode", this.cacheWriterConfiguration.getWriteMode()).optional(true).defaultValue(CacheWriterConfiguration.DEFAULT_WRITE_MODE));
        this.addAttribute(new SimpleNodeAttribute("writeBatchSize", this.cacheWriterConfiguration.getWriteBatchSize()).optional(true).defaultValue(1));
        this.addAttribute(new SimpleNodeAttribute("maxWriteDelay", this.cacheWriterConfiguration.getMaxWriteDelay()).optional(true).defaultValue(1));
        this.addAttribute(new SimpleNodeAttribute("retryAttempts", this.cacheWriterConfiguration.getRetryAttempts()).optional(true).defaultValue(0));
        this.addAttribute(new SimpleNodeAttribute("rateLimitPerSecond", this.cacheWriterConfiguration.getRateLimitPerSecond()).optional(true).defaultValue(0));
        this.addAttribute(new SimpleNodeAttribute("writeBatching", this.cacheWriterConfiguration.getWriteBatching()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("writeCoalescing", this.cacheWriterConfiguration.getWriteCoalescing()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("notifyListenersOnException", this.cacheWriterConfiguration.getNotifyListenersOnException()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("retryAttemptDelaySeconds", this.cacheWriterConfiguration.getRetryAttemptDelaySeconds()).optional(true).defaultValue(1));
        this.addAttribute(new SimpleNodeAttribute("writeBehindConcurrency", this.cacheWriterConfiguration.getWriteBehindConcurrency()).optional(true).defaultValue(1));
        this.addAttribute(new SimpleNodeAttribute("writeBehindMaxQueueSize", this.cacheWriterConfiguration.getWriteBehindMaxQueueSize()).optional(true).defaultValue(0));
        CacheWriterConfiguration.CacheWriterFactoryConfiguration cacheWriterFactoryConfiguration = this.cacheWriterConfiguration.getCacheWriterFactoryConfiguration();
        if (cacheWriterFactoryConfiguration != null) {
            this.addChildElement(new FactoryConfigurationElement(this, "cacheWriterFactory", cacheWriterFactoryConfiguration));
        }
    }
}

