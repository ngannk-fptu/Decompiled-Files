/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.ConfigurationElement;

public class DiskStoreConfigurationElement
extends SimpleNodeElement {
    private final DiskStoreConfiguration diskStoreConfiguration;

    public DiskStoreConfigurationElement(ConfigurationElement parent, DiskStoreConfiguration diskStoreConfiguration) {
        super(parent, "diskStore");
        this.diskStoreConfiguration = diskStoreConfiguration;
        this.init();
    }

    private void init() {
        if (this.diskStoreConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("path", this.diskStoreConfiguration.getOriginalPath()).optional(true));
    }
}

