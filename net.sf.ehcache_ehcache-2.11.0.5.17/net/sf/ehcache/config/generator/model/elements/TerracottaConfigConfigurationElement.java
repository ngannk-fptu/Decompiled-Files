/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;

public class TerracottaConfigConfigurationElement
extends SimpleNodeElement {
    private final TerracottaClientConfiguration tcConfigConfiguration;

    public TerracottaConfigConfigurationElement(NodeElement parent, TerracottaClientConfiguration tcConfigConfiguration) {
        super(parent, "terracottaConfig");
        this.tcConfigConfiguration = tcConfigConfiguration;
        this.init();
    }

    private void init() {
        if (this.tcConfigConfiguration == null) {
            return;
        }
        if (this.tcConfigConfiguration.getUrl() != null) {
            this.addAttribute(new SimpleNodeAttribute("url", this.tcConfigConfiguration.getUrl()).optional(true));
        }
        this.addAttribute(new SimpleNodeAttribute("rejoin", this.tcConfigConfiguration.isRejoin()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("wanEnabledTSA", this.tcConfigConfiguration.isWanEnabledTSA()).optional(true).defaultValue(false));
        if (this.tcConfigConfiguration.getOriginalEmbeddedConfig() != null) {
            this.addChildElement(new TCConfigElement(this, this.tcConfigConfiguration.getOriginalEmbeddedConfig()));
        }
    }

    private static class TCConfigElement
    extends SimpleNodeElement {
        public TCConfigElement(TerracottaConfigConfigurationElement parent, String content) {
            super(parent, "tc-config");
            this.setInnerContent(content);
        }
    }
}

