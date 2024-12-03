/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.NonstopConfigurationElement;

public class TerracottaConfigurationElement
extends SimpleNodeElement {
    private final TerracottaConfiguration tcConfiguration;

    public TerracottaConfigurationElement(NodeElement parent, TerracottaConfiguration tcConfiguration) {
        super(parent, "terracotta");
        this.tcConfiguration = tcConfiguration;
        this.init();
    }

    private void init() {
        if (this.tcConfiguration == null) {
            return;
        }
        if (!TerracottaConfiguration.DEFAULT_NON_STOP_CONFIGURATION.equals(this.tcConfiguration.getNonstopConfiguration())) {
            this.addChildElement(new NonstopConfigurationElement((NodeElement)this, this.tcConfiguration.getNonstopConfiguration()));
        }
        this.addAttribute(new SimpleNodeAttribute("clustered", this.tcConfiguration.isClustered()).optional(true).defaultValue(true));
        this.addAttribute(new SimpleNodeAttribute("consistency", this.tcConfiguration.getConsistency().name()).optional(true).defaultValue(TerracottaConfiguration.DEFAULT_CONSISTENCY_TYPE.name()));
        this.addAttribute(new SimpleNodeAttribute("synchronousWrites", this.tcConfiguration.isSynchronousWrites()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("copyOnRead", this.tcConfiguration.isCopyOnRead()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("localKeyCache", this.tcConfiguration.getLocalKeyCache()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("localKeyCacheSize", this.tcConfiguration.getLocalKeyCacheSize()).optional(true).defaultValue(300000));
        this.addAttribute(new SimpleNodeAttribute("orphanEviction", this.tcConfiguration.getOrphanEviction()).optional(true).defaultValue(true));
        this.addAttribute(new SimpleNodeAttribute("orphanEvictionPeriod", this.tcConfiguration.getOrphanEvictionPeriod()).optional(true).defaultValue(4));
        this.addAttribute(new SimpleNodeAttribute("coherentReads", this.tcConfiguration.getCoherentReads()).optional(true).defaultValue(true));
        this.addAttribute(new SimpleNodeAttribute("concurrency", this.tcConfiguration.getConcurrency()).optional(true).defaultValue(0));
        this.addAttribute(new SimpleNodeAttribute("localCacheEnabled", this.tcConfiguration.isLocalCacheEnabled()).optional(true).defaultValue(true));
        this.addAttribute(new SimpleNodeAttribute("compressionEnabled", this.tcConfiguration.isCompressionEnabled()).optional(true).defaultValue(false));
    }
}

