/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.config.TimeoutBehaviorConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.TimeoutBehaviorConfigurationElement;

public class NonstopConfigurationElement
extends SimpleNodeElement {
    private final NonstopConfiguration nonstopConfiguration;

    public NonstopConfigurationElement(NodeElement parent, NonstopConfiguration nonstopConfiguration) {
        super(parent, "nonstop");
        this.nonstopConfiguration = nonstopConfiguration;
        this.init();
    }

    private void init() {
        if (this.nonstopConfiguration == null) {
            return;
        }
        if (this.nonstopConfiguration.getTimeoutBehavior() != null && !this.isDefault(this.nonstopConfiguration.getTimeoutBehavior())) {
            this.addChildElement(new TimeoutBehaviorConfigurationElement((NodeElement)this, this.nonstopConfiguration.getTimeoutBehavior()));
        }
        this.addAttribute(new SimpleNodeAttribute("enabled", this.nonstopConfiguration.isEnabled()).optional(true).defaultValue(true));
        this.addAttribute(new SimpleNodeAttribute("immediateTimeout", this.nonstopConfiguration.isImmediateTimeout()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("timeoutMillis", this.nonstopConfiguration.getTimeoutMillis()).optional(true).defaultValue(30000));
        this.addAttribute(new SimpleNodeAttribute("searchTimeoutMillis", this.nonstopConfiguration.getSearchTimeoutMillis()).optional(true).defaultValue(30000));
    }

    private boolean isDefault(TimeoutBehaviorConfiguration timeoutBehavior) {
        boolean rv = true;
        if (!NonstopConfiguration.DEFAULT_TIMEOUT_BEHAVIOR.getType().equals(timeoutBehavior.getType())) {
            rv = false;
        }
        if (!NonstopConfiguration.DEFAULT_TIMEOUT_BEHAVIOR.getProperties().equals(timeoutBehavior.getProperties())) {
            rv = false;
        }
        if (!NonstopConfiguration.DEFAULT_TIMEOUT_BEHAVIOR.getPropertySeparator().equals(timeoutBehavior.getPropertySeparator())) {
            rv = false;
        }
        return rv;
    }
}

