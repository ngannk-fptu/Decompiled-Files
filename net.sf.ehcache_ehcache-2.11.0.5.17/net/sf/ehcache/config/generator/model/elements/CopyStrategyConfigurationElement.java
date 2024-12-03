/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.CopyStrategyConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;

public class CopyStrategyConfigurationElement
extends SimpleNodeElement {
    private final CopyStrategyConfiguration copyStrategyConfiguration;

    public CopyStrategyConfigurationElement(NodeElement parent, CopyStrategyConfiguration copyStrategyConfiguration) {
        super(parent, "copyStrategy");
        this.copyStrategyConfiguration = copyStrategyConfiguration;
        this.init();
    }

    private void init() {
        if (this.copyStrategyConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("class", this.copyStrategyConfiguration.getClassName()).optional(false));
    }
}

