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
 *  org.dom4j.Element
 */
package com.atlassian.confluence.content.persistence.hibernate;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class HibernateContentQueryModuleDescriptor
extends AbstractModuleDescriptor<HibernateContentQueryFactory>
implements PluginModuleFactory<HibernateContentQueryFactory> {
    private PluginModuleHolder<HibernateContentQueryFactory> module;
    private String queryName;

    public HibernateContentQueryModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.module = PluginModuleHolder.getInstance(this);
        this.queryName = element.attributeValue("query-name");
        if (StringUtils.isBlank((CharSequence)this.queryName)) {
            throw new PluginParseException("query-name is a required attribute for HibernateContentQueryModules");
        }
    }

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.module.disabled();
        super.disabled();
    }

    public HibernateContentQueryFactory getModule() {
        return this.module.getModule();
    }

    @Override
    public HibernateContentQueryFactory createModule() {
        return (HibernateContentQueryFactory)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }

    public String getQueryName() {
        return this.queryName;
    }

    public String toString() {
        return this.queryName + " [" + this.getCompleteKey() + "]";
    }
}

