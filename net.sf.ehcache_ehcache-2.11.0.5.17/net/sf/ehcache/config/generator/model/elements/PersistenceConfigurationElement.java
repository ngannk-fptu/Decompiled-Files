/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.ConfigurationElement;

public class PersistenceConfigurationElement
extends SimpleNodeElement {
    private final PersistenceConfiguration persistenceConfiguration;

    public PersistenceConfigurationElement(ConfigurationElement parent, PersistenceConfiguration persistenceConfiguration) {
        super(parent, "persistence");
        this.persistenceConfiguration = persistenceConfiguration;
        this.init();
    }

    public PersistenceConfigurationElement(NodeElement element, PersistenceConfiguration persistenceConfiguration) {
        super(element, "persistence");
        this.persistenceConfiguration = persistenceConfiguration;
        this.init();
    }

    private void init() {
        if (this.persistenceConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("strategy", this.persistenceConfiguration.getStrategy()));
        this.addAttribute(new SimpleNodeAttribute("synchronousWrites", this.persistenceConfiguration.getSynchronousWrites()).optional(true).defaultValue(false));
    }
}

