/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;

public class PinningConfigurationElement
extends SimpleNodeElement {
    private final PinningConfiguration pinningConfiguration;

    public PinningConfigurationElement(NodeElement parent, PinningConfiguration pinningConfiguration) {
        super(parent, "pinning");
        this.pinningConfiguration = pinningConfiguration;
        this.init();
    }

    private void init() {
        if (this.pinningConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("store", this.pinningConfiguration.getStore()).optional(false));
    }
}

