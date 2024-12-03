/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model.elements;

import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.config.generator.model.elements.ConfigurationElement;

public class ManagementRESTServiceConfigurationElement
extends SimpleNodeElement {
    private final ManagementRESTServiceConfiguration managementRESTServiceConfiguration;

    public ManagementRESTServiceConfigurationElement(ConfigurationElement parent, ManagementRESTServiceConfiguration cfg) {
        super(parent, "managementRESTService");
        this.managementRESTServiceConfiguration = cfg;
        this.init();
    }

    public ManagementRESTServiceConfigurationElement(NodeElement element, ManagementRESTServiceConfiguration cfg) {
        super(element, "managementRESTService");
        this.managementRESTServiceConfiguration = cfg;
        this.init();
    }

    private void init() {
        if (this.managementRESTServiceConfiguration == null) {
            return;
        }
        this.addAttribute(new SimpleNodeAttribute("enabled", this.managementRESTServiceConfiguration.isEnabled()).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("bind", this.managementRESTServiceConfiguration.getBind()).defaultValue("0.0.0.0:9888"));
        this.addAttribute(new SimpleNodeAttribute("securityServiceLocation", this.managementRESTServiceConfiguration.getSecurityServiceLocation()).optional(true));
        this.addAttribute(new SimpleNodeAttribute("securityServiceTimeout", this.managementRESTServiceConfiguration.getSecurityServiceTimeout()).optional(true).defaultValue(5000));
        this.addAttribute(new SimpleNodeAttribute("sslEnabled", this.managementRESTServiceConfiguration.isSslEnabled()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("needClientAuth", this.managementRESTServiceConfiguration.isNeedClientAuth()).optional(true).defaultValue(false));
        this.addAttribute(new SimpleNodeAttribute("sampleHistorySize", this.managementRESTServiceConfiguration.getSampleHistorySize()).optional(true).defaultValue(30));
        this.addAttribute(new SimpleNodeAttribute("sampleIntervalSeconds", this.managementRESTServiceConfiguration.getSampleIntervalSeconds()).optional(true).defaultValue(1));
        this.addAttribute(new SimpleNodeAttribute("sampleSearchIntervalSeconds", this.managementRESTServiceConfiguration.getSampleSearchIntervalSeconds()).optional(true).defaultValue(10));
    }
}

