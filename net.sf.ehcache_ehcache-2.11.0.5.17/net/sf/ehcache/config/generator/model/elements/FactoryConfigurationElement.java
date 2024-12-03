/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;

public class FactoryConfigurationElement
extends SimpleNodeElement {
    private final FactoryConfiguration<? extends FactoryConfiguration> factoryConfiguration;

    public FactoryConfigurationElement(NodeElement parent, String name, FactoryConfiguration<? extends FactoryConfiguration> factoryConfiguration) {
        super(parent, name);
        this.factoryConfiguration = factoryConfiguration;
        this.init();
    }

    private void init() {
        if (this.factoryConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("class", this.factoryConfiguration.getFullyQualifiedClassPath()).optional(false));
        this.addAttribute(new SimpleNodeAttribute("properties", this.factoryConfiguration.getProperties()).optional(true));
        this.addAttribute(new SimpleNodeAttribute("propertySeparator", this.factoryConfiguration.getPropertySeparator()).optional(true));
    }

    public FactoryConfiguration<? extends FactoryConfiguration> getFactoryConfiguration() {
        return this.factoryConfiguration;
    }
}

