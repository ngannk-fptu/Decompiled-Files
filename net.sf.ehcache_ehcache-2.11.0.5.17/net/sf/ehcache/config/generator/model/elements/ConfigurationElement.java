/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import java.util.List;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.CacheConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.DefaultCacheConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.DiskStoreConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.FactoryConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.ManagementRESTServiceConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.SizeOfPolicyConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.TerracottaConfigConfigurationElement;

public class ConfigurationElement
extends SimpleNodeElement {
    private final CacheManager cacheManager;
    private final Configuration configuration;

    public ConfigurationElement(Configuration configuration) {
        super(null, "ehcache");
        this.cacheManager = null;
        this.configuration = configuration;
        this.init();
    }

    public ConfigurationElement(CacheManager cacheManager) {
        super(null, "ehcache");
        this.cacheManager = cacheManager;
        this.configuration = cacheManager.getConfiguration();
        this.init();
    }

    private void init() {
        if (this.configuration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("name", this.configuration.getName()).optional(true));
        this.addAttribute(new SimpleNodeAttribute("monitoring", this.configuration.getMonitoring()).optional(true).defaultValue(Configuration.DEFAULT_MONITORING.name().toLowerCase()));
        this.addAttribute(new SimpleNodeAttribute("dynamicConfig", this.configuration.getDynamicConfig()).optional(true).defaultValue(String.valueOf(true)));
        this.addAttribute(new SimpleNodeAttribute("defaultTransactionTimeoutInSeconds", this.configuration.getDefaultTransactionTimeoutInSeconds()).optional(true).defaultValue(String.valueOf(15)));
        this.testAddMaxBytesLocalHeapAttribute();
        this.testAddMaxBytesLocalOffHeapAttribute();
        this.testAddMaxBytesLocalDiskAttribute();
        this.testAddDiskStoreElement();
        this.testAddSizeOfPolicyElement();
        this.testAddTransactionManagerLookupElement();
        this.testAddManagementRESTService();
        this.testAddCacheManagerEventListenerFactoryElement();
        this.testAddCacheManagerPeerProviderFactoryElement();
        this.testAddCacheManagerPeerListenerFactoryElement();
        this.addChildElement(new DefaultCacheConfigurationElement(this, this.configuration, this.configuration.getDefaultCacheConfiguration()));
        if (this.cacheManager != null) {
            for (String cacheName : this.cacheManager.getCacheNames()) {
                boolean decoratedCache = false;
                Ehcache cache = this.cacheManager.getCache(cacheName);
                if (cache == null) {
                    cache = this.cacheManager.getEhcache(cacheName);
                    decoratedCache = true;
                }
                CacheConfiguration config = decoratedCache ? cache.getCacheConfiguration().clone().name(cacheName) : cache.getCacheConfiguration();
                this.addChildElement(new CacheConfigurationElement(this, this.configuration, config));
            }
        } else {
            for (CacheConfiguration cacheConfiguration : this.configuration.getCacheConfigurations().values()) {
                this.addChildElement(new CacheConfigurationElement(this, this.configuration, cacheConfiguration));
            }
        }
        this.testAddTerracottaElement();
    }

    private void testAddMaxBytesLocalHeapAttribute() {
        if (this.configuration.getMaxBytesLocalHeap() > 0L) {
            this.addAttribute(new SimpleNodeAttribute("maxBytesLocalHeap", this.configuration.getMaxBytesLocalHeapAsString()).optional(true).defaultValue(String.valueOf(0L)));
        }
    }

    private void testAddMaxBytesLocalOffHeapAttribute() {
        if (this.configuration.getMaxBytesLocalOffHeap() > 0L) {
            this.addAttribute(new SimpleNodeAttribute("maxBytesLocalOffHeap", this.configuration.getMaxBytesLocalOffHeapAsString()).optional(true).defaultValue(String.valueOf(0L)));
        }
    }

    private void testAddMaxBytesLocalDiskAttribute() {
        if (this.configuration.getMaxBytesLocalDisk() > 0L) {
            this.addAttribute(new SimpleNodeAttribute("maxBytesLocalDisk", this.configuration.getMaxBytesLocalDiskAsString()).optional(true).defaultValue(String.valueOf(0L)));
        }
    }

    private void testAddDiskStoreElement() {
        DiskStoreConfiguration diskStoreConfiguration = this.configuration.getDiskStoreConfiguration();
        if (diskStoreConfiguration != null) {
            this.addChildElement(new DiskStoreConfigurationElement(this, diskStoreConfiguration));
        }
    }

    private void testAddSizeOfPolicyElement() {
        SizeOfPolicyConfiguration sizeOfPolicyConfiguration = this.configuration.getSizeOfPolicyConfiguration();
        if (sizeOfPolicyConfiguration != null && !Configuration.DEFAULT_SIZEOF_POLICY_CONFIGURATION.equals(sizeOfPolicyConfiguration)) {
            this.addChildElement(new SizeOfPolicyConfigurationElement(this, sizeOfPolicyConfiguration));
        }
    }

    private void testAddTransactionManagerLookupElement() {
        FactoryConfiguration transactionManagerLookupConfiguration = this.configuration.getTransactionManagerLookupConfiguration();
        if (transactionManagerLookupConfiguration != null && !transactionManagerLookupConfiguration.equals(Configuration.DEFAULT_TRANSACTION_MANAGER_LOOKUP_CONFIG)) {
            this.addChildElement(new FactoryConfigurationElement(this, "transactionManagerLookup", transactionManagerLookupConfiguration));
        }
    }

    private void testAddManagementRESTService() {
        ManagementRESTServiceConfiguration managementRESTServiceConfiguration = this.configuration.getManagementRESTService();
        if (managementRESTServiceConfiguration != null) {
            this.addChildElement(new ManagementRESTServiceConfigurationElement(this, managementRESTServiceConfiguration));
        }
    }

    private void testAddCacheManagerEventListenerFactoryElement() {
        FactoryConfiguration cacheManagerEventListenerFactoryConfiguration = this.configuration.getCacheManagerEventListenerFactoryConfiguration();
        if (cacheManagerEventListenerFactoryConfiguration != null) {
            this.addChildElement(new FactoryConfigurationElement(this, "cacheManagerEventListenerFactory", cacheManagerEventListenerFactoryConfiguration));
        }
    }

    private void testAddCacheManagerPeerProviderFactoryElement() {
        List<FactoryConfiguration> cacheManagerPeerProviderFactoryConfiguration = this.configuration.getCacheManagerPeerProviderFactoryConfiguration();
        if (cacheManagerPeerProviderFactoryConfiguration != null) {
            ConfigurationElement.addAllFactoryConfigsAsChildElements(this, "cacheManagerPeerProviderFactory", cacheManagerPeerProviderFactoryConfiguration);
        }
    }

    private void testAddCacheManagerPeerListenerFactoryElement() {
        List<FactoryConfiguration> cacheManagerPeerListenerFactoryConfigurations = this.configuration.getCacheManagerPeerListenerFactoryConfigurations();
        if (cacheManagerPeerListenerFactoryConfigurations != null && !cacheManagerPeerListenerFactoryConfigurations.isEmpty()) {
            ConfigurationElement.addAllFactoryConfigsAsChildElements(this, "cacheManagerPeerListenerFactory", cacheManagerPeerListenerFactoryConfigurations);
        }
    }

    private void testAddTerracottaElement() {
        TerracottaClientConfiguration terracottaConfiguration = this.configuration.getTerracottaConfiguration();
        if (terracottaConfiguration != null) {
            this.addChildElement(new TerracottaConfigConfigurationElement((NodeElement)this, terracottaConfiguration));
        }
    }
}

