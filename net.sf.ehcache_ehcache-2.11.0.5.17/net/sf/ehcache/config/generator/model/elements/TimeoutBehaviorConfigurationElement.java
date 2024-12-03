/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.TimeoutBehaviorConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;

public class TimeoutBehaviorConfigurationElement
extends SimpleNodeElement {
    private final TimeoutBehaviorConfiguration timeoutBehaviorConfiguration;

    public TimeoutBehaviorConfigurationElement(NodeElement parent, TimeoutBehaviorConfiguration timeoutBehaviorConfiguration) {
        super(parent, "timeoutBehavior");
        this.timeoutBehaviorConfiguration = timeoutBehaviorConfiguration;
        this.init();
    }

    private void init() {
        if (this.timeoutBehaviorConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("type", this.timeoutBehaviorConfiguration.getType()).optional(true).defaultValue(TimeoutBehaviorConfiguration.DEFAULT_VALUE));
        this.addAttribute(new SimpleNodeAttribute("properties", this.timeoutBehaviorConfiguration.getProperties()).optional(true).defaultValue(""));
        this.addAttribute(new SimpleNodeAttribute("propertySeparator", this.timeoutBehaviorConfiguration.getPropertySeparator()).optional(true).defaultValue(","));
    }
}

