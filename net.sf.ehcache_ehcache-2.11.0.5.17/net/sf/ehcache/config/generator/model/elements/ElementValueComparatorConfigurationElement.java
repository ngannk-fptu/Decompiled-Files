/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.ElementValueComparatorConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;

public class ElementValueComparatorConfigurationElement
extends SimpleNodeElement {
    private final ElementValueComparatorConfiguration elementValueComparatorConfiguration;

    public ElementValueComparatorConfigurationElement(NodeElement parent, ElementValueComparatorConfiguration elementValueComparatorConfiguration) {
        super(parent, "elementValueComparator");
        this.elementValueComparatorConfiguration = elementValueComparatorConfiguration;
        this.init();
    }

    private void init() {
        if (this.elementValueComparatorConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("class", this.elementValueComparatorConfiguration.getClassName()).optional(false));
    }
}

