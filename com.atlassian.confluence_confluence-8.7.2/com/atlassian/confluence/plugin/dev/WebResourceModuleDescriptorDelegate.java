/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.dev;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dom4j.Element;

public abstract class WebResourceModuleDescriptorDelegate
extends WebResourceModuleDescriptor {
    private WebResourceModuleDescriptor delegate;

    public WebResourceModuleDescriptorDelegate(WebResourceModuleDescriptor delegate) {
        super(new ModuleFactory(){

            public <T> T createModule(String name, ModuleDescriptor<T> moduleDescriptor) throws PluginParseException {
                throw new UnsupportedOperationException();
            }
        }, null);
        this.delegate = delegate;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        this.delegate.init(plugin, element);
    }

    public Void getModule() {
        return this.delegate.getModule();
    }

    public void enabled() {
        this.delegate.enabled();
    }

    public void disabled() {
        this.delegate.disabled();
    }

    public Set<String> getContexts() {
        return this.delegate.getContexts();
    }

    public List<String> getDependencies() {
        return this.delegate.getDependencies();
    }

    public boolean isDisableMinification() {
        return this.delegate.isDisableMinification();
    }

    public boolean canEncodeStateIntoUrl() {
        return this.delegate.canEncodeStateIntoUrl();
    }

    public boolean shouldDisplay(QueryParams params) {
        return this.delegate.shouldDisplay(params);
    }

    public boolean shouldDisplayImmediate() {
        return this.delegate.shouldDisplayImmediate();
    }

    public Map<String, WebResourceDataProvider> getDataProviders() {
        return this.delegate.getDataProviders();
    }

    public void destroy() {
        this.delegate.destroy();
    }

    public boolean isEnabledByDefault() {
        return this.delegate.isEnabledByDefault();
    }

    public boolean isSystemModule() {
        return this.delegate.isSystemModule();
    }

    public String getCompleteKey() {
        return this.delegate.getCompleteKey();
    }

    public String getPluginKey() {
        return this.delegate.getPluginKey();
    }

    public String getKey() {
        return this.delegate.getKey();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public Class<Void> getModuleClass() {
        return this.delegate.getModuleClass();
    }

    public String getDescription() {
        return this.delegate.getDescription();
    }

    public Map<String, String> getParams() {
        return this.delegate.getParams();
    }

    public String getI18nNameKey() {
        return this.delegate.getI18nNameKey();
    }

    public String getDescriptionKey() {
        return this.delegate.getDescriptionKey();
    }

    public List<ResourceDescriptor> getResourceDescriptors() {
        return this.delegate.getResourceDescriptors();
    }

    public ResourceLocation getResourceLocation(String type, String name) {
        return this.delegate.getResourceLocation(type, name);
    }

    public ResourceDescriptor getResourceDescriptor(String type, String name) {
        return this.delegate.getResourceDescriptor(type, name);
    }

    public Float getMinJavaVersion() {
        return this.delegate.getMinJavaVersion();
    }

    public boolean satisfiesMinJavaVersion() {
        return this.delegate.satisfiesMinJavaVersion();
    }

    public void setPlugin(Plugin plugin) {
        this.delegate.setPlugin(plugin);
    }

    public Plugin getPlugin() {
        return this.delegate.getPlugin();
    }

    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        return this.delegate.toString();
    }
}

