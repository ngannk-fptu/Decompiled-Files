/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.CopyStrategyConfiguration;
import net.sf.ehcache.config.ElementValueComparatorConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.CacheWriterConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.CopyStrategyConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.ElementValueComparatorConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.FactoryConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.PersistenceConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.PinningConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.SearchableConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.SizeOfPolicyConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.TerracottaConfigurationElement;
import net.sf.ehcache.store.DefaultElementValueComparator;

public class CacheConfigurationElement
extends SimpleNodeElement {
    private final Configuration configuration;
    private final CacheConfiguration cacheConfiguration;

    public CacheConfigurationElement(NodeElement parent, Configuration configuration, CacheConfiguration cacheConfiguration) {
        super(parent, "cache");
        this.configuration = configuration;
        this.cacheConfiguration = cacheConfiguration;
        this.init();
    }

    private void init() {
        if (this.cacheConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("name", this.cacheConfiguration.getName()).optional(false));
        CacheConfigurationElement.addCommonAttributesWithDefaultCache(this, this.configuration, this.cacheConfiguration);
        this.addAttribute(new SimpleNodeAttribute("logging", this.cacheConfiguration.getLogging()).optional(true).defaultValue(false));
        CacheConfigurationElement.addCommonChildElementsWithDefaultCache(this, this.cacheConfiguration);
        if (this.cacheConfiguration.getMaxBytesLocalHeap() > 0L || this.cacheConfiguration.isMaxBytesLocalHeapPercentageSet()) {
            this.addAttribute(new SimpleNodeAttribute("maxBytesLocalHeap", this.cacheConfiguration.getMaxBytesLocalHeapAsString()).optional(true).defaultValue(String.valueOf(0L)));
        }
        if (this.cacheConfiguration.getMaxBytesLocalOffHeap() > 0L || this.cacheConfiguration.isMaxBytesLocalOffHeapPercentageSet()) {
            this.addAttribute(new SimpleNodeAttribute("maxBytesLocalOffHeap", this.cacheConfiguration.getMaxBytesLocalOffHeapAsString()).optional(true).defaultValue(String.valueOf(0L)));
        }
        if (!this.cacheConfiguration.isTerracottaClustered() && (this.cacheConfiguration.getMaxBytesLocalDisk() > 0L || this.cacheConfiguration.isMaxBytesLocalDiskPercentageSet())) {
            this.addAttribute(new SimpleNodeAttribute("maxBytesLocalDisk", this.cacheConfiguration.getMaxBytesLocalDiskAsString()).optional(true).defaultValue(String.valueOf(0L)));
        }
    }

    public static void addCommonAttributesWithDefaultCache(NodeElement element, Configuration configuration, CacheConfiguration cacheConfiguration) {
        element.addAttribute(new SimpleNodeAttribute("eternal", cacheConfiguration.isEternal()).optional(true).defaultValue(false));
        if (cacheConfiguration.getMaxBytesLocalHeap() <= 0L && configuration.getMaxBytesLocalHeap() <= 0L) {
            element.addAttribute(new SimpleNodeAttribute("maxEntriesLocalHeap", cacheConfiguration.getMaxEntriesLocalHeap()).optional(false));
        }
        element.addAttribute(new SimpleNodeAttribute("clearOnFlush", cacheConfiguration.isClearOnFlush()).optional(true).defaultValue(String.valueOf(true)));
        element.addAttribute(new SimpleNodeAttribute("diskAccessStripes", cacheConfiguration.getDiskAccessStripes()).optional(true).defaultValue(1));
        element.addAttribute(new SimpleNodeAttribute("diskSpoolBufferSizeMB", cacheConfiguration.getDiskSpoolBufferSizeMB()).optional(true).defaultValue(30));
        element.addAttribute(new SimpleNodeAttribute("diskExpiryThreadIntervalSeconds", cacheConfiguration.getDiskExpiryThreadIntervalSeconds()).optional(true).defaultValue(120L));
        element.addAttribute(new SimpleNodeAttribute("copyOnWrite", cacheConfiguration.isCopyOnWrite()).optional(true).defaultValue(false));
        element.addAttribute(new SimpleNodeAttribute("copyOnRead", cacheConfiguration.isCopyOnRead()).optional(true).defaultValue(false));
        element.addAttribute(new SimpleNodeAttribute("timeToIdleSeconds", cacheConfiguration.getTimeToIdleSeconds()).optional(true).defaultValue(0L));
        element.addAttribute(new SimpleNodeAttribute("timeToLiveSeconds", cacheConfiguration.getTimeToLiveSeconds()).optional(true).defaultValue(0L));
        if (cacheConfiguration.isTerracottaClustered()) {
            element.addAttribute(new SimpleNodeAttribute("maxEntriesInCache", cacheConfiguration.getMaxEntriesInCache()).optional(true).defaultValue(0L));
        }
        if (!cacheConfiguration.isTerracottaClustered() && cacheConfiguration.getMaxEntriesLocalDisk() > 0L) {
            element.addAttribute(new SimpleNodeAttribute("maxEntriesLocalDisk", cacheConfiguration.getMaxEntriesLocalDisk()).optional(true).defaultValue(0));
        }
        if (cacheConfiguration.isOverflowToOffHeapSet()) {
            element.addAttribute(new SimpleNodeAttribute("overflowToOffHeap", cacheConfiguration.isOverflowToOffHeap()));
        }
        element.addAttribute(new SimpleNodeAttribute("cacheLoaderTimeoutMillis", cacheConfiguration.getCacheLoaderTimeoutMillis()).optional(true).defaultValue(0L));
        element.addAttribute(new SimpleNodeAttribute("transactionalMode", cacheConfiguration.getTransactionalMode()).optional(true).defaultValue(CacheConfiguration.DEFAULT_TRANSACTIONAL_MODE));
        element.addAttribute(new SimpleNodeAttribute("memoryStoreEvictionPolicy", cacheConfiguration.getMemoryStoreEvictionPolicy().toString().toUpperCase()).optional(true).defaultValue(CacheConfiguration.DEFAULT_MEMORY_STORE_EVICTION_POLICY.toString().toUpperCase()));
        if (cacheConfiguration.isOverflowToDisk() && cacheConfiguration.isDiskPersistent()) {
            element.addAttribute(new SimpleNodeAttribute("diskPersistent", "true"));
            element.addAttribute(new SimpleNodeAttribute("overflowToDisk", "true"));
        }
    }

    public static void addCommonChildElementsWithDefaultCache(NodeElement element, CacheConfiguration cacheConfiguration) {
        for (FactoryConfigurationElement child : CacheConfigurationElement.getAllFactoryElements(element, "cacheEventListenerFactory", cacheConfiguration.getCacheEventListenerConfigurations())) {
            CacheConfiguration.CacheEventListenerFactoryConfiguration factoryConfiguration = (CacheConfiguration.CacheEventListenerFactoryConfiguration)child.getFactoryConfiguration();
            child.addAttribute(new SimpleNodeAttribute("listenFor", factoryConfiguration.getListenFor()));
            element.addChildElement(child);
        }
        CacheConfigurationElement.addAllFactoryConfigsAsChildElements(element, "cacheExtensionFactory", cacheConfiguration.getCacheExtensionConfigurations());
        CacheConfigurationElement.addAllFactoryConfigsAsChildElements(element, "cacheLoaderFactory", cacheConfiguration.getCacheLoaderConfigurations());
        CacheConfigurationElement.addBootstrapCacheLoaderFactoryConfigurationElement(element, cacheConfiguration);
        CacheConfigurationElement.addCacheExceptionHandlerFactoryConfigurationElement(element, cacheConfiguration);
        CacheConfigurationElement.addSizeOfPolicyConfigurationElement(element, cacheConfiguration);
        if (!cacheConfiguration.isOverflowToDisk() || !cacheConfiguration.isDiskPersistent()) {
            CacheConfigurationElement.addPersistenceConfigurationElement(element, cacheConfiguration);
        }
        CacheConfigurationElement.addCopyStrategyConfigurationElement(element, cacheConfiguration);
        CacheConfigurationElement.addElementValueComparatorConfigurationElement(element, cacheConfiguration);
        CacheConfigurationElement.addCacheWriterConfigurationElement(element, cacheConfiguration);
        CacheConfigurationElement.addAllFactoryConfigsAsChildElements(element, "cacheDecoratorFactory", cacheConfiguration.getCacheDecoratorConfigurations());
        CacheConfigurationElement.addTerracottaConfigurationElement(element, cacheConfiguration);
        CacheConfigurationElement.addPinningElement(element, cacheConfiguration);
        CacheConfigurationElement.addSearchElement(element, cacheConfiguration);
    }

    private static void addBootstrapCacheLoaderFactoryConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration bootstrapCacheLoaderFactoryConfiguration = cacheConfiguration.getBootstrapCacheLoaderFactoryConfiguration();
        if (bootstrapCacheLoaderFactoryConfiguration != null) {
            element.addChildElement(new FactoryConfigurationElement(element, "bootstrapCacheLoaderFactory", bootstrapCacheLoaderFactoryConfiguration));
        }
    }

    private static void addCacheExceptionHandlerFactoryConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        CacheConfiguration.CacheExceptionHandlerFactoryConfiguration cacheExceptionHandlerFactoryConfiguration = cacheConfiguration.getCacheExceptionHandlerFactoryConfiguration();
        if (cacheExceptionHandlerFactoryConfiguration != null) {
            element.addChildElement(new FactoryConfigurationElement(element, "cacheExceptionHandlerFactory", cacheExceptionHandlerFactoryConfiguration));
        }
    }

    private static void addSizeOfPolicyConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        SizeOfPolicyConfiguration sizeOfPolicyConfiguration = cacheConfiguration.getSizeOfPolicyConfiguration();
        if (sizeOfPolicyConfiguration != null && !Configuration.DEFAULT_SIZEOF_POLICY_CONFIGURATION.equals(sizeOfPolicyConfiguration)) {
            element.addChildElement(new SizeOfPolicyConfigurationElement(element, sizeOfPolicyConfiguration));
        }
    }

    private static void addPersistenceConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        PersistenceConfiguration persistenceConfiguration = cacheConfiguration.getPersistenceConfiguration();
        if (persistenceConfiguration != null) {
            element.addChildElement(new PersistenceConfigurationElement(element, persistenceConfiguration));
        }
    }

    private static void addCopyStrategyConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        CopyStrategyConfiguration copyStrategyConfiguration = cacheConfiguration.getCopyStrategyConfiguration();
        if (copyStrategyConfiguration != null && !copyStrategyConfiguration.equals(CacheConfiguration.DEFAULT_COPY_STRATEGY_CONFIGURATION)) {
            element.addChildElement(new CopyStrategyConfigurationElement(element, copyStrategyConfiguration));
        }
    }

    private static void addElementValueComparatorConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        ElementValueComparatorConfiguration elementValueComparatorConfiguration = cacheConfiguration.getElementValueComparatorConfiguration();
        if (elementValueComparatorConfiguration != null && !elementValueComparatorConfiguration.getClassName().equals(DefaultElementValueComparator.class.getName())) {
            element.addChildElement(new ElementValueComparatorConfigurationElement(element, elementValueComparatorConfiguration));
        }
    }

    private static void addCacheWriterConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        CacheWriterConfiguration cacheWriterConfiguration = cacheConfiguration.getCacheWriterConfiguration();
        if (cacheWriterConfiguration != null && !CacheConfiguration.DEFAULT_CACHE_WRITER_CONFIGURATION.equals(cacheWriterConfiguration)) {
            element.addChildElement(new CacheWriterConfigurationElement(element, cacheWriterConfiguration));
        }
    }

    private static void addTerracottaConfigurationElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        TerracottaConfiguration terracottaConfiguration = cacheConfiguration.getTerracottaConfiguration();
        if (terracottaConfiguration != null) {
            element.addChildElement(new TerracottaConfigurationElement(element, terracottaConfiguration));
        }
    }

    private static void addSearchElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        if (cacheConfiguration.isSearchable()) {
            element.addChildElement(new SearchableConfigurationElement(element, cacheConfiguration.getSearchable()));
        }
    }

    private static void addPinningElement(NodeElement element, CacheConfiguration cacheConfiguration) {
        PinningConfiguration pinningConfiguration = cacheConfiguration.getPinningConfiguration();
        if (pinningConfiguration != null) {
            element.addChildElement(new PinningConfigurationElement(element, pinningConfiguration));
        }
    }
}

