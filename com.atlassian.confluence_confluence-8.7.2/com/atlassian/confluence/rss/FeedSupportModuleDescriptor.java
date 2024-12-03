/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.rss.RssRenderSupport;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class FeedSupportModuleDescriptor
extends AbstractModuleDescriptor<RssRenderSupport>
implements PluginModuleFactory<RssRenderSupport> {
    private PluginModuleHolder<RssRenderSupport> renderItem = PluginModuleHolder.getInstance(this);
    private String renders;

    public FeedSupportModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        Attribute att = element.attribute("renders");
        if (att == null || StringUtils.isBlank((CharSequence)att.getValue())) {
            throw new PluginParseException("Required configuration attribute missing or empty: renders");
        }
        this.renders = att.getValue();
    }

    public RssRenderSupport getModule() {
        return this.renderItem.getModule();
    }

    @Override
    public RssRenderSupport createModule() {
        return (RssRenderSupport)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }

    public void enabled() {
        super.enabled();
        this.renderItem.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.renderItem.disabled();
        super.disabled();
    }

    public String getRenders() {
        return this.renders;
    }
}

