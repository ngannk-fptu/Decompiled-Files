/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.ConfigurationElement;

public class SizeOfPolicyConfigurationElement
extends SimpleNodeElement {
    private final SizeOfPolicyConfiguration sizeOfPolicyConfiguration;

    public SizeOfPolicyConfigurationElement(ConfigurationElement parent, SizeOfPolicyConfiguration sizeOfPolicyConfiguration) {
        super(parent, "sizeOfPolicy");
        this.sizeOfPolicyConfiguration = sizeOfPolicyConfiguration;
        this.init();
    }

    public SizeOfPolicyConfigurationElement(NodeElement element, SizeOfPolicyConfiguration sizeOfPolicyConfiguration) {
        super(element, "sizeOfPolicy");
        this.sizeOfPolicyConfiguration = sizeOfPolicyConfiguration;
        this.init();
    }

    private void init() {
        if (this.sizeOfPolicyConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("maxDepth", this.sizeOfPolicyConfiguration.getMaxDepth()).optional(true).defaultValue(1000));
        this.addAttribute(new SimpleNodeAttribute("maxDepthExceededBehavior", this.sizeOfPolicyConfiguration.getMaxDepthExceededBehavior()).optional(true).defaultValue(SizeOfPolicyConfiguration.DEFAULT_MAX_DEPTH_EXCEEDED_BEHAVIOR));
    }
}

