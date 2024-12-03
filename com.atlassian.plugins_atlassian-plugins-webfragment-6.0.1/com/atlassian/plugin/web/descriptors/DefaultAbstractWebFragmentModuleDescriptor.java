/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 *  com.atlassian.plugin.web.model.WebParam
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.plugin.web.model.WebParam;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;

public class DefaultAbstractWebFragmentModuleDescriptor<T>
implements StateAware,
WebFragmentModuleDescriptor<T> {
    private final WebFragmentModuleDescriptor<T> decoratedDescriptor;

    public DefaultAbstractWebFragmentModuleDescriptor(WebFragmentModuleDescriptor<T> abstractDescriptor) {
        this.decoratedDescriptor = abstractDescriptor;
    }

    public void enabled() {
        this.decoratedDescriptor.enabled();
    }

    public void disabled() {
        this.decoratedDescriptor.disabled();
    }

    protected WebFragmentModuleDescriptor getDecoratedDescriptor() {
        return this.decoratedDescriptor;
    }

    public int getWeight() {
        return this.decoratedDescriptor.getWeight();
    }

    public String getKey() {
        return this.decoratedDescriptor.getKey();
    }

    public T getModule() {
        return null;
    }

    public String getI18nNameKey() {
        return this.decoratedDescriptor.getI18nNameKey();
    }

    public String getDescriptionKey() {
        return this.decoratedDescriptor.getDescriptionKey();
    }

    public Plugin getPlugin() {
        return this.decoratedDescriptor.getPlugin();
    }

    public boolean isEnabled() {
        return this.decoratedDescriptor.isEnabled();
    }

    public WebLabel getWebLabel() {
        return this.decoratedDescriptor.getWebLabel();
    }

    public WebLabel getTooltip() {
        return this.decoratedDescriptor.getTooltip();
    }

    public void setWebInterfaceManager(WebInterfaceManager webInterfaceManager) {
        if (this.decoratedDescriptor instanceof AbstractWebFragmentModuleDescriptor) {
            AbstractWebFragmentModuleDescriptor abstractWebFragmentModuleDescriptor = (AbstractWebFragmentModuleDescriptor)this.decoratedDescriptor;
            abstractWebFragmentModuleDescriptor.setWebInterfaceManager(webInterfaceManager);
        }
    }

    public Condition getCondition() {
        return this.decoratedDescriptor.getCondition();
    }

    public ContextProvider getContextProvider() {
        return this.decoratedDescriptor.getContextProvider();
    }

    public WebParam getWebParams() {
        return this.decoratedDescriptor.getWebParams();
    }

    public String getCompleteKey() {
        return this.decoratedDescriptor.getCompleteKey();
    }

    public String getPluginKey() {
        return this.decoratedDescriptor.getPluginKey();
    }

    public String getName() {
        return this.decoratedDescriptor.getName();
    }

    public String getDescription() {
        return this.decoratedDescriptor.getDescription();
    }

    public Class<T> getModuleClass() {
        return this.decoratedDescriptor.getModuleClass();
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        this.decoratedDescriptor.init(plugin, element);
    }

    public boolean isEnabledByDefault() {
        return this.decoratedDescriptor.isEnabledByDefault();
    }

    public boolean isSystemModule() {
        return this.decoratedDescriptor.isSystemModule();
    }

    public void destroy() {
        this.decoratedDescriptor.destroy();
    }

    public Float getMinJavaVersion() {
        return this.decoratedDescriptor.getMinJavaVersion();
    }

    public boolean satisfiesMinJavaVersion() {
        return this.decoratedDescriptor.satisfiesMinJavaVersion();
    }

    public Map<String, String> getParams() {
        return this.decoratedDescriptor.getParams();
    }

    public List<ResourceDescriptor> getResourceDescriptors() {
        return this.decoratedDescriptor.getResourceDescriptors();
    }

    public ResourceLocation getResourceLocation(String type, String name) {
        return this.decoratedDescriptor.getResourceLocation(type, name);
    }

    public ResourceDescriptor getResourceDescriptor(String type, String name) {
        return this.decoratedDescriptor.getResourceDescriptor(type, name);
    }

    public String toString() {
        return this.decoratedDescriptor.toString();
    }
}

