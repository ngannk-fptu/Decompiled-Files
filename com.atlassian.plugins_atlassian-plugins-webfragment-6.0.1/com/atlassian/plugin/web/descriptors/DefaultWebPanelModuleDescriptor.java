/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Maps
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelSupplierFactory;
import com.atlassian.plugin.web.descriptors.WeightElementParser;
import com.atlassian.plugin.web.model.WebPanel;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.dom4j.Element;

public class DefaultWebPanelModuleDescriptor
extends AbstractWebFragmentModuleDescriptor<WebPanel>
implements WebPanelModuleDescriptor {
    public static final String XML_ELEMENT_NAME = "web-panel";
    private WebPanelSupplierFactory webPanelSupplierFactory;
    private Supplier<WebPanel> webPanelFactory;
    private int weight;
    private String location;

    public DefaultWebPanelModuleDescriptor(HostContainer hostContainer, ModuleFactory moduleClassFactory, WebInterfaceManager webInterfaceManager) {
        super(moduleClassFactory, webInterfaceManager);
        this.webPanelSupplierFactory = new WebPanelSupplierFactory(this, hostContainer, this.moduleFactory);
        this.webInterfaceManager = webInterfaceManager;
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.weight = WeightElementParser.getWeight(element);
        this.location = element.attributeValue("location");
        this.webPanelFactory = this.webPanelSupplierFactory.build(this.moduleClassName);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@location").withError("The Web Panel location attribute is required.")});
    }

    public String getLocation() {
        return this.location;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    public WebPanel getModule() {
        return new ContextAwareWebPanel((WebPanel)this.webPanelFactory.get());
    }

    @Override
    public void enabled() {
        super.enabled();
        this.webInterfaceManager.refresh();
    }

    @Override
    public void disabled() {
        this.webInterfaceManager.refresh();
        super.disabled();
    }

    private class ContextAwareWebPanel
    implements WebPanel {
        private final WebPanel delegate;

        private ContextAwareWebPanel(WebPanel delegate) {
            this.delegate = delegate;
        }

        public String getHtml(Map<String, Object> context) {
            return this.delegate.getHtml(DefaultWebPanelModuleDescriptor.this.getContextProvider().getContextMap((Map)Maps.newHashMap(context)));
        }

        public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
            this.delegate.writeHtml(writer, DefaultWebPanelModuleDescriptor.this.getContextProvider().getContextMap((Map)Maps.newHashMap(context)));
        }
    }
}

